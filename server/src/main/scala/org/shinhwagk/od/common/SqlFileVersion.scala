package org.shinhwagk.od.common

case class SqlFileVersion(path: String, name: String, `9`: Boolean = false, `10`: Boolean = false, `11`: Boolean = false, `12`: Boolean = false) {

  private def judgeVersionExist(version: Int): Boolean = {
    version match {
      case 9 if `9` => true
      case 10 if `10` => true
      case 11 if `11` => true
      case 12 if `12` => true
    }
  }

  def getSqlCode(version: Int): String = {
    if (judgeVersionExist(version)) {
      val l = path :: name +"."+ version + ".sql" :: Nil
      Tools.readSqlFile(l.mkString("\\"))
    } else {
      throw new Exception("this version sql file no exist.")
    }
  }
}