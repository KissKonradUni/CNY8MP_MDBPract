for $r in doc("./CNY8MP_XML.xml")//rendeles
return
  update value $r/osszeg with $r/osszeg + 1000