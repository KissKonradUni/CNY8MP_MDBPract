package app.konrads.CNY8MPDomParse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * c.) Adatkiírás
 * A Java nyelv és a DOM API szabvány elemeit használva az XML feldolgozási utasításra és
 * minden csomópontra építse fel az XML dokumentumot, majd írja ki konzolra és fájlba.
 * File name: NeptunkodXML1.xml
 *
 * (Kódból készíts egy helyes XML dokumentumot ami ugyanazt a sémát (CNY8MP_XML.xsd) követi,
 * mint a CNY8MP_XML.xml fájl.
 */
public class CNY8MPDomWrite {

    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
        var filePath = "./CNY8MP_XML1.xml";
        var file = new File(filePath);

        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument();

        var root = document.createElement("vendeglatas");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:noNamespaceSchemaLocation", "CNY8MP_XML.xsd");
        document.appendChild(root);

        createEtterem(root, "Étterem 01", "Budapest", "Széchenyi István tér", "7", 5);
        createEtterem(root, "Étterem 02", "Debrecen", "Kossuth Lajos utca", "12", 4);

        createFoszakacs(root, "Főszakács 01", 45, "e1", "Szakácsiskola", "Szakközépiskola");
        createFoszakacs(root, "Főszakács 02", 38, "e2", "Szakácsiskola", "Főiskola");

        createSzakacs(root, "Szakács 01", 30, "e1", "Szakácsiskola");
        createSzakacs(root, "Szakács 02", 28, "e2", "Szakácsiskola", "Szakközépiskola");

        createGyakornok(root, "Gyakornok 01", "e1", "2026-01-01", "6 hónap", "Reggel", "Délután");
        createGyakornok(root, "Gyakornok 02", "e2", "2026-02-01", "3 hónap", "Délután", "Este");

        createVendeg(root, "Vendég 01", 35, "Budapest", "Andrássy út", "10");
        createVendeg(root, "Vendég 02", 42, "Debrecen", "Piac utca", "5");

        createRendeles(root, "v1", "e1", "Gulyásleves", 1500);
        createRendeles(root, "v2", "e2", "Lángos", 1200);

        Transformer transformer = TransformerFactory
            .newInstance()
            .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);
        transformer.transform(source, new StreamResult(System.out));
        transformer.transform(source, new StreamResult(file));
    }

    static int etteremIdCounter = 1;
    private static void createEtterem(Element root, String nev, String varos, String utca, String hazszam, int csillag) {
        var document = root.getOwnerDocument();
        var etterem = document.createElement("etterem");
        etterem.setAttribute("ekod", "e" + etteremIdCounter++);
        root.appendChild(etterem);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        etterem.appendChild(nevElem);

        var cim = document.createElement("cim");
        var varosElem = document.createElement("varos");
        varosElem.setTextContent(varos);
        var utcaElem = document.createElement("utca");
        utcaElem.setTextContent(utca);
        var hazszamElem = document.createElement("hazszam");
        hazszamElem.setTextContent(hazszam);

        cim.appendChild(varosElem);
        cim.appendChild(utcaElem);
        cim.appendChild(hazszamElem);

        etterem.appendChild(cim);

        var csillagElem = document.createElement("csillag");
        csillagElem.setTextContent(String.valueOf(csillag));
        etterem.appendChild(csillagElem);
    }

    static int foszakacsIdCounter = 1;
    private static void createFoszakacs(Element root, String nev, int eletkor, String etteremId, String... vegzettsegek) {
        var document = root.getOwnerDocument();
        var foszakacs = document.createElement("foszakacs");
        foszakacs.setAttribute("fkod", "f" + foszakacsIdCounter++);
        foszakacs.setAttribute("e_f", etteremId);
        root.appendChild(foszakacs);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        foszakacs.appendChild(nevElem);

        var eletkorElem = document.createElement("eletkor");
        eletkorElem.setTextContent(String.valueOf(eletkor));
        foszakacs.appendChild(eletkorElem);

        for (String vegzettseg : vegzettsegek) {
            var vegzettsegElem = document.createElement("vegzettseg");
            vegzettsegElem.setTextContent(vegzettseg);
            foszakacs.appendChild(vegzettsegElem);
        }
    }

    static int szakacsIdCounter = 1;
    private static void createSzakacs(Element root, String nev, int eletkor, String etteremId, String... vegzettsegek) {
        var document = root.getOwnerDocument();
        var szakacs = document.createElement("szakacs");
        szakacs.setAttribute("szkod", "sz" + szakacsIdCounter++);
        szakacs.setAttribute("e_sz", etteremId);
        root.appendChild(szakacs);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        szakacs.appendChild(nevElem);

        var eletkorElem = document.createElement("eletkor");
        eletkorElem.setTextContent(String.valueOf(eletkor));
        szakacs.appendChild(eletkorElem);

        for (String vegzettseg : vegzettsegek) {
            var vegzettsegElem = document.createElement("vegzettseg");
            vegzettsegElem.setTextContent(vegzettseg);
            szakacs.appendChild(vegzettsegElem);
        }
    }

    static int gyakornokIdCounter = 1;
    private static void createGyakornok(Element root, String nev, String etteremId, String gyakorlatKezdete, String gyakorlatIdotartama, String... muszakok) {
        var document  = root.getOwnerDocument();
        var gyakornok = document.createElement("gyakornok");
        gyakornok.setAttribute("gykod", "gy" + gyakornokIdCounter++);
        gyakornok.setAttribute("e_gy", etteremId);
        root.appendChild(gyakornok);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        gyakornok.appendChild(nevElem);

        var gyakorlat = document.createElement("gyakorlat");
        var kezdeteElem = document.createElement("kezdete");
        kezdeteElem.setTextContent(gyakorlatKezdete);
        var idotartamaElem = document.createElement("idotartama");
        idotartamaElem.setTextContent(gyakorlatIdotartama);

        gyakorlat.appendChild(kezdeteElem);
        gyakorlat.appendChild(idotartamaElem);
        gyakornok.appendChild(gyakorlat);

        for (String muszak : muszakok) {
            var muszakElem = document.createElement("muszak");
            muszakElem.setTextContent(muszak);
            gyakornok.appendChild(muszakElem);
        }
    }

    static int vendegIdCounter = 1;
    private static void createVendeg(Element root, String nev, int eletkor, String varos, String utca, String hazszam) {
        var document = root.getOwnerDocument();
        var vendeg = document.createElement("vendeg");
        vendeg.setAttribute("vkod", "v" + vendegIdCounter++);
        root.appendChild(vendeg);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        vendeg.appendChild(nevElem);

        var eletkorElem = document.createElement("eletkor");
        eletkorElem.setTextContent(String.valueOf(eletkor));
        vendeg.appendChild(eletkorElem);

        var cim = document.createElement("cim");
        var varosElem = document.createElement("varos");
        varosElem.setTextContent(varos);
        var utcaElem = document.createElement("utca");
        utcaElem.setTextContent(utca);
        var hazszamElem = document.createElement("hazszam");
        hazszamElem.setTextContent(hazszam);

        cim.appendChild(varosElem);
        cim.appendChild(utcaElem);
        cim.appendChild(hazszamElem);
        vendeg.appendChild(cim);
    }

    public static void createRendeles(Element root, String vendegId, String etteremId, String etel, int osszeg) {
        var document = root.getOwnerDocument();
        var rendeles = document.createElement("rendeles");
        rendeles.setAttribute("e_v_v", vendegId);
        rendeles.setAttribute("e_v_e", etteremId);
        root.appendChild(rendeles);

        var osszegElem = document.createElement("osszeg");
        osszegElem.setTextContent(String.valueOf(osszeg));
        rendeles.appendChild(osszegElem);

        var etelElem = document.createElement("etel");
        etelElem.setTextContent(etel);
        rendeles.appendChild(etelElem);
    }
}
