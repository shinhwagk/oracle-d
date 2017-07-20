package org.shinhwagk.od.common

import java.sql.{Connection, PreparedStatement, ResultSet}

import oracle.jdbc.{OracleConnection, OraclePreparedStatement, OracleResultSet}

object OracleJdbcConvert {

  implicit def connectionToOracleConnect(c: Connection): OracleConnection = {
    c.asInstanceOf[OracleConnection]
  }

  implicit def preparedStatementToOraclePreparedStatement(p: PreparedStatement): OraclePreparedStatement = {
    p.asInstanceOf[OraclePreparedStatement]
  }

  implicit def resultSetToOracleResultSet(r: ResultSet): OracleResultSet = {
    r.asInstanceOf[OracleResultSet]
  }
}
