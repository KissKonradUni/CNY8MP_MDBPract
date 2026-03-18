for $s in doc("./CNY8MP_XML.xml")//szakacs where $s/vegzettseg = "Szakközépiskola" return
<szakacs>
  <szakacsID>{data($s/@szkod)}</szakacsID>
  <nev>{data($s/nev)}</nev>
  <vegzettsegek>{$s/vegzettseg}</vegzettsegek>
</szakacs>