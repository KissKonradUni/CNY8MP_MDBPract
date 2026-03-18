for $r in doc("./CNY8MP_XML_hazifeladat.xml")//fuvarozó where $r/dij <= 1500 return
<fuvarozo>
  <nev>{data($r/nev)}</nev>
  <dij>{data($r/dij)}</dij>
</fuvarozo>
