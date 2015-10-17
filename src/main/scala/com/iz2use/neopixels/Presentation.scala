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

  private val _width = mixin(margin.`0`)

  val arduino_code = style(_width, flex := "1")
  val arduino_preview = style(imageRendering.pixelated, border(1.px, solid, c"#000000"), display.inlineBlock)
  val video_preview = style(_width, maxHeight(40.vh))
  val img_preview = style(_width, maxHeight(40.vh))

  val row = mixin(display.flex, flex := "1", flexDirection.row)
  val rest = style(display.flex, flex := "1")
  val body = style(unsafeRoot("BODY")(display.flex, flexDirection.row, margin.`0`, padding.`0`, height(100.vh)))
  val INPUT = style(unsafeRoot("INPUT")(border.`0`))
  val TEXTAREA = style(unsafeRoot("TEXTAREA")(border.`0`))
  val instructions = style(display.flex, borderBottom(1.px, solid, c"#AAAAAA"), backgroundColor(c"#FFFFE0"))
  val main = style(row)
  val content = style(display.flex, flex := "1", flexDirection.column)
  val line = style(row)
  val toolbox = style(borderTop(1.px, solid, c"#AAAAAA"))
  val toolwrapper = style(margin(1.em, 0.em))
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
