for $v in doc("./CNY8MP_XML_hazifeladat.xml")//vasarlo
  let $rendelesek := doc("./CNY8MP_XML_hazifeladat.xml")//rendeles[@v_fid = $v/@v_id]
  for $r in $rendelesek
    return
      <adat>
        <nev>{$v/nev}</nev>
        <osszeg>{$r/teljes-ar}</osszeg>
      </adat>
