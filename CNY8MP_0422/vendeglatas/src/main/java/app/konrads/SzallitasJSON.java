package app.konrads;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SzallitasJSON {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(new File("../sajat.json"));
        JsonNode uzeletek = root.get("uzeletek");

        JsonNode kereskedesek = uzeletek.get("kereskedes");
        JsonNode alkalmazottak = uzeletek.get("alkalmazott");
        JsonNode aruk = uzeletek.get("aru");
        JsonNode rendelesek = uzeletek.get("rendeles");
        JsonNode vasarlok = uzeletek.get("vasarlo");
        JsonNode fuvarozok = uzeletek.get("fuvarozo");
        JsonNode arRendeles = uzeletek.get("a_r");

        System.out.println("=== Kereskedesek es dolgozok ===");
        for (JsonNode k : kereskedesek) {
            String kId = k.get("k_id").asText();
            String varos = k.path("cim").path("varos").asText();
            String utcaHazszam = k.path("cim").path("utca_hazszam").asText();
            System.out.println("Kereskedes: " + kId + " | Cim: " + varos + ", " + utcaHazszam);
            for (JsonNode d : k.path("dolgozo")) {
                String alkalmazottId = d.path("a_fid").asText();
                String nev = findNameById(alkalmazottak, "a_id", alkalmazottId, "nev");
                System.out.println(" - Dolgozo: " + nev + " (" + alkalmazottId + ")");
            }
        }

        System.out.println("\n=== Atlagos rendelesi ertek ===");
        double osszeg = 0;
        int db = 0;
        for (JsonNode r : rendelesek) {
            osszeg += r.path("teljes_ar").asDouble();
            db++;
        }
        System.out.println("Atlag: " + (db == 0 ? 0 : (osszeg / db)) + " Ft");

        System.out.println("\n=== Teljesult rendelesek ===");
        for (JsonNode r : rendelesek) {
            if ("igen".equalsIgnoreCase(r.path("teljesult").asText())) {
                System.out.println(" - " + r.path("re_id").asText() + " | ertek: " + r.path("teljes_ar").asText() + " Ft");
            }
        }

        System.out.println("\n=== Ki, mit rendelt es ki szallitotta? ===");
        for (JsonNode r : rendelesek) {
            String rendelesId = r.path("re_id").asText();
            String vasarloId = r.path("v_fid").asText();
            String fuvarozoId = r.path("f_fid").asText();

            String vasarloNev = findNameById(vasarlok, "v_id", vasarloId, "nev");
            String fuvarozoNev = findNameById(fuvarozok, "f_id", fuvarozoId, "nev");

            String etelNev = "ismeretlen aru";
            for (JsonNode ar : arRendeles) {
                if (rendelesId.equals(ar.path("re_fid").asText())) {
                    etelNev = findNameById(aruk, "a_id", ar.path("a_fid").asText(), "nev");
                    break;
                }
            }

            System.out.println(" - " + vasarloNev + " rendelte: " + etelNev + " | szallito: " + fuvarozoNev);
        }

        System.out.println("\n=== VIP vasarlo (legtobbet kolto) ===");
        Map<String, Double> koltes = new HashMap<>();
        for (JsonNode r : rendelesek) {
            String vasarloId = r.path("v_fid").asText();
            koltes.put(vasarloId, koltes.getOrDefault(vasarloId, 0.0) + r.path("teljes_ar").asDouble());
        }

        String vipId = "";
        double max = -1;
        for (Map.Entry<String, Double> e : koltes.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                vipId = e.getKey();
            }
        }
        String vipNev = findNameById(vasarlok, "v_id", vipId, "nev");
        System.out.println("VIP: " + vipNev + " | osszes koltes: " + max + " Ft");

        System.out.println("\n=== Uj JSON fajl: bevetel kereskedesenkent ===");
        ArrayNode ujLista = mapper.createArrayNode();
        for (JsonNode k : kereskedesek) {
            String kId = k.path("k_id").asText();
            double bevetel = 0;
            for (JsonNode r : rendelesek) {
                if (kId.equals(r.path("k_fid").asText())) {
                    bevetel += r.path("teljes_ar").asDouble();
                }
            }

            ObjectNode rekord = mapper.createObjectNode();
            rekord.put("kereskedes_id", kId);
            rekord.put("bevetel", bevetel);
            ujLista.add(rekord);
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File("szallitas_bevetel.json"), ujLista);
        System.out.println("Fajl kiirva: szallitas_bevetel.json");
    }

    private static String findNameById(JsonNode tomb, String idField, String idValue, String nameField) {
        for (JsonNode node : tomb) {
            if (idValue.equals(node.path(idField).asText())) {
                return node.path(nameField).asText();
            }
        }
        return "ismeretlen";
    }
}