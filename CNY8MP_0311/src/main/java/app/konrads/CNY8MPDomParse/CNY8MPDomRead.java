package app.konrads.CNY8MPDomParse;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

@SuppressWarnings("ALL")
public class CNY8MPDomRead {
    public static void main(String[] args) {
        var filePath = "./CNY8MP_XML.xml";
        var file = new File(filePath);

        Document document = null;
        try {
            document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(file);
        } catch (Exception e) {
            System.err.println("Error parsing XML file: " + e.getMessage());
            return;
        }
        document.normalize();

        var root = document.getDocumentElement();
        if (root == null) {
            System.err.println("Error: XML document has no root element.");
            return;
        }
        System.out.println("Root element: " + root.getTagName() + "\n");

        readEtterem(root);
        readFoszakacs(root);
        readSzakacs(root);
        readGyakornok(root);
        readVendeg(root);
        readRendeles(root);

        CNY8MPDomQuery.querySzakacsSzakkozepiskola(root);
        CNY8MPDomQuery.queryEtteremOtCsillag(root);
        CNY8MPDomQuery.queryGyakornokDelutan(root);
    }

    public static void readEtterem(Element element) {
        var etterems = element.getElementsByTagName("etterem");
        for (int i = 0; i < etterems.getLength(); i++) {
            var etterem = (Element) etterems.item(i);
            System.out.println("Current element: " + etterem.getTagName());

            var id = etterem.getAttribute("ekod");
            System.out.println("Étterem ID: " + id);

            var nev = etterem.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var cim = etterem.getElementsByTagName("cim").item(0);
            var varos = ((Element) cim).getElementsByTagName("varos").item(0).getTextContent();
            var utca = ((Element) cim).getElementsByTagName("utca").item(0).getTextContent();
            var hazszam = ((Element) cim).getElementsByTagName("hazszam").item(0).getTextContent();
            var cimSzoveg = String.format("%s %s %s", varos, utca, hazszam);
            System.out.println("Cím: " + cimSzoveg);

            var csillag = etterem.getElementsByTagName("csillag").item(0).getTextContent();
            System.out.println("Csillag: " + csillag);

            System.out.println();
        }
    }

    public static void readFoszakacs(Element element) {
        var foszakacsok = element.getElementsByTagName("foszakacs");
        for (int i = 0; i < foszakacsok.getLength(); i++) {
            var foszakacs = (Element) foszakacsok.item(i);
            System.out.println("Current element: " + foszakacs.getTagName());

            var id = foszakacs.getAttribute("fkod");
            System.out.println("Főszakács ID: " + id);

            var nev = foszakacs.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var eletkor = foszakacs.getElementsByTagName("eletkor").item(0).getTextContent();
            System.out.println("Életkor: " + eletkor);

            var vegzettsegek = foszakacs.getElementsByTagName("vegzettseg");
            System.out.print("Végzettségek: ");
            for (int j = 0; j < vegzettsegek.getLength(); j++) {
                var vegzettseg = vegzettsegek.item(j).getTextContent();
                System.out.print(vegzettseg);
                if (j < vegzettsegek.getLength() - 1) {
                    System.out.print(", ");
                }
            }

            System.out.println("\n");
        }
    }

    public static void readSzakacs(Element element) {
        var szakacsok = element.getElementsByTagName("szakacs");
        for (int i = 0; i < szakacsok.getLength(); i++) {
            var szakacs = (Element) szakacsok.item(i);
            System.out.println("Current element: " + szakacs.getTagName());

            var id = szakacs.getAttribute("skod");
            var etteremKulcs = szakacs.getAttribute("e_sz");
            System.out.println("Szakács ID: " + id);
            System.out.println("Étterem idegen kulcs: " + etteremKulcs);

            var nev = szakacs.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var eletkor = szakacs.getElementsByTagName("eletkor").item(0).getTextContent();
            System.out.println("Életkor: " + eletkor);

            var vegzettseg = szakacs.getElementsByTagName("vegzettseg").item(0).getTextContent();
            System.out.println("Végzettség: " + vegzettseg);

            System.out.println();
        }
    }

    public static void readGyakornok(Element element) {
        var gyakornokok = element.getElementsByTagName("gyakornok");
        for (int i = 0; i < gyakornokok.getLength(); i++) {
            var gyakornok = (Element) gyakornokok.item(i);
            System.out.println("Current element: " + gyakornok.getTagName());

            var id = gyakornok.getAttribute("gykod");
            var etteremKulcs = gyakornok.getAttribute("e_gy");
            System.out.println("Gyakornok ID: " + id);
            System.out.println("Étterem idegen kulcs: " + etteremKulcs);

            var nev = gyakornok.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var gyakorlat = (Element) gyakornok.getElementsByTagName("gyakorlat").item(0);
            var kezdete = gyakorlat.getElementsByTagName("kezdete").item(0).getTextContent();
            var idotartama = gyakorlat.getElementsByTagName("idotartama").item(0).getTextContent();
            System.out.println("Gyakorlat kezdete: " + kezdete);
            System.out.println("Gyakorlat időtartama: " + idotartama);

            var muszakok = gyakornok.getElementsByTagName("muszak");
            System.out.print("Műszakok: ");
            for (int j = 0; j < muszakok.getLength(); j++) {
                var muszak = muszakok.item(j).getTextContent();
                System.out.print(muszak);
                if (j < muszakok.getLength() - 1) {
                    System.out.print(", ");
                }
            }

            System.out.println("\n");
        }
    }

    public static void readVendeg(Element element) {
        var vendegok = element.getElementsByTagName("vendeg");
        for (int i = 0; i < vendegok.getLength(); i++) {
            var vendeg = (Element) vendegok.item(i);
            System.out.println("Current element: " + vendeg.getTagName());

            var id = vendeg.getAttribute("vkod");
            System.out.println("Vendég ID: " + id);

            var nev = vendeg.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var eletkor = vendeg.getElementsByTagName("eletkor").item(0).getTextContent();
            System.out.println("Életkor: " + eletkor);

            var cim = vendeg.getElementsByTagName("cim").item(0);
            var varos = ((Element) cim).getElementsByTagName("varos").item(0).getTextContent();
            var utca = ((Element) cim).getElementsByTagName("utca").item(0).getTextContent();
            var hazszam = ((Element) cim).getElementsByTagName("hazszam").item(0).getTextContent();
            var cimSzoveg = String.format("%s %s %s", varos, utca, hazszam);
            System.out.println("Cím: " + cimSzoveg);

            System.out.println();
        }
    }

    public static void readRendeles(Element element) {
        var rendelesek = element.getElementsByTagName("rendeles");
        for (int i = 0; i < rendelesek.getLength(); i++) {
            var rendeles = (Element) rendelesek.item(i);
            System.out.println("Current element: " + rendeles.getTagName());

            var vendegKulcs = rendeles.getAttribute("e_v_v");
            var etteremKulcs = rendeles.getAttribute("e_v_e");
            System.out.println("Vendég idegen kulcs: " + vendegKulcs);
            System.out.println("Étterem idegen kulcs: " + etteremKulcs);

            var osszeg = rendeles.getElementsByTagName("osszeg").item(0).getTextContent();
            System.out.println("Összeg: " + osszeg);

            var etel = rendeles.getElementsByTagName("etel").item(0).getTextContent();
            System.out.println("Étel: " + etel);

            System.out.println();
        }
    }
}