package app.konrads;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String connectionString = "mongodb://localhost:27017";

        MongoClient client = MongoClients.create(connectionString);
        doDatabaseOperations(client);
    }

    private static void doDatabaseOperations(MongoClient client) {
        System.out.println("Connected to MongoDB successfully!");

        MongoDatabase database = client.getDatabase("vendeglatas");
        MongoCollection<Document> ettermek = database.getCollection("ettermek");

        Document newEtterem1 = new Document("nev", "Pizzéria")
                .append("varos", "Szeged")
                .append("cim", new Document("utca", "Kossuth Lajos").append("hazszam", "5"))
                .append("csillag", 3)
                .append("specialitasok", Arrays.asList("olasz", "nemzetkozi"));

        Document newEtterem2 = new Document("nev", "Söröző")
                .append("varos", "Debrecen")
                .append("cim", new Document("utca", "Piac").append("hazszam", "10"))
                .append("csillag", 2)
                .append("specialitasok", Arrays.asList("magyar", "nemzetkozi"));

        Document newEtterem3 = new Document("nev", "Kávézó")
                .append("varos", "Pécs")
                .append("cim", new Document("utca", "Rákóczi").append("hazszam", "20"))
                .append("csillag", 4)
                .append("specialitasok", Arrays.asList("kávé", "desszert"));

        ettermek.insertOne(newEtterem1);
        ettermek.insertOne(newEtterem2);
        ettermek.insertOne(newEtterem3);

        // Kérdezzük le az összes éttermet
        System.out.println("Az összes étterem:");
        for (Document doc : ettermek.find()) {
            System.out.println(doc.toJson());
        }

        // Kérdezzük le a 3 csillagos éttermeket
        System.out.println("\n3 csillagos éttermek:");
        for (Document doc : ettermek.find(new Document("csillag", 3))) {
            System.out.println(doc.toJson());
        }

        MongoCollection<Document> foszakacsok = database.getCollection("foszakacsok");
        // Minden főszakács aki idősebb mint 32 éves
        System.out.println("\n32 évnél idősebb főszakácsok:");
        for (Document doc : foszakacsok.find(new Document("eletkor", new Document("$gt", 32)))) {
            System.out.println(doc.toJson());
        }

        // Szakács aki 40 éves fölötti és az étterme
        System.out.println("\n40 évnél idősebb főszakácsok és az éttermük:");
        for (Document doc : foszakacsok.find(new Document("eletkor", new Document("$gt", 40)))) {
            System.out.println(doc.toJson());
            String etteremNev = doc.getString("etterem_new");
            Document etterem = ettermek.find(new Document("nev", etteremNev)).first();
            if (etterem != null) {
                System.out.println("Éttermük: " + etterem.toJson());
            }
        }

        // Átlagéletkor főszakácsok között
        Document avgAge = foszakacsok.aggregate(Arrays.asList(
                new Document("$group", new Document("_id", null).append("avgEletkor", new Document("$avg", "$eletkor")))
        )).first();
        if (avgAge != null) {
            System.out.println("\nFőszakácsok átlagéletkora: " + avgAge.getDouble("avgEletkor"));
        }
    }
}