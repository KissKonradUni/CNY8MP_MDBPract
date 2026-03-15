package app.konrads.CNY8MPDomParse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

@SuppressWarnings("ALL")
public class CNY8MPDomWriteOwn {

    public static void main(String[] args) throws ParserConfigurationException, TransformerException {
        var filePath = "./CNY8MP_XML_hazifeladat1.xml";
        var file = new File(filePath);

        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .newDocument();

        var root = document.createElement("uzeletek");
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root.setAttribute("xsi:noNamespaceSchemaLocation", "CNY8MP_XML_hazifeladat.xsd");
        document.appendChild(root);

        createKereskedes(root, "1111", "Budapest", "Alkotmány utca 11.", "al1", "al2");
        createKereskedes(root, "9021", "Győr", "Baross út 5.", "al3", "al4");

        createRaktar(root, "6720", "Szeged", "Ipari park 3.");
        createRaktar(root, "7630", "Pécs", "Logisztika utca 8.");

        createBeszallito(root, "r1", "k1");
        createBeszallito(root, "r2", "k2");

        createAru(root, "Őrölt kávé", 1200, 27);
        createAru(root, "Száraztészta", 650, 18);

        createLeltar(root, "a1", "r1", 40);
        createLeltar(root, "a2", "r2", 25);

        createRendeles(root, "f1", "v1", "k1", "igen", "2026-03-01 09:15:00", 4800);
        createRendeles(root, "f2", "v2", "k2", "nem", "2026-03-02 16:45:00", 3250);

        createAR(root, "a1", "re1", 4);
        createAR(root, "a2", "re2", 5);

        createVasarlo(root, "Németh Ádám", "1117", "Budapest", "Fehérvári út 21.");
        createVasarlo(root, "Balogh Réka", "4024", "Debrecen", "Piac utca 9.");

        createFuvarozo(root, "MPL", 1400);
        createFuvarozo(root, "DHL", 2100);

        createAlkalmazott(root, "Tóth Júlia", "1111, Budapest, Alkotmány utca 4.", 380000, "+36 1 222 3344", "+36 30 111 2233");
        createAlkalmazott(root, "Varga Bence", "1117, Budapest, Karinthy út 12.", 360000, "+36 1 444 5566", "+36 20 333 4455");
        createAlkalmazott(root, "Király Dóra", "9021, Győr, Szent István út 18.", 340000, "+36 96 123 456", "+36 30 765 4321");
        createAlkalmazott(root, "Molnár Ákos", "9024, Győr, Tihanyi Árpád út 7.", 320000, "+36 96 654 321", "+36 70 456 7890");

        Transformer transformer = TransformerFactory
                .newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);
        transformer.transform(source, new StreamResult(System.out));
        transformer.transform(source, new StreamResult(file));
    }

    static int kereskedesIdCounter = 1;
    private static void createKereskedes(Element root, String iranyitoszam, String varos, String utcaHazszam, String... dolgozoAzonositok) {
        var document = root.getOwnerDocument();
        var kereskedes = document.createElement("kereskedes");
        kereskedes.setAttribute("k_id", "k" + kereskedesIdCounter++);
        root.appendChild(kereskedes);

        var cim = document.createElement("cim");
        var iranyitoszamElem = document.createElement("iranyitoszam");
        iranyitoszamElem.setTextContent(iranyitoszam);
        var varosElem = document.createElement("varos");
        var varosText = varos;
        varosElem.setTextContent(varosText);
        var utcaHazszamElem = document.createElement("utca-hazszam");
        utcaHazszamElem.setTextContent(utcaHazszam);

        cim.appendChild(iranyitoszamElem);
        cim.appendChild(varosElem);
        cim.appendChild(utcaHazszamElem);
        kereskedes.appendChild(cim);

        for (String dolgozoAzonosito : dolgozoAzonositok) {
            var dolgozo = document.createElement("dolgozo");
            dolgozo.setAttribute("a_fid", dolgozoAzonosito);
            kereskedes.appendChild(dolgozo);
        }
    }

    static int raktarIdCounter = 1;
    private static void createRaktar(Element root, String iranyitoszam, String varos, String utcaHazszam) {
        var document = root.getOwnerDocument();
        var raktar = document.createElement("raktar");
        raktar.setAttribute("r_id", "r" + raktarIdCounter++);
        root.appendChild(raktar);

        var cim = document.createElement("cim");
        var iranyitoszamElem = document.createElement("iranyitoszam");
        iranyitoszamElem.setTextContent(iranyitoszam);
        var varosElem = document.createElement("varos");
        varosElem.setTextContent(varos);
        var utcaHazszamElem = document.createElement("utca-hazszam");
        utcaHazszamElem.setTextContent(utcaHazszam);

        cim.appendChild(iranyitoszamElem);
        cim.appendChild(varosElem);
        cim.appendChild(utcaHazszamElem);
        raktar.appendChild(cim);
    }

    private static void createBeszallito(Element root, String raktarId, String kereskedesId) {
        var document = root.getOwnerDocument();
        var beszallito = document.createElement("beszallito");
        beszallito.setAttribute("r_fid", raktarId);
        beszallito.setAttribute("k_fid", kereskedesId);
        root.appendChild(beszallito);
    }

    static int aruIdCounter = 1;
    private static void createAru(Element root, String nev, int nettoAr, int afa) {
        var document = root.getOwnerDocument();
        var aru = document.createElement("aru");
        aru.setAttribute("a_id", "a" + aruIdCounter++);
        root.appendChild(aru);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        aru.appendChild(nevElem);

        var nettoArElem = document.createElement("netto-ar");
        nettoArElem.setTextContent(String.valueOf(nettoAr));
        aru.appendChild(nettoArElem);

        var afaElem = document.createElement("afa");
        afaElem.setTextContent(String.valueOf(afa));
        aru.appendChild(afaElem);
    }

    private static void createLeltar(Element root, String aruId, String raktarId, int darabszam) {
        var document = root.getOwnerDocument();
        var leltar = document.createElement("leltar");
        leltar.setAttribute("a_fid", aruId);
        leltar.setAttribute("r_fid", raktarId);
        root.appendChild(leltar);

        var darabszamElem = document.createElement("darabszam");
        darabszamElem.setTextContent(String.valueOf(darabszam));
        leltar.appendChild(darabszamElem);
    }

    static int rendelesIdCounter = 1;
    private static void createRendeles(Element root, String fuvarozoId, String vasarloId, String kereskedesId, String teljesult, String datumIdo, int teljesAr) {
        var document = root.getOwnerDocument();
        var rendeles = document.createElement("rendeles");
        rendeles.setAttribute("re_id", "re" + rendelesIdCounter++);
        rendeles.setAttribute("f_fid", fuvarozoId);
        rendeles.setAttribute("v_fid", vasarloId);
        rendeles.setAttribute("k_fid", kereskedesId);
        root.appendChild(rendeles);

        var teljesultElem = document.createElement("teljesült");
        teljesultElem.setTextContent(teljesult);
        rendeles.appendChild(teljesultElem);

        var datumIdoElem = document.createElement("datum-ido");
        datumIdoElem.setTextContent(datumIdo);
        rendeles.appendChild(datumIdoElem);

        var teljesArElem = document.createElement("teljes-ar");
        teljesArElem.setTextContent(String.valueOf(teljesAr));
        rendeles.appendChild(teljesArElem);
    }

    private static void createAR(Element root, String aruId, String rendelesId, int darabszam) {
        var document = root.getOwnerDocument();
        var arRendeles = document.createElement("a-r");
        arRendeles.setAttribute("a_fid", aruId);
        arRendeles.setAttribute("re_fid", rendelesId);
        root.appendChild(arRendeles);

        var darabszamElem = document.createElement("darabszam");
        darabszamElem.setTextContent(String.valueOf(darabszam));
        arRendeles.appendChild(darabszamElem);
    }

    static int vasarloIdCounter = 1;
    private static void createVasarlo(Element root, String nev, String iranyitoszam, String varos, String utcaHazszam) {
        var document = root.getOwnerDocument();
        var vasarlo = document.createElement("vasarlo");
        vasarlo.setAttribute("v_id", "v" + vasarloIdCounter++);
        root.appendChild(vasarlo);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        vasarlo.appendChild(nevElem);

        var cim = document.createElement("cim");
        var iranyitoszamElem = document.createElement("iranyitoszam");
        iranyitoszamElem.setTextContent(iranyitoszam);
        var varosElem = document.createElement("varos");
        varosElem.setTextContent(varos);
        var utcaHazszamElem = document.createElement("utca-hazszam");
        utcaHazszamElem.setTextContent(utcaHazszam);

        cim.appendChild(iranyitoszamElem);
        cim.appendChild(varosElem);
        cim.appendChild(utcaHazszamElem);
        vasarlo.appendChild(cim);
    }

    static int fuvarozoIdCounter = 1;
    private static void createFuvarozo(Element root, String nev, int dij) {
        var document = root.getOwnerDocument();
        var fuvarozo = document.createElement("fuvarozo");
        fuvarozo.setAttribute("f_id", "f" + fuvarozoIdCounter++);
        root.appendChild(fuvarozo);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        fuvarozo.appendChild(nevElem);

        var dijElem = document.createElement("dij");
        dijElem.setTextContent(String.valueOf(dij));
        fuvarozo.appendChild(dijElem);
    }

    static int alkalmazottIdCounter = 1;
    private static void createAlkalmazott(Element root, String nev, String lakcim, int fizetes, String... telefonszamok) {
        var document = root.getOwnerDocument();
        var alkalmazott = document.createElement("alkalmazott");
        alkalmazott.setAttribute("a_id", "al" + alkalmazottIdCounter++);
        root.appendChild(alkalmazott);

        var nevElem = document.createElement("nev");
        nevElem.setTextContent(nev);
        alkalmazott.appendChild(nevElem);

        var lakcimElem = document.createElement("lakcim");
        lakcimElem.setTextContent(lakcim);
        alkalmazott.appendChild(lakcimElem);

        for (String telefonszam : telefonszamok) {
            var telefonszamElem = document.createElement("telefonszam");
            telefonszamElem.setTextContent(telefonszam);
            alkalmazott.appendChild(telefonszamElem);
        }

        var fizetesElem = document.createElement("fizetes");
        fizetesElem.setTextContent(String.valueOf(fizetes));
        alkalmazott.appendChild(fizetesElem);
    }
}

