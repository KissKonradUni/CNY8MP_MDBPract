using System.Xml.Linq;



// XML dokumentum betöltése
// Feltételezzük, hogy az XML schema-nak helyes,
// ezért nem használunk null-check-et szinte sehol.
XDocument dokumentum = XDocument.Load("etterem.xml");
XElement  gyoker     = dokumentum.Descendants("vendeglatas").First();

// A gyöker elem kiíratása
Console.WriteLine("\n(0.) A gyöker elem / teljes dokumentum: \n\n" + gyoker);



// Egyszerű "SELECT * FROM etterem WHERE ..." művelet
Console.WriteLine("\n(1.) Az ötcsillagos éttermek: \n");
var otCsillagosEttermek = gyoker.Descendants("etterem")
    .Where(elem => elem.Descendants("csillag").First().Value == "5")
    .ToList();
otCsillagosEttermek.ForEach(elem => Console.WriteLine(" - " + elem.Descendants("nev").First().Value));



Console.WriteLine("\n(2.) Melyik vendég, melyik étteremben, mit rendelt, mennyiért (3-mas join): \n");
// Dotnet 8.0-ban van egy SQL-szerűbb Join művelet, de 
// tradícionális módon mutatom meg az egyszerűség kedvéért.
var harmasJoin = gyoker.Descendants("rendeles")
    .Select(elem => {
        var vendegID = elem.Attribute("e_v_v").Value;
        // Lehetséges rövidítés:
        // - a "First()" művelet egy feltételt is kaphat opcionálisan
        //   így kombinálható a Where művelettel.
        var vendeg = gyoker.Descendants("vendeg")
            .Where(vendegElem => vendegElem.Attribute("vkod").Value == vendegID)
            .First()
            .Descendants("nev")
            .FirstOrDefault().Value;
        
        var etteremID = elem.Attribute("e_v_e").Value;
        // Itt alkalmazom is a rövidítést:
        var etterem = gyoker.Descendants("etterem")
            .First(etteremElem => etteremElem.Attribute("ekod").Value == etteremID)
            .Descendants("nev")
            .FirstOrDefault().Value;

        var rendeltEtel = elem.Descendants("etel").First().Value;
        var osszeg = elem.Descendants("osszeg").First().Value;

        // Visszatérek egy joined objektummal, ami tartalmaz mindent
        // Mintha egy "SELECT vendeg.nev AS Vendeg, ... FROM rendeles JOIN vendeg ..." lenne SQL-ben
        return new
        {
            Vendeg = vendeg,
            Etterem = etterem,
            Etel = rendeltEtel,
            Osszeg = osszeg
        };
    })
    .ToList();

harmasJoin.ForEach(join =>
    // A @ multiline string-et jelent,
    // a $ pedig string interpolációt, így könnyen formázható a kiíratás.
    // Ilyenkor a string tabokkal nem rendezhető, mert azokat is figyelembe veszi.
    Console.WriteLine(
@$"- Vendég: {join.Vendeg}
  - Étterem: {join.Etterem}
  - Rendelt étel: {join.Etel}
  - Összeg: {join.Osszeg}"
    )
);



// Aggregáció is könnyen készíthető
var atlagKoltes = gyoker.Descendants("rendeles")
    .Select(rendeles => rendeles.Descendants("osszeg").First().Value)
    .Average(osszeg => double.Parse(osszeg)); // Az XML-ből kiolvasott érték string, ezért parse-olni kell számra

Console.WriteLine($"\n(3.) Az átlagos költés: {atlagKoltes}");



// És módosítás is lehetséges, például az összeg értékét megduplázom minden rendelésnél
Console.WriteLine("\n(4.) Minden rendelés összegét megduplázom, majd elmentem egy új fájlba: \n");
gyoker.Descendants("rendeles")
    .ToList()
    .ForEach(rendeles => {
        var osszegElem = rendeles.Descendants("osszeg").First();
        
        var osszeg = double.Parse(osszegElem.Value);
        Console.Write($" - Eredeti összeg: {osszeg}");
        osszeg *= 2;
        Console.WriteLine($", új összeg: {osszeg}");
        
        osszegElem.Value = osszeg.ToString();
    });

XDocument modositottDokumentum = new XDocument(gyoker);
modositottDokumentum.Save("etterem_modositott.xml");
Console.WriteLine("\nAz új fájl neve: \"etterem_modositott.xml\"");



// Természetesen törölni is lehet elemeket
Console.WriteLine("\n(5.) Törlöm az összes 3 csillagos éttermet, majd elmentem egy új fájlba: \n");
gyoker.Descendants("etterem")
    .Where(elem => elem.Descendants("csillag").First().Value == "3")
    .ToList()
    .ForEach(elem => {
        Console.WriteLine(" - Törlöm: " + elem.Descendants("nev").First().Value);
        elem.Remove();
    });

XDocument toroltDokumentum = new XDocument(gyoker);
toroltDokumentum.Save("etterem_torolt.xml");
Console.WriteLine("\nAz új fájl neve: \"etterem_torolt.xml\"");
// Ez természetesen töri a sémát, de manuálisan validálható csak az új fájl.



// És utolsó példa kedvéért felépítek kódból egy egyszerű XML dokumentumot
Console.WriteLine("\n(6.) Egy új XML dokumentum létrehozása: ");
XElement ujGyoker = new XElement("konyvtar",
    new XElement("konyv",
        new XAttribute("isbn", "1234567890"),
        new XElement("cim", "LINQ to XML példa"),
        new XElement("szerzo", "Nagyszerű Konrád"),
        new XElement("ar", "2990")
    ),
    new XElement("konyv",
        new XAttribute("isbn", "0987654321"),
        new XElement("cim", "C# programozás"),
        new XElement("szerzo", "Szerény Konrád"),
        new XElement("ar", "3990")
    )
);
// És hogy a LINQ ne maradjon ki minden könyv árát megduplázom és hozzáadom hogy a szerő best seller
ujGyoker.Descendants("konyv")
    .ToList()
    .ForEach(konyv => {
        var arElem = konyv.Descendants("ar").First();
        var ar = double.Parse(arElem.Value);
        ar *= 2;
        arElem.Value = ar.ToString();
        
        var szerzoElem = konyv.Descendants("szerzo").First();
        var szerzo = szerzoElem.Value;
        szerzo += " (best seller)";
        szerzoElem.Value = szerzo;
    });
// A LINQ alapvetően funkcionális műveletekre van kitalálva, azaz mellékhatás nélkül egy adatsorból adna
// egy új adatsort, de az XML dokumentumok módosítása esetén ez nem praktikus, ezért itt a "ForEach"
// műveletet használom, ami lehetőve teszi a helyben történő módosítást.
XDocument ujDokumentum = new XDocument(ujGyoker);
ujDokumentum.Save("konyvtar.xml");
Console.WriteLine("\nAz új fájl neve: \"konyvtar.xml\"");