for $e in doc("./CNY8MP_XML.xml")//etterem where $e/csillag = 5 return
<etterem>
  <etteremID>{data($e/@ekod)}</etteremID>
  <nev>{data($e/nev)}</nev>
  <cim>{$e/cim}</cim>
  <csillag>{data($e/csillag)}</csillag>
</etterem>