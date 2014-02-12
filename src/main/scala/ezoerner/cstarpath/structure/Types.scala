package ezoerner.cstarpath.structure

/**
 * Utility methods for working with types within structured objects.
 *
 * @author Eric Zoerner <a href="mailto:eric.zoerner@gmail.com">eric.zoerner@gmail.com</a>
 */
private[structure] object Types {
  val LIST_TERMINATOR_VALUE: String = "\uFFFF\uFFFF"

  def isSimple(obj: Any): Boolean = obj match {
    case null => true
    case _: String => true
    case _: Boolean => true
    case _: Int => true
    case _: Char => true
    case _: Long => true
    case _: Double => true
    case _: Float => true
    case _: Short => true
    case _: Byte => true
    case _: Unit => true
    case _ => false
  }

  def isListTerminator(value: Any): Boolean = {
    return value == LIST_TERMINATOR_VALUE
  }
}