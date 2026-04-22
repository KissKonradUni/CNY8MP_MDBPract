db.kereskedesek.find({}, { _id: 0, k_id: 1, "cim.varos": 1 })

db.alkalmazottak.find({ fizetes: { $gte: 350000 } }, { _id: 0, nev: 1, fizetes: 1, kereskedes_id: 1 })

db.alkalmazottak.updateOne(
  { a_id: "al3" },
  { $set: { fizetes: 330000 } }
)

db.alkalmazottak.aggregate([
  {
    $group: {
      _id: "$kereskedes_id",
      dolgozok_szama: { $sum: 1 },
      atlag_fizetes: { $avg: "$fizetes" }
    }
  },
  { $sort: { atlag_fizetes: -1 } }
])

db.alkalmazottak.aggregate([
  {
    $lookup: {
      from: "kereskedesek",
      localField: "kereskedes_id",
      foreignField: "k_id",
      as: "kereskedes_adat"
    }
  },
  { $unwind: "$kereskedes_adat" },
  {
    $project: {
      _id: 0,
      alkalmazott: "$nev",
      kereskedes: "$kereskedes_adat.k_id",
      varos: "$kereskedes_adat.cim.varos",
      fizetes: 1
    }
  }
])