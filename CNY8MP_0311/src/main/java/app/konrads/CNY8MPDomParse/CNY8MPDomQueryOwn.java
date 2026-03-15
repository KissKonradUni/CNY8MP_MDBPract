package app.konrads.CNY8MPDomParse;

import org.w3c.dom.Element;

@SuppressWarnings("ALL")
public class CNY8MPDomQueryOwn {

	public static void queryRendelesTeljesult(Element element) {
		System.out.println("Azok a rendelések, amelyek teljesültek: \n");

		var rendelesek = element.getElementsByTagName("rendeles");
		for (int i = 0; i < rendelesek.getLength(); i++) {
			var rendeles = (Element) rendelesek.item(i);
			var teljesult = rendeles.getElementsByTagName("teljesült").item(0).getTextContent();
			if (!teljesult.toLowerCase().trim().equals("igen"))
				continue;

			System.out.println("Current element: " + rendeles.getTagName());

			var id = rendeles.getAttribute("re_id");
			var fuvarozoKulcs = rendeles.getAttribute("f_fid");
			var vasarloKulcs = rendeles.getAttribute("v_fid");
			var kereskedesKulcs = rendeles.getAttribute("k_fid");
			System.out.println("Rendelés ID: " + id);
			System.out.println("Fuvarozó idegen kulcs: " + fuvarozoKulcs);
			System.out.println("Vásárló idegen kulcs: " + vasarloKulcs);
			System.out.println("Kereskedés idegen kulcs: " + kereskedesKulcs);
			System.out.println("Teljesült: " + teljesult);

			var datumIdo = rendeles.getElementsByTagName("datum-ido").item(0).getTextContent();
			System.out.println("Dátum és idő: " + datumIdo);

			var teljesAr = rendeles.getElementsByTagName("teljes-ar").item(0).getTextContent();
			System.out.println("Teljes ár: " + teljesAr);

			System.out.println();
		}
	}

	public static void queryAlkalmazottMagasFizetes(Element element) {
		System.out.println("Azok az alkalmazottak, akiknek a fizetése legalább 350000: \n");

		var alkalmazottak = element.getElementsByTagName("alkalmazott");
		for (int i = 0; i < alkalmazottak.getLength(); i++) {
			var alkalmazott = (Element) alkalmazottak.item(i);
			var fizetes = Double.parseDouble(alkalmazott.getElementsByTagName("fizetes").item(0).getTextContent());
			if (fizetes < 350000)
				continue;

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
				var telefonszam = telefonszamok.item(j).getTextContent();
				System.out.print("(" + telefonszam + ") ");
			}
			System.out.println();

			System.out.println("Fizetés: " + (int) fizetes);
			System.out.println();
		}
	}

	public static void queryLeltarTizFelett(Element element) {
		System.out.println("Azok a leltár elemek, ahol a darabszám nagyobb mint 10: \n");

		var leltarak = element.getElementsByTagName("leltar");
		for (int i = 0; i < leltarak.getLength(); i++) {
			var leltar = (Element) leltarak.item(i);
			var darabszam = Integer.parseInt(leltar.getElementsByTagName("darabszam").item(0).getTextContent());
			if (darabszam <= 10)
				continue;

			System.out.println("Current element: " + leltar.getTagName());

			var aruKulcs = leltar.getAttribute("a_fid");
			var raktarKulcs = leltar.getAttribute("r_fid");
			System.out.println("Áru idegen kulcs: " + aruKulcs);
			System.out.println("Raktár idegen kulcs: " + raktarKulcs);
			System.out.println("Darabszám: " + darabszam);

			var aruk = element.getElementsByTagName("aru");
			for (int j = 0; j < aruk.getLength(); j++) {
				var aru = (Element) aruk.item(j);
				if (aru.getAttribute("a_id").equals(aruKulcs)) {
					var nev = aru.getElementsByTagName("nev").item(0).getTextContent();
					System.out.println("Áru neve: " + nev);
					break;
				}
			}

			System.out.println();
		}
	}
}

