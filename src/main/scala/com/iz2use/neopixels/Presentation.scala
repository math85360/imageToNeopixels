package com.iz2use.neopixels

import scalacss.Defaults._
import scalacss.Attr
import scalacss.Transform
import scalacss.CanIUse
import scala.language.postfixOps

/**
 * @author mathi_000
 */
object Presentation extends StyleSheet.Inline {
  import dsl._

  private val _width = mixin(margin.`0`, maxWidth(40.vw), maxHeight(40.vh))

  val arduino_code = style(_width, width(40.vw), height(40.vh))
  val arduino_preview = style(imageRendering.pixelated, border(1.px, solid, c"#000000"))
  val video_preview = style(_width)
  val img_preview = style(_width)
  /*
  private val _full = mixin(margin.`0`, padding.`0`, border.`0`)

  val cBackground = c"#FFFFE0"

  val cText = c"#000000"

  val main = style(_full)

  val rendered = style(
    unsafeChild("table")(borderCollapse.collapse),
    unsafeChild("th,td")(border(1 px, solid, blue)),
    unsafeChild(">*")( //padding(0 em, 5 vw) //,mobile(padding.`0`)
    ))

*/
}
