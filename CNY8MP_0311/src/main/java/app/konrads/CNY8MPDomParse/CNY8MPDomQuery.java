package app.konrads.CNY8MPDomParse;

import org.w3c.dom.Element;

@SuppressWarnings("ALL")
public class CNY8MPDomQuery {

    public static void querySzakacsSzakkozepiskola(Element element) {
        System.out.println("Azok a szakácsok, akik végzettségei között van szakközépiskola: \n");

        var szakacsok = element.getElementsByTagName("szakacs");
        for (int i = 0; i < szakacsok.getLength(); i++) {
            var szakacs = (Element) szakacsok.item(i);
            var vegzettsegek = szakacs.getElementsByTagName("vegzettseg");

            var jo = false;
            for (int j = 0; j < vegzettsegek.getLength(); j++) {
                var vegzettseg = vegzettsegek.item(j).getTextContent();
                if (vegzettseg.toLowerCase().trim().equals("szakközépiskola"))
                    jo = true;
            }
            if (!jo)
                continue;

            System.out.println("Current element: " + szakacs.getTagName());

            var id = szakacs.getAttribute("skod");
            var etteremKulcs = szakacs.getAttribute("e_sz");
            System.out.println("Szakács ID: " + id);
            System.out.println("Étterem idegen kulcs: " + etteremKulcs);

            var nev = szakacs.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var eletkor = szakacs.getElementsByTagName("eletkor").item(0).getTextContent();
            System.out.println("Életkor: " + eletkor);

            for (int j = 0; j < vegzettsegek.getLength(); j++) {
                var vegzettseg = vegzettsegek.item(j).getTextContent();
                System.out.println("Végzettség: " + vegzettseg);
            }

            System.out.println();
        }
    }

    public static void queryEtteremOtCsillag(Element element) {
        System.out.println("Azok az éttermek, amelyek 5 csillagosak: \n");

        var etterems = element.getElementsByTagName("etterem");
        for (int i = 0; i < etterems.getLength(); i++) {
            var etterem = (Element) etterems.item(i);
            var csillag = etterem.getElementsByTagName("csillag").item(0).getTextContent();
            if (!csillag.equals("5"))
                continue;

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

            System.out.println("Csillag: " + csillag);

            System.out.println();
        }
    }

    public static void queryGyakornokDelutan(Element element) {
        System.out.println("Azok a gyakornokok, akik délutáni műszakban dolgoznak: \n");

        var gyakornokok = element.getElementsByTagName("gyakornok");
        for (int i = 0; i < gyakornokok.getLength(); i++) {
            var gyakornok = (Element) gyakornokok.item(i);
            var muszakok = gyakornok.getElementsByTagName("muszak");

            var jo = false;
            for (int j = 0; j < muszakok.getLength(); j++) {
                var muszak = muszakok.item(j).getTextContent();
                if (muszak.toLowerCase().trim().equals("délután"))
                    jo = true;
            }
            if (!jo)
                continue;

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

}
