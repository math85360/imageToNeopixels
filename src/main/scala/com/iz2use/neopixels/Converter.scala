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
  val pause = button(b("||"), disabled, onclick := (() => if (video_preview.paused) video_preview.play() else video_preview.pause())).render
  val camera = button("camera", onclick := (() => switchToCamera)).render

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
              ol("start".attr := "3", li("Nombre de leds par ligne et par colonne modifiables en bas à droite"),
                li("Glisser-déposer une image/video sur cette page")))),
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
            label("x"), led_height,
            pause, camera)))).render)
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
      if ((ev.buttons == 0) && !paint_check.checked) {
        paint_r.value = arr.data(0).toString
        paint_g.value = arr.data(1).toString
        paint_b.value = arr.data(2).toString
      } else if ((ev.buttons == 1) && paint_check.checked) {
        arr.data(0) = paint_r.value.toInt
        arr.data(1) = paint_g.value.toInt
        arr.data(2) = paint_b.value.toInt
        arr.data(3) = 255
        ctx.putImageData(arr, x, y)
        implicit val size = Size(led_width.value.toInt, led_height.value.toInt)
        val m = getModeFrom()
        ctx.putImageData(applyImageData(readImageData, m.apply), 0, 0)
        arduino_code.value = m.toString()
      }
    }
  }
  def clear() {
    img_preview.style.display = "none"
    video_preview.style.display = "none"
  }
  def switchToCamera {
    if (!video_preview.paused) video_preview.ended = true
    var camId: String = ""
    dom.console.info("goto camera")
    dom.window.asInstanceOf[js.Dynamic].MediaStreamTrack.getSources({ (data: js.Array[js.Dynamic]) =>
      dom.console.info(data.mkString(","))
      for (x <- data) {
        if (x.kind == "video") {
          camId = x.id.asInstanceOf[String]
        }
      }
      dom.navigator.asInstanceOf[js.Dynamic].webkitGetUserMedia(
          js.Dynamic.literal(video = js.Dynamic.literal(optional = js.Array(js.Dynamic.literal(sourceId = camId)))), { (stream: js.Any) =>
        dom.window.setTimeout(() => startVideo(dom.window.asInstanceOf[js.Dynamic].webkitURL.createObjectURL(stream).asInstanceOf[String]), 1000)
      }, { () => })
    })
  }

  def startVideo(src: String) {
    pause.disabled = false
    video_preview.src = src
    video_preview.style.display = "initial"
    dom.window.requestAnimationFrame((x: Double) => drawVideo(x))
    //video_preview.onended = ((ev: dom.raw.Event) => dom.window.cancelAnimationFrame(interval))
    video_preview.play()
  }
  def handleFiles(files: FileList) {
    clear()
    if (files.length > 0) {
      val file = files.item(0)
      val reader = new FileReader()
      if (file.`type`.startsWith("image")) {
        reader.onload = { (ev: UIEvent) =>
          pause.disabled = true
          img_preview.src = ev.target.asInstanceOf[js.Dynamic].result.asInstanceOf[String]
          img_preview.style.display = "initial"
          dom.window.setTimeout(() => drawImage, 0)
        }
      } else if (file.`type`.startsWith("video")) {
        reader.onload = { (ev: UIEvent) =>
          startVideo(ev.target.asInstanceOf[js.Dynamic].result.asInstanceOf[String])
        }
      }
      reader.readAsDataURL(file)
    }
  }

  def drawVideo(x: Double) {
    drawImage
    if (!video_preview.ended) dom.window.requestAnimationFrame((x: Double) => drawVideo(x))
  }
  def drawImage {
    setSize
    implicit val ctx = arduino_preview.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    //ctx.drawImage(img_preview, 0, 0, img_preview.naturalWidth, img_preview.naturalHeight, 0, 0, 60, 7)
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, arduino_preview.width, arduino_preview.height)
    val (src, w, h) =
      if (video_preview.style.display == "none")
        (img_preview, img_preview.width.toDouble, img_preview.height.toDouble)
      else
        (video_preview, video_preview.offsetWidth.toDouble, video_preview.offsetHeight.toDouble)
    //val w = img_preview.width.toDouble
    //val h = img_preview.height.toDouble
    if (w > 0 && h > 0) {
      val ratio = w / h
      val maxw = arduino_preview.width
      val maxh = arduino_preview.height
      if (fit_in_size.checked)
        ctx.drawImage(src, 0, 0, maxw, maxh)
      else if (ratio * maxh > maxw) {
        //ctx.drawImage(img_preview, 0, 0, arduino_preview.width, arduino_preview.height)
        dom.console.info(ratio + " " + maxw / ratio + " / " + maxh)
        ctx.drawImage(src, 0, 0, maxw / ratio, maxh)
      } else {
        dom.console.info(ratio + "x" + ratio * maxh + " / " + maxh)
        ctx.drawImage(src, 0, 0, ratio * maxh, maxh)
      }
      //if(ratio)
      //dom.console.info(ratio)
      //val m = RGBto323Byte()
      implicit val size = Size(led_width.value.toInt, led_height.value.toInt)
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
    implicit val size = Size(led_width.value.toInt, led_height.value.toInt)
    val m = getModeFrom(vec)
    ctx.fillStyle = "black"
    ctx.fillRect(0, 0, arduino_preview.width, arduino_preview.height)
    ctx.putImageData(applyImageData(readImageData, m.unapply), 0, 0)

  }

  def getModeFrom(result: Vector[Int] = Vector())(implicit size: Size): Transform =
    mode.value.toInt match {
      case 0 => RGBToSameColorLength(result)
      case 1 => RGBto323Byte(result)
      case 2 => RGB(result)
      case _ => null
    }

  //val mode = 0

  // ImageData, offset

  // Color, Length, x0, x1, x2, x3, xlength

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
