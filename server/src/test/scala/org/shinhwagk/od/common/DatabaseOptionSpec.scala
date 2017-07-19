package org.shinhwagk.od.common

import org.scalatest.FlatSpec

class DatabaseOptionSpec extends FlatSpec {
  val ci = ConnectInfo("10.65.193.25", 1521, "orayali2", "system", "oracle")

  "An empty Set" should "have size 0" in {
    val jv = DatabaseOperation.sqlQuery(ci,"select * from dual")


    assert(jv.toString() === """[{"X":"X"}]""")
  }


}
