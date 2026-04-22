db.ettermek.insertOne({
    nev: "Borkonyha",
    varos: "Budapest",
    cim: { utca: "Sas", hazszam: "3" },
    csillag: 5,
    specialitasok: ["modern magyar", "fine dining"]
})

db.szakacsok.insertOne({
    nev: "Szabo Anna",
    reszleg: "Garde Manger",
    eletkor: 32,
    fizetes: 400000,
    etterem_nev: "Borkonyha",
    vegzettsegek: ["Egyetem", "Le Cordon Bleu"]
})

db.ettermek.find({}, { nev: 1, csillag: 1, _id: 0 })

db.ettermek.find({ csillag: { $gte: 4 } })

db.ettermek.find({
    $or: [{ varos: "Budapest" }, { csillag: 5 }]
})

db.ettermek.find().sort({ csillag: -1 }).limit(3)

db.szakacsok.find({ eletkor: { $gte: 25, $lte: 40 } })

db.ettermek.updateOne(
    { nev: "Anyukam Mondta" },
    { $set: { csillag: 5 } }
)

db.ettermek.updateOne(
    { nev: "Trofea" },
    { $push: { specialitasok: "steak" } }
)

db.ettermek.deleteMany({ csillag: { $lt: 3 } })

db.ettermek.countDocuments()

db.ettermek.find({ nev: "Gundel" }).explain("executionStats")

db.ettermek.createIndex({ nev: 1 })

db.ettermek.find({ nev: "Gundel" }).explain("executionStats")

db.ettermek.createIndex({ varos: 1, csillag: -1 })

db.ettermek.getIndexes()

db.ettermek.dropIndex("nev_1")

db.ettermek.aggregate([
    {
        $group: {
            _id: "$varos",
            db_etterem: { $sum: 1 },
            atlag_csillag: { $avg: "$csillag" }
        }
    },
    { $sort: { atlag_csillag: -1 } }
])

db.szakacsok.aggregate([
    { $unwind: "$vegzettsegek" },
    {
        $group: {
            _id: "$vegzettsegek",
            szakacsok_szama: { $sum: 1 }
        }
    },
    { $sort: { szakacsok_szama: -1 } }
])

db.szakacsok.aggregate([
    {
        $lookup: {
            from: "ettermek",
            localField: "etterem_nev",
            foreignField: "nev",
            as: "etterem_adatok"
        }
    },
    { $unwind: "$etterem_adatok" },
    { $match: { "etterem_adatok.csillag": { $gte: 4 } } },
    {
        $project: {
            _id: 0,
            nev: 1,
            reszleg: 1,
            etterem: "$etterem_adatok.nev",
            csillag: "$etterem_adatok.csillag"
        }
    }
])

db.szakacsok.aggregate([
    { $sort: { eletkor: -1 } },
    {
        $group: {
            _id: "$etterem_nev",
            legidosebb_nev: { $first: "$nev" },
            legidosebb_kor: { $first: "$eletkor" }
        }
    }
])
