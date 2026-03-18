for $gy in doc("./CNY8MP_XML.xml")//gyakornok where $gy/muszak = "Esti" return
<gyakornok>
  <gyakornokID>{data($gy/@gykod)}</gyakornokID>
  <nev>{data($gy/nev)}</nev>
  <gyakorlat>{$gy/gyakorlat}</gyakorlat>
  <muszakok>{$gy/muszak}</muszakok>
</gyakornok>