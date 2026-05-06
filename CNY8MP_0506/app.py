import pymongo as pymongo


# Kapcsolódás a MongoDB szerverhez
MONGO_URI = "mongodb://localhost:27017/"
mongo_client = pymongo.MongoClient(MONGO_URI)

database = mongo_client.local

# Collection-ok
restaurants_col = database["etterem"]
headchefs_col = database["foszakacs"]

# Ürítés (hogy újrafuttatásnál se duplázzon)
restaurants_col.delete_many({})
headchefs_col.delete_many({})

restaurants_seed = [
    {
        "_id": "e1",
        "nev": "Aranyhal Étterem",
        "cim": {
            "varos": "Miskolc",
            "utca": "Széchenyi u.",
            "hazszam": 107
        },
        "csillag": 3
    },
    {
        "_id": "e2",
        "nev": "Aranyhal Étterem",
        "cim": {
            "varos": "Miskolc",
            "utca": "Széchenyi u.",
            "hazszam": 107
        },
        "csillag": 4
    },
    {
        "_id": "e3",
        "nev": "Creppy Palacsintaház Étterem",
        "cim": {
            "varos": "Miskolc",
            "utca": "Méylvölgy utca",
            "hazszam": 15
        },
        "csillag": 5
    }
]

# Főszakácsok hozzáadása
headchefs_seed = [
    {
        "_id": "f1",
        "e_f": "e1",
        "nev": "Fő István",
        "eletkor": 45,
        "vegzettseg": ["Szakközépiskola", "Főiskola"]
    },
    {
        "_id": "f2",
        "e_f": "e2",
        "nev": "Kovács János",
        "eletkor": 38,
        "vegzettseg": ["Szakközépiskola", "Főiskola"]
    },
    {
        "_id": "f3",
        "e_f": "e3",
        "nev": "Nemes Géza",
        "eletkor": 28,
        "vegzettseg": ["Főiskola"]
    }
]

restaurants_col.insert_many(restaurants_seed)
print("Éttermek feltöltve.")

headchefs_col.insert_many(headchefs_seed)
print("Főszakácsok feltöltve.")






# Éttermek lekérdezése
print("\n--- 2.a) Éttermek (mind) ---")
for restaurant in restaurants_col.find():
    print(restaurant)

# Főszakácsok lekérdezése
print("\n--- 2.a) Főszakácsok (mind) ---")
for chef in headchefs_col.find():
    print(chef)

# Konkrét étterem ID alapján
print("\n--- 2.b) Étterem keresése: _id = e2 ---")
restaurant_e2 = restaurants_col.find_one({"_id": "e2"})
print(restaurant_e2)

# 4 csillag alatti/egyenlő éttermek
print("\n--- 2.c) Éttermek, ahol csillag <= 4 ---")
for restaurant in restaurants_col.find({"csillag": {"$lte": 4}}):
    print(restaurant)

# Főszakácsok átlagéletkora
print("\n--- 2.d) Főszakácsok átlagéletkora ---")
avg_age_pipeline = [
    {
        "$group": {
            "_id": None,
            "atlagEletkor": {"$avg": "$eletkor"}
        }
    }
]
avg_age_result = list(headchefs_col.aggregate(avg_age_pipeline))
avg_age = avg_age_result[0]["atlagEletkor"]
print(f"Átlagéletkor: {avg_age:.2f} év")



print("\n--- Szakközépiskolát végzett főszakácsok + éttermük ---")
lookup_pipeline = [
    {
        "$match": {
            "vegzettseg": "Szakközépiskola"
        }
    },
    {
        "$lookup": {
            "from": "etterem",
            "localField": "e_f",
            "foreignField": "_id",
            "as": "etterem_adatok"
        }
    }
]

for row in headchefs_col.aggregate(lookup_pipeline):
    restaurant_name = row["etterem_adatok"][0]["nev"] if row["etterem_adatok"] else "Nincs adat"
    print(f"Főszakács: {row['nev']} | Étterem: {restaurant_name}")

# Módosítás
print("\n--- 3.a) e1 csillag értékének módosítása (3 -> 4) ---")
restaurants_col.update_one({"_id": "e1"}, {"$set": {"csillag": 4}})

print("\n--- 3.a) Éttermek módosítás után ---")
for restaurant in restaurants_col.find():
    print(restaurant)



# Törlések
print("\n--- 4.a) Főszakács törlése: _id = f2 ---")
headchefs_col.delete_one({"_id": "f2"})

print("\n--- Ellenőrzés: megmaradt főszakácsok ---")
for chef in headchefs_col.find():
    print(chef)

print("\n--- 4.b) 30 év alatti főszakácsok törlése ---")
delete_result = headchefs_col.delete_many({"eletkor": {"$lt": 30}})
print(f"Törölve: {delete_result.deleted_count} db")

print("\n--- Ellenőrzés: megmaradt főszakácsok ---")
for chef in headchefs_col.find():
    print(chef)
