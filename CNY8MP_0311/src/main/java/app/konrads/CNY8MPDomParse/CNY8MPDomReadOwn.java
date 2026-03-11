package app.konrads.CNY8MPDomParse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

@SuppressWarnings("ALL")
public class CNY8MPDomReadOwn {
    public static void main(String[] args) {
        var filePath = "./CNY8MP_XML_hazifeladat.xml";
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

        readKereskedes(root);
        readRaktar(root);
        readBeszallito(root);
        readAru(root);
        readLeltar(root);
        readRendeles(root);
        readAR(root);
        readVasarlo(root);
        readFuvarozo(root);
        readAlkalmazott(root);
    }

    public static void readKereskedes(Element element) {
        var kereskedesek = element.getElementsByTagName("kereskedes");
        for (int i = 0; i < kereskedesek.getLength(); i++) {
            var kereskedes = (Element) kereskedesek.item(i);
            System.out.println("Current element: " + kereskedes.getTagName());

            var id = kereskedes.getAttribute("k_id");
            System.out.println("Kereskedés ID: " + id);

            var cim = kereskedes.getElementsByTagName("cim").item(0);
            var iranyitoszam = ((Element) cim).getElementsByTagName("iranyitoszam").item(0).getTextContent();
            var varos = ((Element) cim).getElementsByTagName("varos").item(0).getTextContent();
            var utcaHazszam = ((Element) cim).getElementsByTagName("utca-hazszam").item(0).getTextContent();
            var cimSzoveg = String.format("%s %s %s", iranyitoszam, varos, utcaHazszam);
            System.out.println("Cím: " + cimSzoveg);

            var dolgozok = kereskedes.getElementsByTagName("dolgozo");
            System.out.print("Dolgozók: ");
            for (int j = 0; j < dolgozok.getLength(); j++) {
                var dolgozo = (Element) dolgozok.item(j);
                var a_fid = dolgozo.getAttribute("a_fid");
                System.out.print(a_fid + " ");
            }
            System.out.println("\n");
        }
    }

    public static void readRaktar(Element element) {
        var raktarak = element.getElementsByTagName("raktar");
        for (int i = 0; i < raktarak.getLength(); i++) {
            var raktar = (Element) raktarak.item(i);
            System.out.println("Current element: " + raktar.getTagName());

            var id = raktar.getAttribute("r_id");
            System.out.println("Raktár ID: " + id);

            var cim = raktar.getElementsByTagName("cim").item(0);
            var iranyitoszam = ((Element) cim).getElementsByTagName("iranyitoszam").item(0).getTextContent();
            var varos = ((Element) cim).getElementsByTagName("varos").item(0).getTextContent();
            var utcaHazszam = ((Element) cim).getElementsByTagName("utca-hazszam").item(0).getTextContent();
            var cimSzoveg = String.format("%s %s %s", iranyitoszam, varos, utcaHazszam);
            System.out.println("Cím: " + cimSzoveg);

            System.out.println();
        }
    }

    public static void readBeszallito(Element element) {
        var beszallitok = element.getElementsByTagName("beszallito");
        for (int i = 0; i < beszallitok.getLength(); i++) {
            var beszallito = (Element) beszallitok.item(i);
            System.out.println("Current element: " + beszallito.getTagName());

            var r_fid = beszallito.getAttribute("r_fid");
            var k_fid = beszallito.getAttribute("k_fid");
            System.out.println("Raktár idegen kulcs: " + r_fid);
            System.out.println("Kereskedés idegen kulcs: " + k_fid);

            System.out.println();
        }
    }

    public static void readAru(Element element) {
        var aruk = element.getElementsByTagName("aru");
        for (int i = 0; i < aruk.getLength(); i++) {
            var aru = (Element) aruk.item(i);
            System.out.println("Current element: " + aru.getTagName());

            var id = aru.getAttribute("a_id");
            System.out.println("Áru ID: " + id);

            var nev = aru.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var nettoAr = aru.getElementsByTagName("netto-ar").item(0).getTextContent();
            System.out.println("Nettó ár: " + nettoAr);

            var afa = aru.getElementsByTagName("afa").item(0).getTextContent();
            System.out.println("ÁFA: " + afa);

            System.out.println();
        }
    }

    public static void readLeltar(Element element) {
        var leltarak = element.getElementsByTagName("leltar");
        for (int i = 0; i < leltarak.getLength(); i++) {
            var leltar = (Element) leltarak.item(i);
            System.out.println("Current element: " + leltar.getTagName());

            var a_fid = leltar.getAttribute("a_fid");
            var r_fid = leltar.getAttribute("r_fid");
            System.out.println("Áru idegen kulcs: " + a_fid);
            System.out.println("Raktár idegen kulcs: " + r_fid);

            var darabszam = leltar.getElementsByTagName("darabszam").item(0).getTextContent();
            System.out.println("Darabszám: " + darabszam);

            System.out.println();
        }
    }

    public static void readRendeles(Element element) {
        var rendelesek = element.getElementsByTagName("rendeles");
        for (int i = 0; i < rendelesek.getLength(); i++) {
            var rendeles = (Element) rendelesek.item(i);
            System.out.println("Current element: " + rendeles.getTagName());

            var id = rendeles.getAttribute("re_id");
            var f_fid = rendeles.getAttribute("f_fid");
            var v_fid = rendeles.getAttribute("v_fid");
            var k_fid = rendeles.getAttribute("k_fid");
            System.out.println("Rendelés ID: " + id);
            System.out.println("Fuvarozó idegen kulcs: " + f_fid);
            System.out.println("Vásárló idegen kulcs: " + v_fid);
            System.out.println("Kereskedés idegen kulcs: " + k_fid);

            var teljesult = rendeles.getElementsByTagName("teljesült").item(0).getTextContent();
            System.out.println("Teljesült: " + teljesult);

            var datumIdo = rendeles.getElementsByTagName("datum-ido").item(0).getTextContent();
            System.out.println("Dátum és idő: " + datumIdo);

            var teljesAr = rendeles.getElementsByTagName("teljes-ar").item(0).getTextContent();
            System.out.println("Teljes ár: " + teljesAr);

            System.out.println();
        }
    }

    public static void readAR(Element element) {
        var arRendelesek = element.getElementsByTagName("a-r");
        for (int i = 0; i < arRendelesek.getLength(); i++) {
            var arRendeles = (Element) arRendelesek.item(i);
            System.out.println("Current element: " + arRendeles.getTagName());

            var a_fid = arRendeles.getAttribute("a_fid");
            var re_fid = arRendeles.getAttribute("re_fid");
            System.out.println("Áru idegen kulcs: " + a_fid);
            System.out.println("Rendelés idegen kulcs: " + re_fid);

            var darabszam = arRendeles.getElementsByTagName("darabszam").item(0).getTextContent();
            System.out.println("Darabszám: " + darabszam);

            System.out.println();
        }
    }

    public static void readVasarlo(Element element) {
        var vasarlok = element.getElementsByTagName("vasarlo");
        for (int i = 0; i < vasarlok.getLength(); i++) {
            var vasarlo = (Element) vasarlok.item(i);
            System.out.println("Current element: " + vasarlo.getTagName());

            var id = vasarlo.getAttribute("v_id");
            System.out.println("Vásárló ID: " + id);

            var nev = vasarlo.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var cim = vasarlo.getElementsByTagName("cim").item(0);
            var iranyitoszam = ((Element) cim).getElementsByTagName("iranyitoszam").item(0).getTextContent();
            var varos = ((Element) cim).getElementsByTagName("varos").item(0).getTextContent();
            var utcaHazszam = ((Element) cim).getElementsByTagName("utca-hazszam").item(0).getTextContent();
            var cimSzoveg = String.format("%s %s %s", iranyitoszam, varos, utcaHazszam);
            System.out.println("Cím: " + cimSzoveg);

            System.out.println();
        }
    }

    public static void readFuvarozo(Element element) {
        var fuvarozok = element.getElementsByTagName("fuvarozo");
        for (int i = 0; i < fuvarozok.getLength(); i++) {
            var fuvarozo = (Element) fuvarozok.item(i);
            System.out.println("Current element: " + fuvarozo.getTagName());

            var id = fuvarozo.getAttribute("f_id");
            System.out.println("Fuvarozó ID: " + id);

            var nev = fuvarozo.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var dij = fuvarozo.getElementsByTagName("dij").item(0).getTextContent();
            System.out.println("Díj: " + dij);

            System.out.println();
        }
    }

    public static void readAlkalmazott(Element element) {
        var alkalmazottak = element.getElementsByTagName("alkalmazott");
        for (int i = 0; i < alkalmazottak.getLength(); i++) {
            var alkalmazott = (Element) alkalmazottak.item(i);
            System.out.println("Current element: " + alkalmazott.getTagName());

            var id = alkalmazott.getAttribute("a_id");
            System.out.println("Alkalmazott ID: " + id);

            var nev = alkalmazott.getElementsByTagName("nev").item(0).getTextContent();
            System.out.println("Név: " + nev);

            var lakcim = alkalmazott.getElementsByTagName("lakcim").item(0).getTextContent();
            System.out.println("Lakcím: " + lakcim);

            var telefonszamok = alkalmazott.getElementsByTagName("telefonszam");
            System.out.print("Telefonszámok: ");
            for (int j = 0; j < telefonszamok.getLength(); j++) {
                var telefonszam = telefonszamok.item(0).getTextContent();
                System.out.print("(" + telefonszam + ") ");
            }
            System.out.println();

            var fizetes = alkalmazott.getElementsByTagName("fizetes").item(0).getTextContent();
            System.out.println("Fizetés: " + fizetes);

            System.out.println();
        }
    }
}
