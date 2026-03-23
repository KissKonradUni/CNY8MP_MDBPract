for $a in doc("./CNY8MP_XML_hazifeladat.xml")//alkalmazott where $a/fizetes >= 350000 return
<alkalmazott>
  <alkalmazottID>{data($a/@a_id)}</alkalmazottID>
  <nev>{data($a/nev)}</nev>
  <fizetes>{data($a/fizetes)}</fizetes>
</alkalmazott>
