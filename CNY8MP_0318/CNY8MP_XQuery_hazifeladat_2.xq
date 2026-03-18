for $r in doc("./CNY8MP_XML_hazifeladat.xml")//rendeles where $r/teljesült = "igen" return
<rendeles>
  <teljesult>igen</teljesult>
  <datum>{data($r/datum-ido)}</datum>
</rendeles>
