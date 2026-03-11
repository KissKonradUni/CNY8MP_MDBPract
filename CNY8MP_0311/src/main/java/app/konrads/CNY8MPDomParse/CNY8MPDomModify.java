package app.konrads.CNY8MPDomParse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

@SuppressWarnings("ALL")
public class CNY8MPDomModify {

    public static void main(String[] args) throws TransformerException {
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

        modifyVendeg(root);
        modifyGyakornok(root);
        deleteRendeles(root);

        // Output the modified XML to console
        Transformer transformer = TransformerFactory
            .newInstance()
            .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);
        transformer.transform(source, new StreamResult(System.out));
    }

    public static void modifyVendeg(Element element) {
        System.out.println("Vendég módosítása... \n");

        var vendegek = element.getElementsByTagName("vendeg");
        if (vendegek.getLength() > 0) {
            var vendeg = (Element) vendegek.item(0);
            var nev = vendeg.getElementsByTagName("nev").item(0);
            var kor = vendeg.getElementsByTagName("kor").item(0);
            if (nev != null)
                nev.setTextContent("Új Vendég Név");
            if (kor != null)
                kor.setTextContent("30");
        }
    }

    public static void modifyGyakornok(Element element) {
        System.out.println("Gyakornok módosítása... \n");

        var gyakornokok = element.getElementsByTagName("gyakornok");
        for (int i = 0; i < gyakornokok.getLength(); i++) {
            var gyakornok = (Element) gyakornokok.item(i);
            if (gyakornok.hasAttribute("e_gy")) {
                gyakornok.setAttribute("e_gy", "e3");
                return;
            }
        }
    }

    public static void deleteRendeles(Element element) {
        System.out.println("Rendelés elemek törlése... \n");

        var rendelesek = element.getElementsByTagName("rendeles");
        for (int i = rendelesek.getLength() - 1; i >= 0; i--) {
            var rendeles = rendelesek.item(i);
            rendeles.getParentNode().removeChild(rendeles);
        }
    }
    
}
