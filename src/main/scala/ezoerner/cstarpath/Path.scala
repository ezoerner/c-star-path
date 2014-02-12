package ezoerner.cstarpath

/**
 * A hierarchical path used for locating structured data.
 *
 * @author Eric Zoerner <a href="mailto:eric.zoerner@gmail.com">eric.zoerner@gmail.com</a>
 */
trait Path {
  /** Return a new Path consisting of this Path concatenated with another Path. */
  def ++(other: Path): Path

  /** Return a new Path consisting of this Path concatenated with the specified (unencoded) elements. */
  def :+(element: String): Path

  /** Return a new Path consisting of this Path concatenated with the specified list indices as elements. */
  def :+(index: Int): Path

  /**
   * Optionally selects the first element.
   *  $orderDependent
   *  @return  the first element of this path if it is nonempty,
   *           `None` if it is empty.
   */
  def headOption: Option[String]

  /** Selects all elements except the first.
    *  $orderDependent
    *  @return  a Path consisting of all elements of this Path
    *           except the first one.
    *  @throws `UnsupportedOperationException` if the $coll is empty.
    */
  def tail: Path


  /** Selects all elements except first ''n'' ones.
    *  $orderDependent
    *  @param  n    the number of elements to drop from this Path.
    *  @return a Path consisting of all elements of this Path except the first `n` ones, or else the
    *          empty Path, if this Path has less than `n` elements.
    */
  def drop(n: Int): Path

  /** Tests whether this Path starts with the given Path.
    *
    * @param  that    the Path to test
    * @return `true` if this Path has `that` as a prefix, `false` otherwise.
    */
  def startsWith(that: Path): Boolean

  /**
   * Return the number of elements.
   */
  def size: Int

  /** Tests whether the Path is empty.
    *
    *  @return    `true` if the Path contains no elements, `false` otherwise.
    */
  def isEmpty: Boolean

  /** Get the encoded elements of this Path. */
  protected[cstarpath] def encodedElements: List[String]
}
