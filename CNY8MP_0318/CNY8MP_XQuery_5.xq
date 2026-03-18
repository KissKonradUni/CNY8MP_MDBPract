for $v in doc("./CNY8MP_XML.xml")//vendeg
  let $rendelesek := doc("./CNY8MP_XML.xml")//rendeles[@e_v_v = $v/@vkod]
  for $r in $rendelesek
    return
      <adat>
        <nev>{$v/nev}</nev>
        <osszeg>{$r/osszeg}</osszeg>
      </adat>