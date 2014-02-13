package ezoerner.cstarpath.structure

import org.scalatest._

/**
 * Tests for DefaultPath.
 *
 * @author Eric Zoerner <a href="mailto:eric.zoerner@gmail.com">eric.zoerner@gmail.com</a>
 */
class DefaultPathTest extends FlatSpec with Matchers {

  "fromEncodedPathString with trailing delimiter" should "produce correct path" in {
    val path = DefaultPath.fromEncodedPathString("x/y/")
    path.size should equal (2)
    path.toString should equal ("x/y/")
  }

  "fromEncodedPathString without trailing delimiter" should "produce correct path" in {
    val path = DefaultPath.fromEncodedPathString("x/y")
    path.size should equal (2)
    path.toString should equal ("x/y/")
  }

  "fromEncodedPathString with one element with trailing delimiter" should "produce correct path" in {
    val path = DefaultPath.fromEncodedPathString("x/")
    path.size should equal (1)
    path.toString should equal ("x/")
  }

  "fromEncodedPathString with one element without trailing delimiter" should "produce correct path" in {
    val path = DefaultPath.fromEncodedPathString("x")
    path.size should equal (1)
    path.toString should equal ("x/")
  }

  "convert from empty path" should "produce empty path" in {
    val path = DefaultPath.fromEncodedPathString("")
    path.size should equal (0)
    path.toString should equal ("/")
    path.isEmpty should be (true)
  }

  "convert from path with only delimiter" should "produce empty path" in {
    val path = DefaultPath.fromEncodedPathString("/")
    path.size should equal (0)
    path.toString should equal ("/")
    path.isEmpty should be (true)
  }
}

