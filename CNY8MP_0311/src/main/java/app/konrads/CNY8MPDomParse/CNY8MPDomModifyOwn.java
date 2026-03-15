package app.konrads.CNY8MPDomParse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

@SuppressWarnings("ALL")
public class CNY8MPDomModifyOwn {

    public static void main(String[] args) throws TransformerException {
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

        modifyAru(root);
        modifyRendeles(root);
        deleteAR(root);

        Transformer transformer = TransformerFactory
                .newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(document);
        transformer.transform(source, new StreamResult(System.out));
    }

    public static void modifyAru(Element element) {
        System.out.println("Áru módosítása... \n");

        var aruk = element.getElementsByTagName("aru");
        if (aruk.getLength() > 0) {
            var aru = (Element) aruk.item(0);
            var nev = aru.getElementsByTagName("nev").item(0);
            var nettoAr = aru.getElementsByTagName("netto-ar").item(0);
            if (nev != null)
                nev.setTextContent("Tej 2.8% ESL");
            if (nettoAr != null)
                nettoAr.setTextContent("220");
        }
    }

    public static void modifyRendeles(Element element) {
        System.out.println("Rendelés módosítása... \n");

        var rendelesek = element.getElementsByTagName("rendeles");
        for (int i = 0; i < rendelesek.getLength(); i++) {
            var rendeles = (Element) rendelesek.item(i);
            if (rendeles.hasAttribute("f_fid")) {
                rendeles.setAttribute("f_fid", "f1");
                return;
            }
        }
    }

    public static void deleteAR(Element element) {
        System.out.println("Áru-rendelés elem törlése... \n");

        var arRendelesek = element.getElementsByTagName("a-r");
        for (int i = arRendelesek.getLength() - 1; i >= 0; i--) {
            var arRendeles = arRendelesek.item(i);
            arRendeles.getParentNode().removeChild(arRendeles);
        }
    }
}

