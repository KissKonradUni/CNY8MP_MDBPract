CREATE TABLE vendeglatas_xml (
    id      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    adat    XMLType
);

INSERT INTO vendeglatas_xml (adat) VALUES (
    XMLType('<?xml version="1.0" encoding="UTF-8"?>
<vendeglatas xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="CNY8MP_XML.xsd">

    <!-- Ettermek -->
    <etterem ekod="e1">
        <nev>Aranyhal Étterem</nev>
        <cim>
            <varos>Miskolc</varos>
            <utca>Széchenyi u.</utca>
            <hazszam>107</hazszam>
        </cim>
        <csillag>3</csillag>
    </etterem>

    <etterem ekod="e2">
        <nev>Vörös Oroszlán Étterem</nev>
        <cim>
            <varos>Budapest</varos>
            <utca>Andrássy út</utca>
            <hazszam>12</hazszam>
        </cim>
        <csillag>5</csillag>
    </etterem>

    <!-- Foszakacsok -->
    <foszakacs fkod="f1" e_f="e1">
        <nev>Főz István</nev>
        <eletkor>45</eletkor>
        <vegzettseg>Szakközépiskola</vegzettseg>
        <vegzettseg>Főiskola</vegzettseg>
    </foszakacs>

    <foszakacs fkod="f2" e_f="e2">
        <nev>Kovács János</nev>
        <eletkor>38</eletkor>
        <vegzettseg>Szakközépiskola</vegzettseg>
        <vegzettseg>Főiskola</vegzettseg>
    </foszakacs>

    <!-- Szakacs -->
    <szakacs szkod="sz1" e_sz="e1">
        <nev>Szabó Péter</nev>
        <eletkor>30</eletkor>
        <vegzettseg>Szakközépiskola</vegzettseg>
    </szakacs>

    <szakacs szkod="sz2" e_sz="e2">
        <nev>Nagy László</nev>
        <eletkor>28</eletkor>
        <vegzettseg>Szakközépiskola</vegzettseg>
        <vegzettseg>OKJ</vegzettseg>
    </szakacs>

    <!-- Gyakornok -->
    <gyakornok gykod="gy1" e_gy="e1">
        <nev>Kiss Anna</nev>
        <gyakorlat>
            <kezdete>2026-01-01</kezdete>
            <idotartama>2 hónap</idotartama>
        </gyakorlat>
        <muszak>Nappali</muszak>
        <muszak>Hétvége</muszak>
    </gyakornok>

    <gyakornok gykod="gy2" e_gy="e2">
        <nev>Horváth Gábor</nev>
        <gyakorlat>
            <kezdete>2026-01-15</kezdete>
            <idotartama>2 hónap</idotartama>
        </gyakorlat>
        <muszak>Délután</muszak>
        <muszak>Esti</muszak>
        <muszak>Hétvége</muszak>
    </gyakornok>

    <!-- Vendeg -->
    <vendeg vkod="v1">
        <nev>Juhász Péter</nev>
        <eletkor>35</eletkor>
        <cim>
            <varos>Debrecen</varos>
            <utca>Kossuth Lajos utca</utca>
            <hazszam>5</hazszam>
        </cim>
    </vendeg>

    <vendeg vkod="v2">
        <nev>Szűcs Mária</nev>
        <eletkor>28</eletkor>
        <cim>
            <varos>Szeged</varos>
            <utca>Rákóczi tér</utca>
            <hazszam>3</hazszam>
        </cim>
    </vendeg>

    <!-- Rendelés -->
    <rendeles e_v_v="v1" e_v_e="e1">
        <osszeg>5000</osszeg>
        <etel>Gyulyásleves</etel>
    </rendeles>

    <rendeles e_v_v="v2" e_v_e="e2">
        <osszeg>8000</osszeg>
        <etel>Palacsinta</etel>
    </rendeles>

</vendeglatas>')
);

SELECT  x.ekod,
        x.nev,
        x.varos,
        x.utca,
        x.hazszam,
        x.csillag
    FROM vendeglatas_xml v,
        XMLTable('/vendeglatas/etterem'
            PASSING v.adat
            COLUMNS
                ekod        VARCHAR2(10)  PATH '@ekod',
                nev         VARCHAR2(100) PATH 'nev',
                varos       VARCHAR2(100) PATH 'cim/varos',
                utca        VARCHAR2(100) PATH 'cim/utca',
                hazszam     VARCHAR2(10)  PATH 'cim/hazszam',
                csillag     NUMBER        PATH 'csillag'
        ) x;

SELECT  x.ekod,
        x.nev,
        x.varos,
        x.utca,
        x.hazszam,
        x.csillag
    FROM vendeglatas_xml v,
        XMLTable('/vendeglatas/etterem'
            PASSING v.adat
            COLUMNS
                ekod        VARCHAR2(10)  PATH '@ekod',
                nev         VARCHAR2(100) PATH 'nev',
                varos       VARCHAR2(100) PATH 'cim/varos',
                utca        VARCHAR2(100) PATH 'cim/utca',
                hazszam     VARCHAR2(10)  PATH 'cim/hazszam',
                csillag     NUMBER        PATH 'csillag'
        ) x
    WHERE x.csillag = 5;

SELECT  sz.szkod, sz.nev AS szakcs_nev,
        sz.reszleg, sz.eletkor,
        e.nev AS etterem_nev
    FROM vendeglatas_xml v,
        XMLTABLE('/vendeglatas/szakacs'
            PASSING v.adat
            COLUMNS
                szkod   varchar2(10)  PATH '@szkod',
                e_sz    varchar2(10)  PATH '@e_sz',
                nev     varchar2(200) PATH 'nev',
                reszleg varchar2(100) PATH 'reszleg',
                eletkor NUMBER        PATH 'eletkor'
        ) sz,
        XMLTABLE('/vendeglatas/etterem'
            PASSING v.adat
            COLUMNS
                ekod VARCHAR2(10)  PATH '@ekod',
                nev  VARCHAR2(200) PATH 'nev'
        ) e
    WHERE sz.e_sz = e.ekod;

CREATE OR REPLACE TYPE cim_t AS OBJECT (
    varos   VARCHAR2(100),
    utca    VARCHAR2(100),
    hazszam VARCHAR2(10)
);

CREATE OR REPLACE TYPE etterem_t AS OBJECT (
    ekod    VARCHAR2(10),
    nev     VARCHAR2(200),
    csillag NUMBER(1),
    cim     cim_t,
    MEMBER FUNCTION leiras RETURN VARCHAR2
);

CREATE OR REPLACE TYPE BODY etterem_t AS
    MEMBER FUNCTION leiras RETURN VARCHAR2 IS
    BEGIN
        RETURN SELF.nev || ' (' || SELF.csillag || ' csillag) - ' || SELF.cim.varos;
    END leiras;
END;

CREATE TABLE ettermek OF etterem_t (
    PRIMARY KEY (ekod)
);

INSERT INTO ettermek
    SELECT etterem_t (
        x.ekod,
        x.nev,
        x.csillag,
        cim_t(x.varos, x.utca, x.hazszam)
    )
    FROM vendeglatas_xml v,
        XMLTABLE('/vendeglatas/etterem'
            PASSING v.adat
            COLUMNS
                ekod    VARCHAR2(10)  PATH '@ekod',
                nev     VARCHAR2(200) PATH 'nev',
                varos   VARCHAR2(100) PATH 'cim/varos',
                utca    VARCHAR2(100) PATH 'cim/utca',
                hazszam VARCHAR2(100) PATH 'cim/hazszam',
                csillag NUMBER        PATH 'csillag'
        ) x;

SELECT e.ekod, e.nev, e.csillag, e.cim.varos, e.cim.utca, e.cim.hazszam FROM ettermek e;

SELECT e.leiras() AS leiras FROM ettermek e;

SELECT  e.cim.varos      AS varos,
        COUNT(*)         AS db,
        AVG(e.csillag)   AS atlag_csillag
    FROM ettermek e
    GROUP BY e.cim.varos
    ORDER BY atlag_csillag DESC;

UPDATE ettermek e
    SET e.csillag = 4
    WHERE e.ekod = 'e1';

DELETE FROM ettermek e
    WHERE e.csillag < 3;

SELECT COUNT(*) AS maradt FROM ettermek;

CREATE OR REPLACE TYPE vegzettseg_va AS VARRAY(5) OF VARCHAR2(200);

CREATE OR REPLACE TYPE szakacs_t AS OBJECT (
    szkod        VARCHAR2(10),
    nev          VARCHAR2(200),
    reszleg      VARCHAR2(100),
    eletkor      NUMBER,
    vegzettsegek vegzettseg_va
);

CREATE TABLE szakacsok OF szakacs_t (
    PRIMARY KEY (szkod)
)

INSERT INTO szakacsok VALUES (
    szakacs_t('sz1', 'Ötlek Elek', 'Saucier', 30, vegzettseg_va(
        'Szakközépiskola', 'Le Cordon Bleu'
    ))
);

INSERT INTO szakacsok VALUES (
    szakacs_t('sz2', 'Kocsis Tibor', 'Entremetier', 32, vegzettseg_va(
        'Szakközépiskola', 'Le Cordon Bleu'
    ))
);

SELECT s.nev, s.reszleg, v.COLUMN_VALUE AS vegzettseg
    FROM szakacsok s,
        TABLE(s.vegzettsegek) v;
