package com.iz2use.neopixels

import angular.extensions.ScalaTagsAngular._
import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.core._
import com.greencatsoft.angularjs.extensions.material.ThemingProvider
import scalacss.ScalatagsCss._
import scalacss.ScalatagsJsDomImplicits
import scalacss.Defaults._
import scalajs.js
import scalajs.js.annotation.JSExport
import scalajs.js.JSApp
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom
import scalatags.JsDom._
import scalatags.JsDom.TypedTag
import org.scalajs.dom
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.HTMLStyleElement
import scala.concurrent.Future
import org.scalajs.dom.raw.FileList
import org.scalajs.dom.raw.FileReader
import org.scalajs.dom.raw.UIEvent
import scalacss.mutable.GlobalRegistry
import org.scalajs.dom.raw.CanvasRenderingContext2D
import org.scalajs.dom.raw.ImageData

/**
 * @author mathi_000
 */
@JSExport("image")
object Image {
  @JSExport
  def converter() {

  }
}

class UISetup {
  import all._

  //def start() {
  val arduino_code = textarea(Presentation.arduino_code).render
  val arduino_preview = canvas(Presentation.arduino_preview).render
  val video_preview = video(Presentation.video_preview).render
  val img_preview = img(Presentation.img_preview).render
  val mode = select(option(selected := "", value := "0", "RGB to [(r,g,b,length,p,", sub("0"), "...p", sub("length"), ")*]"),
    option(value := "1", "RGB to [byte_3r2g3b*]"),
    option(value := "2", "RGB to [(R,G,B)*]")).render
  val paint_check = input(`type` := "checkbox", id := "paint_check").render
  val fit_in_size = input(`type` := "checkbox", id := "fit_in_size", checked := "").render
  val paint_r = input(value := "0").render
  val paint_g = input(value := "0").render
  val paint_b = input(value := "0").render
  val led_width = input(value := "60").render
  val led_height = input(value := "7").render

  setupUI

  def setupUI() {
    //GlobalRegistry.register(Presentation)
    dom.document.head.appendChild(Presentation.render[TypedTag[HTMLStyleElement]].render)
    dom.document.body.appendChild(
      div(Presentation.main,
        div(Presentation.content,
          div(Presentation.instructions,
            div(
              ol(li("Choisir un mode en bas à gauche"),
                li("Laisser fit coché si l'image doit remplir entièrement la zone"))),
            div(
              ol(li("Nombre de leds par ligne et par colonne modifiables en bas à droite"),
                li("Glisser-déposer une image sur cette page")))),
          div(
            img_preview,
            video_preview, arduino_preview),
          div(Presentation.line,
            arduino_code),
          div(Presentation.toolbox, div(Presentation.toolwrapper,
            mode,
            fit_in_size, label(`for` := "fit_in_size", "fit"),
            paint_check, label(`for` := "paint_check", "paint on canvas"),
            label("Red .."), paint_r,
            label("Green .."), paint_g,
            label("Blue .."), paint_b,
            label("LEDS : "), led_width,
            label("x"), led_height)))).render)
    dom.document.ondragenter = { (ev: dom.raw.DragEvent) =>
      ev.stopPropagation()
      ev.preventDefault()
    }
    dom.document.ondragover = { (ev: dom.raw.DragEvent) =>
      ev.stopPropagation()
      ev.preventDefault()
    }
    dom.document.ondrop = { (ev: dom.raw.DragEvent) =>
      ev.stopPropagation()
      ev.preventDefault()
      val dt = ev.dataTransfer
      val files = dt.files
      handleFiles(files)
    }
    arduino_code.onblur = { (ev: dom.raw.Event) =>
      ev.stopPropagation()
      ev.preventDefault()
      //dom.window.alert("hello")
      dom.window.setTimeout(() => readImage, 0)
    }
    arduino_preview.onmousemove = { (ev: dom.raw.MouseEvent) =>
      ev.stopPropagation()
      ev.preventDefault()
      val x = (ev.clientX - arduino_preview.offsetLeft) / 10
      val y = (ev.clientY - arduino_preview.offsetTop) / 10
      implicit val ctx = arduino_preview.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
      val arr = ctx.getImageData(x, y, 1, 1)
      //dom.console.info(ev.buttons)
      if ((ev.buttons == 0) && !paint_check.checked) {
        paint_r.value = arr.data(0).toString
        paint_g.value = arr.data(1).toString
        paint_b.value = arr.data(2).toString
        //arr.data(0) = 255
        //arr.data(1) = 255
        //arr.data(2) = 255
      } else if ((ev.buttons == 1) && paint_check.checked) {
        //dom.console.info("hhh")
        arr.data(0) = paint_r.value.toInt
        arr.data(1) = paint_g.value.toInt
        arr.data(2) = paint_b.value.toInt
        arr.data(3) = 255
        ctx.putImageData(arr, x, y)
        implicit val row_count = led_height.value.toInt
        val m = getModeFrom()
        ctx.putImageData(applyImageData(readImageData, m.apply), 0, 0)
        arduino_code.value = m.toString()
      }
    }
    /*import all._
    val html = div(
      md.flex.default,
      ng.App := "ConverterApp",
      ng.Controller := "ConverterController",
      div(img(ng.If := ""), video(ng.If := ""),
        canvas())).render
    dom.document.body.insertAdjacentHTML("beforeEnd", html)*/
  }
  def clear() {
    img_preview.style.display = "none"
    video_preview.style.display = "none"
  }
  def handleFiles(files: FileList) {
    clear()
    if (files.length > 0) {
      val file = files.item(0)
      val reader = new FileReader()
      if (file.`type`.startsWith("image")) {
        //val image = img()
        //img_preview.file = file
        reader.onload = { (ev: UIEvent) =>
          img_preview.src = ev.target.asInstanceOf[js.Dynamic].result.asInstanceOf[String]
          img_preview.style.display = "initial"
          dom.window.setTimeout(() => drawImage, 0)
        }
      } else if (file.`type`.startsWith("video")) {
        reader.onload = { (ev: UIEvent) =>
          video_preview.src = ev.target.asInstanceOf[js.Dynamic].result.asInstanceOf[String]
          video_preview.style.display = "initial"
        }
      }
      reader.readAsDataURL(file)
    }
  }
  def drawImage {
    setSize
    implicit val ctx = arduino_preview.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    //ctx.drawImage(img_preview, 0, 0, img_preview.naturalWidth, img_preview.naturalHeight, 0, 0, 60, 7)
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, arduino_preview.width, arduino_preview.height)
    val w = img_preview.width.toDouble
    val h = img_preview.height.toDouble
    if (w > 0 && h > 0) {
      val ratio = w / h
      val maxw = arduino_preview.width
      val maxh = arduino_preview.height
      if (fit_in_size.checked)
        ctx.drawImage(img_preview, 0, 0, maxw, maxh)
      else if (ratio * maxh > maxw) {
        //ctx.drawImage(img_preview, 0, 0, arduino_preview.width, arduino_preview.height)
        dom.console.info(ratio + " " + maxw / ratio + " / " + maxh)
        ctx.drawImage(img_preview, 0, 0, maxw / ratio, maxh)
      } else {
        dom.console.info(ratio + "x" + ratio * maxh + " / " + maxh)
        ctx.drawImage(img_preview, 0, 0, ratio * maxh, maxh)
      }
      //if(ratio)
      //dom.console.info(ratio)
      //val m = RGBto323Byte()
      implicit val row_count = led_height.value.toInt
      val m = getModeFrom()
      ctx.putImageData(applyImageData(readImageData, m.apply), 0, 0)
      arduino_code.value = m.toString()
    }
  }
  def readImageData(implicit ctx: CanvasRenderingContext2D) = ctx.getImageData(0, 0, arduino_preview.width, arduino_preview.height)

  def setSize {
    arduino_preview.width = led_width.value.toInt
    arduino_preview.height = led_height.value.toInt
    arduino_preview.style.width = (arduino_preview.width * 10).px
    arduino_preview.style.height = (arduino_preview.height * 10).px
  }
  def readImage {
    setSize
    implicit val ctx = arduino_preview.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    val txt = arduino_code.value.filterNot("{}; \r\n\t\u00A0".contains(_)).split(",").filter(_.trim().length != 0).map(_.toInt)
    val vec = Vector() ++ txt
    //dom.console.info(vec.mkString("[", ", ", "]"))
    implicit val row_count = led_height.value.toInt
    val m = getModeFrom(vec)
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, arduino_preview.width, arduino_preview.height)
    ctx.putImageData(applyImageData(readImageData, m.unapply), 0, 0)

  }

  def getModeFrom(result: Vector[Int] = Vector())(implicit row_count: Int): Transform =
    mode.value.toInt match {
      case 0 => RGBToSameColorLength(result)
      case 1 => RGBto323Byte(result)
      case 2 => RGB(result)
      case _ => null
    }

  //val mode = 0

  // ImageData, offset

  // Color, Length, x0, x1, x2, x3, xlength

  trait Transform {
    def apply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int)
    def unapply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int)
  }

  case class RGBToSameColorLength(var result: Vector[Int] = Vector())(implicit row_count: Int) extends Transform {
    var trsf = {
      val pixels = Array.fill(arduino_preview.width * arduino_preview.height)((0, 0, 0))
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
            val x = v / row_count
            val y = v % row_count
            pixels(x * row_count + y) = lastColor
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
        val pos = x * row_count + (row_count - 1 - y)
        map = map + (color -> (map.get(color).getOrElse(Vector()) :+ pos))
      }
    }
    def unapply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
      val pos = x * row_count + (row_count - 1 - y)
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
      //arr.data(p + 0) = (i / 60) * 4
      //arr.data(p + 1) = y * 40
      //arr.data(p + 2) = 0
      //arr.data(p + 3) = 0
      arr.data(p + 0) = r << 5
      arr.data(p + 1) = g << 6
      arr.data(p + 2) = b << 5
      val color = (r << 5) | (g << 3) | b

      //yield color
      //result = result :+ (x + 1) * 1000 + y
      result = result :+ color
    }
    //def unapply(arr: Seq[Int]) {

    //}

    def unapply(arr: ImageData, p: Int, offset: Int, x: Int, y: Int) {
      if (result.length > offset) {
        val color = result(offset)
        arr.data(p + 0) = (7 & (color >> 5)) << 5
        arr.data(p + 1) = (3 & (color >> 3)) << 6
        arr.data(p + 2) = (7 & (color >> 0)) << 5
        //if (offset == 0)
        //dom.console.info(color + " : " + arr.data(p + 0) + ", " + arr.data(p + 1) + ", " + arr.data(p + 2))
        //} else {
        //arr.data(p + 0) = 127
        //arr.data(p + 1) = 127
        //arr.data(p + 2) = 127
      }
    }
    override def toString() = result.mkString("{", ",", "}")
  }
  def applyImageData(arr: ImageData, f: (ImageData, Int, Int, Int, Int) => Unit) = {
    val w = arr.width
    val h = arr.height
    var i = 0
    val r = for (x <- 0 until w) {
      for (y <- (h - 1) to 0 by -1) {
        val offset = (x + w * y)
        val p = offset * 4
        f(arr, p, i, x, y)
        i += 1
      }
    }
    //arduino_code.value = 
    //for(var i = 0 to arr.)
    arr
  }
}

object Example extends JSApp {
  override def main() {
    //setupAngular()
    dom.window.onload = { (ev: Event) => new UISetup }
  }

  /*def setupAngular() {
    val module = Angular.module("ConverterApp", Seq("ngMaterial"))
    module.controller[ConverterController]
  }*/
}
