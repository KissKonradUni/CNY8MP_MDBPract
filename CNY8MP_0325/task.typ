#import "@preview/codly:1.3.0": *
#import "@preview/codly-languages:0.1.1": *
#show: codly-init.with()
#codly(languages: codly-languages)

*Modern adatbázis rendszerek MSc - 7. Practice *

*Téma: LINQ programozása*

*Mappa: NEPTUNKOD_0325*

*Töltse fel a GitHub rendszer aktuális mappájába a forrás fájlokat!*

Fejlesztő-környezet: Visual Studio/Visual Studio Code/Intellij Rider

#v(2em)

*0. Feladat* - fejlesztőkörnyezet előkészítése

#set enum(numbering: "a)")
+ Ha Windows rendszeren dolgozunk, és fel van telepítve a `Visual Studio/.net`, akkor nyitunk egy új *C\# projektet*, és folytathatjuk tovább a feladatokat. (pl. `NEPTUNKOD_0325/Etterem/`)

+ Amennyiben más rendszeren dolgozunk, vagy a `Visual Studio` nincs feltelepítve, a `Visual Studio Code` és a `dotnet-sdk/dotnet-runtime` csomagok segítségével tudunk projektet létrehozni. Lehetőleg minimum 8.0-ás `.net` lenne az ideális, de megoldható régebbivel is. Így készítünk egy projektet:

  - Nyitunk egy `PowerShell/cmd/Terminál` ablakot
  - Odanavigálunk a projekt (üres) mappájához (pl. `NEPTUNKOD_0325/Etterem/`)
  - Készítünk egy `.net` projektet: `dotnet new console`
  - Megnyitjuk a mappát `Visual Studio Code`-al
  - Szerkesztés után futtatjuk az alábbi módon: `dotnet run`

#v(2em)

*1. Feladat* - fájlbeolvasás

A *C\#* nyelv remek eszközökkel rendelkezik az *XML dokumentumok* beolvasására. Amit mi fogunk használni, az az `XDocument/XElement` osztályok, amelyek felépítése rendkívül hasonlít a Java `Document`-re (szintén XML Dom alapú).

```cs
XDocument dokumentum = XDocument.Load("etterem.xml");
XElement  gyoker     = dokumentum.Descendants("vendeglatas").First();
```

#v(1.5em)

*2. Feladat* - egyszerű műveletek

+ a fájlt ki tudjuk íratni a sima `Console.WriteLine()` utasítással:
```cs
// Az XElement toString metódusa automatikusan használatra kerül
Console.WriteLine("(0.) A teljes dokumentum: \n\n" + gyoker);
```

+ készíthetünk egy egyszerű szűrt lekérdezést `LINQ` segítségével:
```cs
// Egyszerű "SELECT * FROM etterem WHERE ..." művelet
Console.WriteLine("(1.) Az ötcsillagos éttermek: \n");
var otCsillagosEttermek = gyoker.Descendants("etterem")
    .Where(elem => elem.Descendants("csillag").First().Value == "5")
    .ToList();
otCsillagosEttermek.ForEach(elem => 
  Console.WriteLine(" - " + elem.Descendants("nev").First().Value)
);
```

*3. Feladat* - komplex lekérdezés

+ több `JOIN`-ból álló művelet:
```cs
Console.WriteLine("(2.) Melyik vendég, melyik étteremben, mit rendelt, mennyiért: \n");

// Dotnet 8.0-ban van egy SQL-szerűbb Join művelet, de 
// tradícionális módon mutatom a követhetőség kedvéért.
var harmasJoin = gyoker.Descendants("rendeles")
    .Select(elem => {
        var vendegID = elem.Attribute("e_v_v").Value;
        var vendeg   = gyoker.Descendants("vendeg")
            .Where(vendegElem => vendegElem.Attribute("vkod").Value == vendegID)
            .First()
            .Descendants("nev")
            .FirstOrDefault().Value;
        // Lehetséges rövidítés:
        // - a "First()" művelet egy feltételt is kaphat opcionálisan
        //   így kombinálható a Where művelettel.
        
        var etteremID = elem.Attribute("e_v_e").Value;
        var etterem   = gyoker.Descendants("etterem")
            .First(etteremElem => etteremElem.Attribute("ekod").Value == etteremID)
            .Descendants("nev")
            .FirstOrDefault().Value;
        // Itt alkalmaztam is a rövidítést.

        var rendeltEtel = elem.Descendants("etel").First().Value;
        var osszeg      = elem.Descendants("osszeg").First().Value;

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
```
A kiíratás szimplán megtehető ezek után:
```cs
harmasJoin.ForEach(join => Console.WriteLine(...));
```

#pagebreak()

*4. Feladat* - egyéb műveletek

+ aggregáció:
```cs
var atlagKoltes = gyoker.Descendants("rendeles")
    .Select(rendeles => rendeles.Descendants("osszeg").First().Value)
    .Average(osszeg => double.Parse(osszeg)); 
    // Az XML-ből kiolvasott érték string, ezért parse-olni kell számra

Console.WriteLine($"(3.) Az átlagos költés: {atlagKoltes}");
```

+ módosítás:
```cs
Console.WriteLine("(4.) Minden rendelés összegét megduplázom, majd elmentem egy új fájlba: \n");
gyoker.Descendants("rendeles")
    .ToList()
    .ForEach(rendeles => {
        var osszegElem = rendeles.Descendants("osszeg").First();
        var osszeg = double.Parse(osszegElem.Value);
        osszeg *= 2;
        osszegElem.Value = osszeg.ToString();
    });

XDocument modositottDokumentum = new XDocument(gyoker);
modositottDokumentum.Save("etterem_modositott.xml");
Console.WriteLine("Az új fájl neve: \"etterem_modositott.xml\"");
```

+ törlés:
```cs
Console.WriteLine("(5.) Törlöm az összes 3 csillagos éttermet, majd elmentem egy új fájlba: \n");
gyoker.Descendants("etterem")
    .Where(elem => elem.Descendants("csillag").First().Value == "3")
    .ToList()
    .ForEach(elem => {
        elem.Remove();
    });

XDocument toroltDokumentum = new XDocument(gyoker);
toroltDokumentum.Save("etterem_torolt.xml");
Console.WriteLine("Az új fájl neve: \"etterem_torolt.xml\"");
```

#pagebreak()

*Szemléltetés* - nem kötelező elkészíteni a feladat teljességéhez:

+ kódból is könnyedén állítható elő *XML* fájl:
```cs
Console.WriteLine("(6.) Egy új XML dokumentum létrehozása: ");
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
// És hogy a LINQ ne maradjon ki minden könyv árát megduplázom
// és hozzáadom hogy a szerző best seller
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
XDocument ujDokumentum = new XDocument(ujGyoker);
ujDokumentum.Save("konyvtar.xml");
Console.WriteLine("\nAz új fájl neve: \"konyvtar.xml\"");
```
A *LINQ* alapvetően funkcionális műveletekre van kitalálva, azaz mellékhatás nélkül egy adatsorból adna egy új adatsort, de az *XML* dokumentumok módosítása esetén ez nem praktikus, ezért itt a `.ForEach` műveletet használom, ami lehetőve teszi a helyben történő módosítást.

#pagebreak();

*Házifeladat*

Otthoni feladat, hogy a `0.-5.` történő példákat amelyeket a program végez megcsináljuk a saját *XML* dokumentumunkra, valamilyen hasonló nehézségi szinten / módon. A kettő keletkezett programot a *`NEPTUNKOD_0325`* mappa almappáiba kérem elhelyezni, a `bin` és `obj` mappák kivételével, mert *binárist* nem töltünk fel *GitHub*-ra.

Példa elrendezés:

```
\ - NEPTUNKOD_0325
  | - etterem
    | - etterem.XML
    | - ...
    \ - Program.cs
  \ - hazifeladat
    | - sajat.XML
    | - ...
    \ - Program.cs
```