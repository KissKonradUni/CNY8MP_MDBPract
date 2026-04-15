CREATE OR REPLACE TYPE szemely_t AS OBJECT (
    nev VARCHAR2(100),

    MEMBER FUNCTION bemutatkozas RETURN VARCHAR2,
    STATIC FUNCTION min_kor RETURN NUMBER,
    ORDER MEMBER FUNCTION cmp (p2 szemely_t) RETURN INTEGER
) NOT FINAL;



CREATE OR REPLACE TYPE BODY szemely_t AS

    MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'A nevem: ' || nev;
    END;

    STATIC FUNCTION min_kor RETURN NUMBER IS
    BEGIN
        RETURN 18;
    END;

    ORDER MEMBER FUNCTION cmp (p2 szemely_t) RETURN INTEGER IS
    BEGIN
        IF self.nev < p2.nev THEN
            RETURN -1;
        ELSIF self.nev > p2.nev THEN
            RETURN 1;
        ELSE
            RETURN 0;
        END IF;
    END;

END;



CREATE OR REPLACE TYPE foszakacs_t UNDER szemely_t (
    fkod VARCHAR2(50),
    eletkor NUMBER,

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2
);



CREATE OR REPLACE TYPE BODY foszakacs_t AS

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'Főszakács vagyok. A nevem: ' || nev || ', korom: ' || eletkor;
    END;

END;



CREATE OR REPLACE TYPE vendeg_t UNDER szemely_t (
    vkod VARCHAR2(50),
    eletkor NUMBER,

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2
);



CREATE OR REPLACE TYPE BODY vendeg_t AS

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'Vendég vagyok. A nevem: ' || nev;
    END;

END;



CREATE TABLE etterem_szemelyek_tbl OF szemely_t;



INSERT INTO etterem_szemelyek_tbl
SELECT vendeg_t(x.nev, x.vkod, x.eletkor)
FROM VENDEGLATAS_XML v,
     XMLTABLE(
        '/vendeglatas/vendeg'
        PASSING v.adat
        COLUMNS
            nev VARCHAR2(100) PATH 'nev',
            vkod VARCHAR2(50) PATH 'vkod',
            eletkor NUMBER PATH 'eletkor'
     ) x;



INSERT INTO etterem_szemelyek_tbl
SELECT foszakacs_t(x.nev, x.fkod, x.eletkor)
FROM VENDEGLATAS_XML v,
     XMLTABLE(
        '/vendeglatas/foszakacs'
        PASSING v.adat
        COLUMNS
            nev VARCHAR2(100) PATH 'nev',
            fkod VARCHAR2(50) PATH 'fkod',
            eletkor NUMBER PATH 'eletkor'
     ) x;



SELECT szemely_t.min_kor() FROM dual;



SELECT t.nev, t.bemutatkozas()
FROM etterem_szemelyek_tbl t
ORDER BY VALUE(t);



SELECT t.nev
FROM etterem_szemelyek_tbl t
WHERE VALUE(t) IS OF (ONLY vendeg_t);



SELECT t.nev,
       TREAT(VALUE(t) AS vendeg_t).eletkor AS eletkor
FROM etterem_szemelyek_tbl t
WHERE VALUE(t) IS OF (vendeg_t);



-- Házi feladat



CREATE OR REPLACE TYPE szakacs2_t UNDER szemely_t (
    szkod VARCHAR2(50),
    reszleg VARCHAR2(100),

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2
);



CREATE OR REPLACE TYPE BODY szakacs2_t AS

    OVERRIDING MEMBER FUNCTION bemutatkozas RETURN VARCHAR2 IS
    BEGIN
        RETURN 'Szakács vagyok a(z) ' || reszleg || ' részlegen, a nevem: ' || nev;
    END;

END;



INSERT INTO etterem_szemelyek_tbl
SELECT szakacs2_t(x.nev, x.szkod, x.reszleg)
FROM VENDEGLATAS_XML v,
     XMLTABLE(
        '/vendeglatas/szakacs'
        PASSING v.adat
        COLUMNS
            nev VARCHAR2(100) PATH 'nev',
            szkod VARCHAR2(50) PATH 'szkod',
            reszleg VARCHAR2(100) PATH 'reszleg'
     ) x;



SELECT t.bemutatkozas()
FROM etterem_szemelyek_tbl t
WHERE VALUE(t) IS OF (foszakacs_t, szakacs2_t);
