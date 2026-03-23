for $r in doc("./CNY8MP_XML_hazifeladat.xml")//rendeles
return
  update value $r/teljes-ar with $r/teljes-ar + 1000
