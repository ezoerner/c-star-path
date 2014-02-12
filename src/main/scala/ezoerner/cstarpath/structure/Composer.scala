package ezoerner.cstarpath.structure

import org.slf4j.{LoggerFactory, Logger}
import ezoerner.cstarpath.Path

/**
 * Support for composing paths back to complex objects.
 * Only the basic JSON structures are supported, i.e. Maps, Lists, Strings, Numbers, Booleans, and null.
 *
 * It is possible to write data that will cause inconsistencies in an object structure
 * when it is reconstructed. This implementation will resolve inconsistencies as follows:
 *
 * If data objects are found at a particular path as well as longer paths, the data object
 * is returned in a map structure with the special key "@ROOT". This may cause an error
 * if the data is later attempted to be deserialized into a POJO.
 *
 * If list elements are found at the same level as longer paths or a data object, then
 * the list elements are returned in a map with the index as keys in the map, e.g. "@0", "@1",
 * etc.
 *
 * If inconsistencies such as these are preventing data from being deserialized into a
 * particular POJO, the data can always be retrieved using an instance of (a subclass of) TypeReference<Object>,
 * which will return the basic JSON to Java mappings, i.e. Maps, Lists and Strings, etc.
 *
 * @author Eric Zoerner <a href="mailto:eric.zoerner@gmail.com">eric.zoerner@gmail.com</a>
 */
object Composer {
  private val log: Logger = LoggerFactory.getLogger(classOf[Composer])

  private val InconsistentRoot: String = "@ROOT"
}

class Composer {

  /**
   * Compose a map of simple objects keyed by paths into a single complex object, e.g. a map or list
   *
   * @param simpleObjects input map of decomposed objects, paths mapped to simple values (i.e. strings, numbers, or booleans)
   * @return a complex object such as a map or list decoded from the paths in decomposedObjects,
   *         or null if decomposedObjects is empty.
   * @throws IllegalArgumentException if there are unsupported objects types in decomposedObjects, or
   *                                  if there is a key that is an empty path
   */
  def compose(simpleObjects: Map[Path, Any]): Any = {

    if (simpleObjects.isEmpty)
      Map.empty
    // if this is a singleton map with an empty path as the key, then this represents itself a
    // simple object, so just return the value
    else if (simpleObjects.size == 1 && simpleObjects.head._1.isEmpty)
      simpleObjects.head._2
    else
      // decompose into nested maps by merging the partial map from each path.
      // After composing into nested maps, go through the tree structure and transform SortedMaps into Lists.
      // The reason for a two-pass approach is that the lists may be "sparse" due to deleted indexes, and
      // this is difficult to handle in one pass.
      transformLists(composeMap(simpleObjects))
  }

  private def composeMap(simpleObjects: Map[Path, Any]): Map[String, Any] = {

    def mergeAll(entries: Map[Path, Any], composition: Map[String, Any]): Map[String,Any] =
      if (entries.isEmpty)
        composition
      else
        mergeAll(entries.tail, mergeOne(entries.head, composition))

    mergeAll(simpleObjects, Map.empty)
  }

  private def mergeOne(simpleEntry: (Path, Any), compositionMap: Map[String, Any]): Map[String, Any] = {
    val path: Path = simpleEntry._1
    val head: String = path.headOption.get

    val nextLevelComposition: Option[Any] = compositionMap.get(head)
    val tail: Path = path.tail
    val simpleValue: Any = simpleEntry._2

    if (!nextLevelComposition.isDefined)
      mergeEntryIntoEmptySlot(compositionMap, head, tail, simpleValue)
    else if (Types isSimple nextLevelComposition)
      mergeEntryWithSimple(compositionMap, nextLevelComposition, head, tail, simpleValue)
    else
      mergeEntryWithStructure(nextLevelComposition.asInstanceOf[Map[String, AnyRef]], tail, simpleValue)
  }

  private def mergeEntryIntoEmptySlot(composition: Map[String, Any],
                                      head: String,
                                      tail: Path,
                                      simpleValue: Any): Map[String, Any] =
    if (tail.isEmpty)
      composition + ((head, simpleValue))
    else {
      composition + ((head, composeMap(Map((tail, simpleValue)))))
    }

  private def mergeEntryWithSimple(composition: Map[String, Any],
                                   nextLevelComposition: Any,
                                   head: String,
                                   tail: Path,
                                   simpleValue: Any): Map[String, Any] =
    if (tail.isEmpty)
      // merging two simple values at same level, this cannot happen because map keys are unique
      throw new IllegalStateException("two simple values at same level -- impossible!")
    else {
      // merging longer path with simple value
      val map: Map[String, Any] = composeMap(Map((tail, simpleValue)))
      // replace the simple value with map containing simple value and inconsistent root
      composition + ((head, map + ((Composer.InconsistentRoot, nextLevelComposition))))
    }

  private def mergeEntryWithStructure(nextLevelComposition: Map[String, Any],
                                      tail: Path,
                                      simpleValue: Any): Map[String, Any] =
    if (tail.isEmpty) {
      // INCONSISTENCY!! there is a simple value at the same level as a complex object
      // Resolve this by putting this value at the special key "@ROOT" inside the complex object.
      val previousValue: Option[Any] = nextLevelComposition get Composer.InconsistentRoot
      // not possible to have a previous value, there can only be one key with this path
      assert(!previousValue.isDefined)
      nextLevelComposition + ((Composer.InconsistentRoot, simpleValue))
    } else
      // simply advance to next level since the head matches a key already there
      mergeOne(((tail, simpleValue)), nextLevelComposition)

  private def transformLists(map: Map[String, Any]): Any =
    if (map.keys forall(DefaultPath.isListIndex(_)))
      transformActualList(map)
    else
      ???


  private def transformActualList(map: Map[String, Any]): Any = ???

}
