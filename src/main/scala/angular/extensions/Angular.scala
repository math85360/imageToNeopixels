package angular.extensions

import scalatags.Text.all._

/**
 * @author mathi_000
 */
object ScalaTagsAngular {
  implicit def enrichString(s: String) = RichString(s)

  case class RichString(s: String) {
    def !(that: String) = s + " " + that
  }
  object typ extends Attr("type") {
    val text = this := "text"
    val checkbox = this := "checkbox"
    val radio = this := "radio"
  }
  object ng extends NGAttrs with NGTags {

  }
  object rn {
    val Carousel = "rn-carousel".attr := "rn-carousel"
    val CarouselControls = "rn-carousel-controls".attr := "rn-carousel-controls"
  }
  object md extends MDAttrs with MDTags {
    val Accent = "md-accent"
    val Actions = "md-actions"
    //val column = "column"
    val Fab = "md-fab"
    val Fling = "md-fling"
    val Hue1 = "md-hue-1"
    val Hue2 = "md-hue-2"
    val Hue3 = "md-hue-3"
    val IconButton = "md-icon-button"
    //val indeterminate = "indeterminate"
    val Left = "md-left"
    val Mini = "md-mini"
    val Primary = "md-primary"
    val Raised = "md-raised"
    val Right = "md-right"
    //val row = "row"
    //val spaceAround = "space-around"
    val ToolbarTools = "md-toolbar-tools"
    val Warning = "md-warn"
  }
}
trait MDAttrs {
  val AutocompleteSnap = "md-autocomplete-snap".attr := "md-autocomplete-snap"

  //val flex = "flex".attr := "flex"
  object flex extends deviceLayout("flex") {

  }

  object layout extends deviceLayout("layout") {
    //val default = this := "layout"
    val row = this := "row"
    val column = this := "column"
  }
  //val layout = "layout".attr := "layout"
  /*object layout extends deviceLayout("layout") {
    val column = this := "column"
  }*/
  object layoutAlign extends Attr("layout-align") {
    val spaceAround = this := "space-around"
  }
  val layoutFill = "layout-fill".attr := "layout-fill"

  object Mode extends Attr("md-mode") {
    val indeterminate = this := "indeterminate"
  }

  val IsOpen = "md-is-open".attr

  val SvgSrc = "md-svg-src".attr

  val SvgIcon = "md-svg-icon".attr

  val icon = "icon".attr

  val label = "label".attr

  val required = "required".attr := "required"

  val fontIcon = "md-font-icon".attr
  object direction extends Attr("md-direction") {
    val left = this := "left"
    val right = this := "right"
    val top = this := "top"
    val down = this := "down"
  }
  trait MoreFunctions {

  }
  class withDefault(attr: String) extends Attr(attr) {
    val default = this := attr
  }
  class deviceLayout[A <: MoreFunctions](attr: String) extends withDefault(attr) {
    /**
     * Small (less than 600px wide)
     */
    object Sm extends withDefault(attr + "-sm")
    /**
     * Greater than small (greater than 600px wide)
     */
    object GtSm extends withDefault(attr + "-gt-sm")
    /**
     * Medium (between 600px and 960px wide)
     */
    object Md extends withDefault(attr + "-md")
    /**
     * Greater than medium (greater than 960px wide)
     */
    object GtMd extends withDefault(attr + "-gt-md")
    /**
     * Large (between 960px and 1200px)
     */
    object Lg extends withDefault(attr + "-lg")
    /**
     * Greater than Large (greater than 1200px)
     */
    object GtLg extends withDefault(attr + "-gt-lg")
  }
  object Hide extends deviceLayout("hide")
  object Show extends deviceLayout("show")
  object cols extends deviceLayout("md-cols")
  object rowHeight extends deviceLayout("md-row-height")
  //val cols = "md-cols".attr
  //val colsGtMd = "md-cols-gt-md".attr
  //val colsGtSm = "md-cols-gt-sm".attr
  //val colsMd = "md-cols-md".attr
  //val colsLg = "md-cols-lg".attr
  //val colsGtLg = "md-cols-gt-lg".attr
  //val rowHeight = "md-row-height".attr
  //val rowHeightGtMd = "md-row-height-gt-md".attr
  //val gutter = "md-gutter".attr
  //val gutterGtSm = "md-gutter-gt-sm".attr
  object gutter extends deviceLayout("md-gutter")

  val DynamicHeight = "md-dynamic-height".attr := "md-dynamic-height"

  val BorderBottom = "md-border-bottom".attr := "md-border-bottom"

  val placeholder = "placeholder".attr
  val deleteButtonLabel = "delete-button-label".attr
  val deleteHint = "deleteHint".attr
  val secondaryPlaceholder = "secondary-placeholder".attr
}

trait MDTags {
  val Autocomplete = "md-autocomplete".tag
  val Button = "md-button".tag
  val Checkbox = "md-checkbox".tag
  val Content = "md-content".tag
  val Card = "md-card".tag
  val CardContent = "md-card-content".tag
  val Chips = "md-chips".tag
  val ChipTemplate = "md-chip-template".tag
  val Dialog = "md-dialog".tag
  val DialogContent = "md-dialog-content".tag
  val Divider = "md-divider".tag
  val FabActions = "md-fab-actions".tag
  val FabSpeedDial = "md-fab-speed-dial".tag
  val FabToolbar = "md-fab-toolbar".tag
  val FabTrigger = "md-fab-trigger".tag
  val GridList = "md-grid-list".tag
  val GridTile = "md-grid-tile".tag
  val GridTileFooter = "md-grid-tile-footer".tag
  val GridTileHeader = "md-grid-tile-header".tag
  val Icon = "md-icon".tag
  val InputContainer = "md-input-container".tag
  val List = "md-list".tag
  val ListItem = "md-list-item".tag
  val Option = "md-option".tag
  val ProgressCircular = "md-progress-circular".tag
  val Select = "md-select".tag
  val Sidenav = "md-sidenav".tag
  val Tab = "md-tab".tag
  val Tabs = "md-tabs".tag
  val Toolbar = "md-toolbar".tag
}

trait NGTags {
  val MdIcon = "ng-md-icon".tag
}
trait NGAttrs {
  val App = "ng-app".attr
  val BindHtml = "ng-bind-html".attr
  val Click = "ng-click".attr
  val Cls = "ng-class".attr
  val Controller = "ng-controller".attr
  val Disabled = "ng-disabled".attr
  val Hide = "ng-hide".attr
  val Href = "ng-href".attr
  val If = "ng-if".attr
  val Init = "ng-init".attr
  val Model = "ng-model".attr
  val Repeat = "ng-repeat".attr
  val Show = "ng-show".attr
  val Src = "ng-src".attr
  val Submit = "ng-submit".attr
  val View = "ng-view".attr := "ng-view"

  val Messages = "ng-messages".attr
  object Message extends Attr("ng-message") {
    val required = this := "required"
    val empty = this := ""
    val pattern = this := "pattern"
    val minLength = this := "minlength"
  }
  val MinLength = "ng-minlength".attr
  val MaxLength = "ng-maxlength".attr
  val MessageExp = "ng-message-exp".attr
  val Pattern = "ng-pattern".attr
  val Required = "ng-required".attr := "true"
}