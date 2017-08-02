package org.shinhwagk.od.keyperformance

import java.math.BigDecimal

object MetricProcess {
  def pAllDbTimeSum(n: Map[Int, Map[String, BigDecimal]],o: Map[Int, Map[String, BigDecimal]]): BigDecimal = {
    val nn = n.flatMap(_._2.filter(_._1 == "DBT").map(_._2)).foldLeft(BigDecimal.ONE)(_ add  _)
    val oo = o.flatMap(_._2.filter(_._1 == "DBT").map(_._2)).foldLeft(BigDecimal.ONE)(_ add  _)
    nn.subtract(oo)
  }

//  def pDeltaTime(o: Map[Int, Map[String, BigDecimal]], n: Map[Int, Map[String, BigDecimal]]): BigDecimal = {
//    n.map { case (k, v) => k -> v("DBT").subtract(o(k).getOrElse("DBT", BigDecimal.ZERO)) }
//  }
}
