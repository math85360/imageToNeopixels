package com.iz2use.neopixels
import scalajs.js
/**
 * @author mathi_000
 */
trait UserMedia extends js.Object {
  def apply(data: js.Any, callback: js.Function1[js.Any, _], error: js.Function0[_]): Unit = js.native
}
trait WindowURL extends js.Object {
  def createObjectURL(stream: Any): String = js.native
}

trait RequestAnimationFrame extends js.Object {
  def apply(callback: js.Function1[Double, _]): Int = js.native
}