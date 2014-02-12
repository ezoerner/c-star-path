package ezoerner.cstarpath.structure

import java.net.URLEncoder
import java.nio.charset.Charset
import ezoerner.cstarpath.Path

/**
 * Implementation of Path used as column names in Cassandra for encoding structures and for querying elements
 * of a structured object.
 *
 * @author Eric Zoerner <a href="mailto:ezoerner@ebuddy.com">ezoerner@ebuddy.com</a>
 */
object DefaultPath {
  private val PathDelimiterChar: Char = '/'
  private val ListIndexPrefix: Char = '@'
  private val Utf8: String = (Charset forName "UTF-8").name

  /** Create a DefaultPath from un-encoded elements */
  def createPath(elements: String*): Path = new DefaultPath(elements.toList map(s => URLEncoder.encode(s, Utf8)))

  /** Create a DefaultPath from an integer index */
  def fromIndex(i: Int): Path = new DefaultPath(List(ListIndexPrefix.toString + i))

  def fromEncodedPathString(s: String): Path = new DefaultPath(s.split(PathDelimiterChar).toList)

  def getListIndex(pathElement: String): Int =
    if (pathElement.isEmpty || !pathElement.startsWith(ListIndexPrefix.toString))
      throw new IllegalArgumentException("illegal path: " + pathElement)
    else Integer.parseInt(pathElement)

  def isListIndex(s: String): Boolean = {
    def isPositiveInt(s: String) =
      try {
        Integer.parseInt(s) >= 0
      } catch {
        case _: NumberFormatException => false
      }

    s.head == ListIndexPrefix && isPositiveInt(s.tail)
  }
}
import DefaultPath._

class DefaultPath(protected[cstarpath] val encodedElements: List[String]) extends Path {

  override def ++(other: Path): Path = new DefaultPath(encodedElements ::: other.encodedElements)

  override def :+(element: String): Path = new DefaultPath(encodedElements :+ URLEncoder.encode(element, Utf8))

  override def :+(index: Int): Path = this ++ DefaultPath.fromIndex(index)

  override def headOption: Option[String] = encodedElements.headOption

  override def tail: Path = new DefaultPath(encodedElements.tail)

  override def drop(n: Int): Path = new DefaultPath(encodedElements drop n)

  override def startsWith(that: Path): Boolean = encodedElements startsWith that.encodedElements

  override def size: Int = encodedElements.size

  override def isEmpty: Boolean = size == 0

  override def toString: String = (encodedElements mkString PathDelimiterChar.toString) + "/"
}
