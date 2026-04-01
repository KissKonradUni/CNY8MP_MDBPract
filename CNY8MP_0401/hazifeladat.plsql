CREATE TABLE uzeletek_xml (
    id      NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    adat    XMLType
);

INSERT INTO uzeletek_xml (adat) VALUES (
    XMLType('<?xml version="1.0" encoding="UTF-8"?><uzeletek xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="CNY8MP_XML_hazifeladat.xsd"><!-- Kereskedések --><kereskedes k_id="k1"><cim><iranyitoszam>1234</iranyitoszam><varos>Budapest</varos><utca-hazszam>Váci utca 10.</utca-hazszam></cim><dolgozo a_fid="al1"/><dolgozo a_fid="al2"/></kereskedes><kereskedes k_id="k2"><cim><iranyitoszam>5678</iranyitoszam><varos>Debrecen</varos><utca-hazszam>Kossuth utca 5.</utca-hazszam></cim><dolgozo a_fid="al3"/><dolgozo a_fid="al4"/></kereskedes><!-- Raktárok --><raktar r_id="r1"><cim><iranyitoszam>4321</iranyitoszam><varos>Szeged</varos><utca-hazszam>Raktár utca 3.</utca-hazszam></cim></raktar><raktar r_id="r2"><cim><iranyitoszam>8765</iranyitoszam><varos>Pécs</varos><utca-hazszam>Raktár utca 7.</utca-hazszam></cim></raktar><!-- Beszállító --><beszallito r_fid="r1" k_fid="k1" /><beszallito r_fid="r2" k_fid="k2" /><!-- Áru --><aru a_id="a1"><nev>Tej</nev><netto-ar>200</netto-ar><afa>27</afa></aru><aru a_id="a2"><nev>Kenyer</nev><netto-ar>150</netto-ar><afa>27</afa></aru><!-- Leltár --><leltar a_fid="a1" r_fid="r1"><darabszam>10</darabszam></leltar><leltar a_fid="a2" r_fid="r2"><darabszam>20</darabszam></leltar><!-- Rendelés --><rendeles re_id="re1" f_fid="f1" v_fid="v1" k_fid="k1"><teljesült>igen</teljesült><datum-ido>2026-02-25 14:30:00</datum-ido><teljes-ar>4500</teljes-ar></rendeles><rendeles re_id="re2" f_fid="f2" v_fid="v2" k_fid="k2"><teljesült>nem</teljesült><datum-ido>2026-02-23 12:00:00</datum-ido><teljes-ar>3000</teljes-ar></rendeles><!-- Áru-Rendelés --><a-r a_fid="a1" re_fid="re1"><darabszam>5</darabszam></a-r><a-r a_fid="a2" re_fid="re2"><darabszam>10</darabszam></a-r><!-- Vásárló --><vasarlo v_id="v1"><nev>Kovács János</nev><cim><iranyitoszam>1234</iranyitoszam><varos>Budapest</varos><utca-hazszam>Fő utca 1.</utca-hazszam></cim></vasarlo><vasarlo v_id="v2"><nev>Szabó Péter</nev><cim><iranyitoszam>5678</iranyitoszam><varos>Debrecen</varos><utca-hazszam>Kossuth utca 2.</utca-hazszam></cim></vasarlo><!-- Fuvarozó --><fuvarozó f_id="f1"><nev>GLS</nev><dij>1500</dij></fuvarozó><fuvarozó f_id="f2"><nev>DHL</nev><dij>2000</dij></fuvarozó><!-- Alkalmazott --><alkalmazott a_id="al1"><nev>Juhász Péter</nev><lakcim>1234, Budapest, Fő utca 15.</lakcim><telefonszam>+36 1 123 4567</telefonszam><telefonszam>+36 30 987 6543</telefonszam><fizetes>350000</fizetes></alkalmazott><alkalmazott a_id="al2"><nev>Kiss Anna</nev><lakcim>5678, Debrecen, Kossuth utca 10.</lakcim><telefonszam>+36 52 123 456</telefonszam><telefonszam>+36 30 987 654</telefonszam><fizetes>400000</fizetes></alkalmazott><alkalmazott a_id="al3"><nev>Nagy László</nev><lakcim>4321, Szeged, Raktár utca 5.</lakcim><telefonszam>+36 62 123 456</telefonszam><telefonszam>+36 30 987 654</telefonszam><fizetes>300000</fizetes></alkalmazott><alkalmazott a_id="al4"><nev>Horváth Éva</nev><lakcim>8765, Pécs, Raktár utca 8.</lakcim><telefonszam>+36 72 123 456</telefonszam><telefonszam>+36 30 987 654</telefonszam><fizetes>320000</fizetes></alkalmazott></uzeletek>')
);

SELECT  x.k_id,
        x.iranyitoszam,
        x.varos,
        x.utca_hazszam,
        x.elso_dolgozo,
        x.masodik_dolgozo
    FROM uzeletek_xml u,
        XMLTable('/uzeletek/kereskedes'
            PASSING u.adat
            COLUMNS
                k_id             VARCHAR2(10)  PATH '@k_id',
                iranyitoszam     NUMBER        PATH 'cim/iranyitoszam',
                varos            VARCHAR2(100) PATH 'cim/varos',
                utca_hazszam     VARCHAR2(200) PATH 'cim/*[local-name()="utca-hazszam"]',
                elso_dolgozo     VARCHAR2(10)  PATH 'dolgozo[1]/@a_fid',
                masodik_dolgozo  VARCHAR2(10)  PATH 'dolgozo[2]/@a_fid'
        ) x;

SELECT  r.re_id,
        r.teljesult,
        r.datum_ido,
        r.teljes_ar,
        v.nev AS vasarlo_nev,
        k.varos AS kereskedes_varos
    FROM uzeletek_xml u,
        XMLTable('/uzeletek/rendeles'
            PASSING u.adat
            COLUMNS
                re_id       VARCHAR2(10)  PATH '@re_id',
                v_fid       VARCHAR2(10)  PATH '@v_fid',
                k_fid       VARCHAR2(10)  PATH '@k_fid',
                teljesult   VARCHAR2(10)  PATH '*[local-name()="teljesült"]',
                datum_ido   VARCHAR2(30)  PATH '*[local-name()="datum-ido"]',
                teljes_ar   NUMBER        PATH '*[local-name()="teljes-ar"]'
        ) r,
        XMLTable('/uzeletek/vasarlo'
            PASSING u.adat
            COLUMNS
                v_id    VARCHAR2(10)  PATH '@v_id',
                nev     VARCHAR2(200) PATH 'nev'
        ) v,
        XMLTable('/uzeletek/kereskedes'
            PASSING u.adat
            COLUMNS
                k_id    VARCHAR2(10)  PATH '@k_id',
                varos   VARCHAR2(100) PATH 'cim/varos'
        ) k
    WHERE r.v_fid = v.v_id
      AND r.k_fid = k.k_id;

SELECT  l.r_fid,
        a.nev AS aru_nev,
        l.darabszam,
        a.netto_ar,
        ROUND(l.darabszam * a.netto_ar * (1 + a.afa / 100), 0) AS brutto_ertek
    FROM uzeletek_xml u,
        XMLTable('/uzeletek/leltar'
            PASSING u.adat
            COLUMNS
                a_fid       VARCHAR2(10)  PATH '@a_fid',
                r_fid       VARCHAR2(10)  PATH '@r_fid',
                darabszam   NUMBER        PATH 'darabszam'
        ) l,
        XMLTable('/uzeletek/aru'
            PASSING u.adat
            COLUMNS
                a_id        VARCHAR2(10)  PATH '@a_id',
                nev         VARCHAR2(100) PATH 'nev',
                netto_ar    NUMBER        PATH '*[local-name()="netto-ar"]',
                afa         NUMBER        PATH 'afa'
        ) a
    WHERE l.a_fid = a.a_id;

CREATE OR REPLACE TYPE hf_cim_t AS OBJECT (
    iranyitoszam NUMBER,
    varos        VARCHAR2(100),
    utca_hazszam VARCHAR2(200)
);

CREATE OR REPLACE TYPE hf_dolgozo_va AS VARRAY(10) OF VARCHAR2(10);

CREATE OR REPLACE TYPE hf_kereskedes_t AS OBJECT (
    k_id         VARCHAR2(10),
    cim          hf_cim_t,
    dolgozok     hf_dolgozo_va,
    MEMBER FUNCTION leiras RETURN VARCHAR2
);

CREATE OR REPLACE TYPE BODY hf_kereskedes_t AS
    MEMBER FUNCTION leiras RETURN VARCHAR2 IS
    BEGIN
        RETURN SELF.k_id || ' - ' || SELF.cim.varos || ', ' || SELF.cim.utca_hazszam;
    END leiras;
END;

CREATE TABLE hf_kereskedesek OF hf_kereskedes_t (
    PRIMARY KEY (k_id)
);

INSERT INTO hf_kereskedesek
    SELECT hf_kereskedes_t(
        x.k_id,
        hf_cim_t(x.iranyitoszam, x.varos, x.utca_hazszam),
        hf_dolgozo_va(x.elso_dolgozo, x.masodik_dolgozo)
    )
    FROM uzeletek_xml u,
        XMLTable('/uzeletek/kereskedes'
            PASSING u.adat
            COLUMNS
                k_id             VARCHAR2(10)  PATH '@k_id',
                iranyitoszam     NUMBER        PATH 'cim/iranyitoszam',
                varos            VARCHAR2(100) PATH 'cim/varos',
                utca_hazszam     VARCHAR2(200) PATH 'cim/*[local-name()="utca-hazszam"]',
                elso_dolgozo     VARCHAR2(10)  PATH 'dolgozo[1]/@a_fid',
                masodik_dolgozo  VARCHAR2(10)  PATH 'dolgozo[2]/@a_fid'
        ) x;

SELECT  k.k_id,
        k.cim.iranyitoszam,
        k.cim.varos,
        k.cim.utca_hazszam,
        k.leiras() AS leiras
    FROM hf_kereskedesek k;

SELECT  k.cim.varos AS varos,
        COUNT(*) AS kereskedes_db
    FROM hf_kereskedesek k
    GROUP BY k.cim.varos
    ORDER BY kereskedes_db DESC;

UPDATE hf_kereskedesek k
    SET k.cim = hf_cim_t(k.cim.iranyitoszam, k.cim.varos, 'Frissitett cim 1.')
    WHERE k.k_id = 'k1';

SELECT k.k_id, k.cim.utca_hazszam FROM hf_kereskedesek k;