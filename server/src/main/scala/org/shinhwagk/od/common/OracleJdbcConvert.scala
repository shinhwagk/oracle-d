package org.shinhwagk.od.common

import java.sql.{CallableStatement, Connection, PreparedStatement, ResultSet}

import oracle.jdbc.{OracleCallableStatement, OracleConnection, OraclePreparedStatement, OracleResultSet}

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

  implicit def callableStatementToOracleCallableStatement(c: CallableStatement): OracleCallableStatement = {
    c.asInstanceOf[OracleCallableStatement]
  }
}
