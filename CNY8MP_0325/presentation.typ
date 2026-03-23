#import "@preview/codly:1.3.0": *
#import "@preview/codly-languages:0.1.1": *
#show: codly-init.with()
#codly(languages: codly-languages)

#set page(
  paper: "presentation-16-9", 
  margin: (x: 3em, y: 2.5em), 
  fill: rgb("#FDFDFD"),
)

#set text(font: ("DejaVu Sans"), size: 18pt, fill: rgb("#333333"))
#set par(leading: 0.8em, justify: true)
#set list(spacing: 1.2em, marker: text(fill: rgb("#0B3B60"))[•])

#let title(t) = {
  text(size: 32pt, weight: "bold", fill: rgb("#0B3B60"), t)
}

#let box(body) = {
  rect(
    width: 100%,
    inset: 1.5em,
    radius: 0.3em,
    fill: rgb("#F0F6FC"),
    stroke: 1pt + rgb("#C2D8EC"),
    body
  )
}

#let term(word) = text(weight: "bold", fill: rgb("#105A8C"))[#word]

// --- Címlap ---
#align(center + horizon)[
  #text(weight: "bold", size: 44pt, fill: rgb("#0B3B60"))[Modern adatbázis-rendszerek (MSc)]
  #v(1em)
  #text(size: 28pt, fill: rgb("#444444"))[7. Gyakorlat: LINQ programozás]
  #v(3em)
  #text(size: 18pt, fill: rgb("#666666"))[GitHub: https://konrads.hu/short/mdb]
]

#pagebreak()

// --- 3. Dia ---
#title[A LINQ (Language Integrated Query)]

Az adatlekérdezés nyelvi szintre emelése: hagyományosan az algoritmus és az adatlekérdezés (SQL) két különálló dolog, a LINQ viszont hidat képez a kettő között.

- #term[Egységes megközelítés:] Ugyanaz a lekérdező szintaxis használható memóriabeli objektumhalmazokon, relációs adatbázisokon vagy épp hierarchikus XML-fákon.
- #term[Deklaratív stílus:] Ahelyett, hogy meghatároznánk, _hogyan_ lépkedjen végig a ciklus az elemeken, csupán feltételeket fogalmazunk meg. (SQL-analógia).

#pagebreak()

// --- 4. Dia ---
#title[Fontos C\# koncepciók]

Mielőtt XML-fájlokat elemeznénk, három modern C\# nyelvi elemet kell ismernünk:

1. #term[Típus-következtetés (`var`):] A C\# erősen típusos nyelv, de a fordító képes kikövetkeztetni a pontos típust az egyenlőségjel jobb oldalából, így a kód olvashatóbbá válik.

2. #term[Lambda-kifejezések (névtelen függvények):] A `Where` és `Select` metódusok paraméterként úgynevezett delegáltakat (függvényreferenciákat) várnak.
  - Szintaxis: `(változó) => logikai_kifejezés`

3. #term[Fluent API:] A funkcionális programozásból ismert láncolási technika.
  - Példa: `készlet.Select(...).Where(x => x.Készlet > 0).ToList()`

#pagebreak()

// --- 5. Dia ---
#title[0. Feladat: A fejlesztőkörnyezet előkészítése]
#text([A .NET parancssoros eszközkészlete (CLI) felel a projektstruktúra létrehozásáért. A projekthez használjunk egy almappát (pl. NEPTUNKOD_0325/etterem).], size: 18pt)
```bash
# Új konzolos .NET projekt inicializálása
dotnet new console
# A projekt fordítása és futtatása
dotnet run
```
*Fontos:* A feldolgozandó XML fájlt a megnyitott projekt munkakönyvtárába (a projekt gyökérkönyvtárába) kell helyezni.

#pagebreak()

// --- 6. Dia ---
#title[1. Feladat: Az XML-dokumentum \ beolvasása memóriába]

A `System.Xml.Linq` névtér modern DOM-kezelést biztosít. A klasszikus W3C DOM modellel szemben (amelyet pl. Java-ban ismerhettetek meg), sokkal kevesebb "boilerplate" kódot igényel.

```cs
using System.Xml.Linq;
using System.Linq;

// A teljes dokumentum betöltése a fájlrendszerből
XDocument dokumentum = XDocument.Load("etterem.xml");
// "Leszármazott" elemeket a .Descendants() hívással kérdezünk le.
XElement gyoker = dokumentum.Descendants("vendeglatas").First();
```

A `.First()` metódus azt biztosítja, hogy a lekérdezett gyűjteményből az attribútumokkal rendelkező legelső tényleges XML node-ot kapjuk meg objektumként.

Ez így önmagában természetesen nem teljesen biztonságos (nem kezeljük a hiányzó vagy hibás értékű elemeket), de a gyakorlat kedvéért most elfogadható.

#pagebreak()

// --- 7. Dia ---
#title[2. Feladat: Deklaratív szűrés]

Az első igazi LINQ-lekérdezés bevezetése. Célunk az elemek szűrése attribútumok vagy gyermekelemek értéke alapján.

```cs
Console.WriteLine("(1.) Az ötcsillagos éttermek kilistázása:\n");

var otCsillagosEttermek = gyoker.Descendants("etterem")
    .Where(elem => elem.Descendants("csillag").First().Value == "5")
    .ToList();

otCsillagosEttermek.ForEach(elem => 
    Console.WriteLine(" - " + elem.Descendants("nev").First().Value)
);
```

#pagebreak()

#v(35%)

#box[Megjegyzés: a `.Value` tulajdonság beolvasása minden esetben `string` típust eredményez, ezért az összehasonlítás (==) is sztringként ("5") történik.]

#pagebreak()

// --- 8. Dia ---
#title[3. Feladat: Komplex relációs \ összekapcsolás]

#term[Probléma:] Egy szabványos egyed-kapcsolat rendszerben az azonosítók (kulcsok) csak hivatkoznak egy másik entitásra.

SQL-ben ez egy klasszikus `JOIN` feladat. LINQ és XML esetén ezt beágyazott kereséssel és névtelen (anonymous) típusok visszaadásával oldjuk meg:

1. Végigmegyünk a `rendeles` elemeken.
2. A rendeléshez tartozó idegen kulcsokat kinyerjük (`elem.Attribute(...)`).
3. Ezekkel a kulcsokkal kikeressük a teljes dokumentumból az éttermet és a vendéget.
4. Projektáljuk az eredményt (`Select -> new { ... }`).

#pagebreak()

// --- 9. Dia ---
#title[3. Feladat: Az összeállított lekérdezés]

```cs
var joinEredmeny = gyoker.Descendants("rendeles")
    .Select(elem => {
        var vendegID = elem.Attribute("e_v_v").Value;
        var vendegElem = gyoker.Descendants("vendeg")
            .First(v => v.Attribute("vkod").Value == vendegID);
        var vendegNev = vendegElem.Descendants("nev").First().Value;
        var osszeg = elem.Descendants("osszeg").First().Value;
        return new { Vendeg = vendegNev, Osszeg = osszeg };
    })
    .ToList();
```

#pagebreak()

// --- 10. Dia ---
#title[4. Feladat: Aggregáció és konverziók]

Statisztikai vagy üzleti összesítések (pl. átlagos fogyasztás, teljes bevétel) számítása beépített LINQ-metódusokkal lehetséges (`Average`, `Sum`, `Max`, `Min`).

Az XML-dokumentumokban az értékeket szöveges (string) formában tároljuk, ezért az aggregáció előtt gondoskodni kell a konverzióról.

```cs
var atlagKoltes = gyoker.Descendants("rendeles")
    // Közvetlenül string kollekcióra projektálunk
    .Select(rendeles => rendeles.Descendants("osszeg").First().Value)
    // Átalakítjuk double-lé és átlagoljuk
    .Average(osszeg => double.Parse(osszeg)); 

Console.WriteLine($"Az átlagos költés: {atlagKoltes} Ft");
```

#pagebreak()

// --- 11. Dia ---
#title[5. Feladat: Módosítás]

Bár a LINQ alapvetően mellékhatásmentes ("pure function") jellegű, a hierarchikus DOM-objektumfáknál helyben (in-place) módosítást is alkalmazhatunk.

```cs
gyoker.Descendants("etterem")
    .Where(elem => elem.Descendants("csillag").First().Value == "3")
    .ToList()
    .ForEach(elem => {
        elem.Remove(); // Teljes node törlése az XML-fából
    });

// A memóriában megváltozott struktúra mentése
XDocument toroltDokumentum = new XDocument(gyoker);
toroltDokumentum.Save("etterem_torolt.xml");
```

#pagebreak()

// --- 12. Dia ---
#title[Gyakorlati önálló (házi) feladat]

*A hallgatói feladat:* Az előbb bemutatott, 5 lépésből álló életciklust (Beolvasás $->$ Szűrés $->$ Join $->$ Aggregáció $->$ Adatmanipuláció és mentés) implementálni a *saját témájú*, az előző hetekben használt XML-dokumentumon.

#v(1em)
A beadással és a repo-struktúrával kapcsolatos elvárások:

- Két külön, dedikált mappa legyen létrehozva a `NEPTUNKOD_0325` könyvtár alatt a teljes izoláció érdekében (egy az órai, egy a házi programhoz).
- A fordító `bin/` és `obj/` mappái szigorúan *gitignore* szabály alá esnek; GitHubra csak a forráskódot töltsük fel.

