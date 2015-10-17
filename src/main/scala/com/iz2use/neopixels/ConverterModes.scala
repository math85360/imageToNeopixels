package com.iz2use.neopixels

import org.scalajs.dom.raw.ImageData

/**
 * @author mathi_000
 */
trait Transform {
  def apply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int)
  def unapply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int)
}

case class Size(cols: Int, rows: Int)
case class RGBToSameColorLength(var result: Vector[Int] = Vector())(implicit size: Size) extends Transform {
  var trsf = {
    val pixels = Array.fill(size.cols * size.rows)((0, 0, 0))
    if (result.length > 0) {
      var lastColor = (0, 0, 0)
      var length = -3
      var i = 3;
      val len = result.length - 1
      while (i < len) {
        lastColor = (result(i - 3), result(i - 2), result(i - 1))
        val cnt = result(i)
        for (p <- 0 until cnt) {
          val v = result(i + p + 1)
          val x = v / size.rows
          val y = v % size.rows
          pixels(x * size.rows + y) = lastColor
        }
        i += 4 + cnt
      }
    }
    pixels
  }
  var map: Map[(Int, Int, Int), Vector[Int]] = Map()
  val black = (0, 0, 0)
  def apply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
    val color = (arr.data(p + 0), arr.data(p + 1), arr.data(p + 2))
    if (color != black) {
      val pos = x * size.rows + (size.rows - 1 - y)
      map = map + (color -> (map.get(color).getOrElse(Vector()) :+ pos))
    }
  }
  def unapply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
    val pos = x * size.rows + (size.rows - 1 - y)
    if (trsf.length > pos) {
      arr.data(p + 0) = trsf(pos)._1
      arr.data(p + 1) = trsf(pos)._2
      arr.data(p + 2) = trsf(pos)._3
    }
  }
  override def toString() = map.map(p => Seq(p._1._1, p._1._2, p._1._3, p._2.length) ++ p._2).flatten.mkString("{", ",", "}")
}

case class RGB(var result: Vector[Int] = Vector()) extends Transform {

  def apply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
    result = result :+ arr.data(p + 0)
    result = result :+ arr.data(p + 1)
    result = result :+ arr.data(p + 2)
  }
  def unapply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
    if (result.length / 3 > offset) {
      arr.data(p + 0) = result(offset * 3 + 0)
      arr.data(p + 1) = result(offset * 3 + 1)
      arr.data(p + 2) = result(offset * 3 + 2)
    }
  }
  override def toString() = result.mkString("{", ",", "}")
}
case class RGBto323Byte(var result: Vector[Int] = Vector()) extends Transform {

  def apply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
    val r = (arr.data(p + 0) >> 5)
    val g = (arr.data(p + 1) >> 6)
    val b = (arr.data(p + 2) >> 5)
    arr.data(p + 0) = r << 5
    arr.data(p + 1) = g << 6
    arr.data(p + 2) = b << 5
    val color = (r << 5) | (g << 3) | b
    result = result :+ color
  }

  def unapply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
    if (result.length > offset) {
      val color = result(offset)
      arr.data(p + 0) = (7 & (color >> 5)) << 5
      arr.data(p + 1) = (3 & (color >> 3)) << 6
      arr.data(p + 2) = (7 & (color >> 0)) << 5
    }
  }
  override def toString() = result.mkString("{", ",", "}")
}