package ezoerner.cstarpath.structure

import org.scalatest._

/**
 * Tests for DefaultPath.
 *
 * @author Eric Zoerner <a href="mailto:eric.zoerner@gmail.com">eric.zoerner@gmail.com</a>
 */
class DefaultPathTest extends FlatSpec with Matchers {

  "fromEncodedPathString with trailing delimiter" should "produce correct path size and string" in {
    val path = DefaultPath.fromEncodedPathString("x/y/")
    path.size should equal (2)
    path.toString should equal ("x/y/")
  }

  "fromEncodedPathString without trailing delimiter" should "produce correct path size and string" in {
    val path = DefaultPath.fromEncodedPathString("x/y")
    path.size should equal (2)
    path.toString should equal ("x/y/")
  }
}

