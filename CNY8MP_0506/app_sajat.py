import pymongo as pymongo


# Kapcsolódás a MongoDB szerverhez
MONGO_URI = "mongodb://localhost:27017/"
mongo_client = pymongo.MongoClient(MONGO_URI)

database = mongo_client.local

# Collection-ok
stores_col = database["kereskedesek"]
employees_col = database["alkalmazottak"]

# Ürítés (újrafuttatásnál ne duplázzon)
stores_col.delete_many({})
employees_col.delete_many({})

stores_seed = [
    {
        "k_id": "k1",
        "cim": {
            "iranyitoszam": "1234",
            "varos": "Budapest",
            "utca_hazszam": "Vaci utca 10.",
        },
        "dolgozo_azonositok": ["al1", "al2"],
    },
    {
        "k_id": "k2",
        "cim": {
            "iranyitoszam": "5678",
            "varos": "Debrecen",
            "utca_hazszam": "Kossuth utca 5.",
        },
        "dolgozo_azonositok": ["al3", "al4"],
    },
]

employees_seed = [
    {
        "a_id": "al1",
        "nev": "Juhasz Peter",
        "lakcim": "1234, Budapest, Fo utca 15.",
        "telefonszam": ["+36 1 123 4567", "+36 30 987 6543"],
        "fizetes": 350000,
        "kereskedes_id": "k1",
    },
    {
        "a_id": "al2",
        "nev": "Kiss Anna",
        "lakcim": "5678, Debrecen, Kossuth utca 10.",
        "telefonszam": ["+36 52 123 456", "+36 30 987 654"],
        "fizetes": 400000,
        "kereskedes_id": "k1",
    },
    {
        "a_id": "al3",
        "nev": "Nagy Laszlo",
        "lakcim": "4321, Szeged, Raktar utca 5.",
        "telefonszam": ["+36 62 123 456", "+36 30 987 654"],
        "fizetes": 300000,
        "kereskedes_id": "k2",
    },
    {
        "a_id": "al4",
        "nev": "Horvath Eva",
        "lakcim": "8765, Pecs, Raktar utca 8.",
        "telefonszam": ["+36 72 123 456", "+36 30 987 654"],
        "fizetes": 320000,
        "kereskedes_id": "k2",
    },
]

stores_col.insert_many(stores_seed)
print("Kereskedések feltöltve.")

employees_col.insert_many(employees_seed)
print("Alkalmazottak feltöltve.")


# 2.a) Listázás
print("\n--- 2.a) Összes kereskedés ---")
for store in stores_col.find():
    print(store)

print("\n--- 2.a) Összes alkalmazott ---")
for emp in employees_col.find():
    print(emp)


# 2.b) Konkrét rekordok ID alapján
print("\n--- 2.b) Kereskedés keresése: k_id = k1 ---")
store_k1 = stores_col.find_one({"k_id": "k1"})
print(store_k1)

print("\n--- 2.b) Alkalmazott keresése: a_id = al2 ---")
emp_al2 = employees_col.find_one({"a_id": "al2"})
print(emp_al2)


# 2.c) Szűrés: fizetés >= 320000
print("\n--- 2.c) Alkalmazottak, ahol fizetes >= 320000 ---")
for emp in employees_col.find({"fizetes": {"$gte": 320000}}):
    print(emp)


# 2.d) Átlagfizetés aggregálással
print("\n--- 2.d) Alkalmazottak átlagfizetése ---")
avg_salary_pipeline = [
    {
        "$group": {
            "_id": None,
            "atlagFizetes": {"$avg": "$fizetes"},
        }
    }
]

avg_salary_result = list(employees_col.aggregate(avg_salary_pipeline))
avg_salary = avg_salary_result[0]["atlagFizetes"]
print(f"Átlagfizetés: {avg_salary:.0f} Ft")


# 2.e) Lookup: alkalmazott + kereskedés címe
print("\n--- 2.e) Alkalmazottak és a kereskedés városa (lookup) ---")
join_pipeline = [
    {
        "$lookup": {
            "from": "kereskedesek",
            "localField": "kereskedes_id",
            "foreignField": "k_id",
            "as": "kereskedes_adatok",
        }
    }
]

for row in employees_col.aggregate(join_pipeline):
    city = (
        row["kereskedes_adatok"][0]["cim"]["varos"]
        if row.get("kereskedes_adatok")
        else "Nincs adat"
    )
    print(f"{row['nev']} -> {city}")


# 3.a) Módosítás: al1 fizetésének emelése
print("\n--- 3.a) Fizetés módosítása: a_id = al1 (350000 -> 360000) ---")
employees_col.update_one({"a_id": "al1"}, {"$set": {"fizetes": 360000}})

print("\n--- 3.a) Alkalmazottak módosítás után ---")
for emp in employees_col.find():
    print(emp)


# 4.a) Törlés: konkrét alkalmazott
print("\n--- 4.a) Alkalmazott törlése: a_id = al4 ---")
employees_col.delete_one({"a_id": "al4"})

print("\n--- Ellenőrzés: megmaradt alkalmazottak ---")
for emp in employees_col.find():
    print(emp)


# 4.b) Törlés: 320000 alatti fizetésűek
print("\n--- 4.b) 320000 Ft alatti fizetésű alkalmazottak törlése ---")
delete_result = employees_col.delete_many({"fizetes": {"$lt": 320000}})
print(f"Törölve: {delete_result.deleted_count} db")

print("\n--- Ellenőrzés: megmaradt alkalmazottak ---")
for emp in employees_col.find():
    print(emp)
