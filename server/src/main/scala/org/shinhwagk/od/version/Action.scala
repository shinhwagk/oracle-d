package org.shinhwagk.od.version

import java.sql.Types

import oracle.jdbc.pool.OracleDataSource
import oracle.jdbc.{OracleCallableStatement, OracleConnection}
import org.shinhwagk.od.common.OracleJdbcConvert

import scala.concurrent.Future

object Action {
  val versionSqlFile: String = "script\\version\\version.sql"

  import OracleJdbcConvert._
  import org.shinhwagk.od.common.DatabaseOperation._
  import org.shinhwagk.od.common.Tools._

  def getVersion(name: String): Future[String] = {
    val ods: OracleDataSource = ???

    val plsqlCode: String = readSqlFile(versionSqlFile)

    val callSet: (OracleCallableStatement) => Unit = (cs) => cs.registerOutParameter(1, Types.NUMERIC)

    val callResult: (OracleCallableStatement) => String = (cs) => cs.getLong(1).toString

    val f: (OracleConnection) => String = plsqlCall(plsqlCode, callSet, callResult)

    usedOracleConnect(ods.getConnection, f, true)
  }

}
