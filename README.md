The original C* Path project was written in Java and was located at github.com ebuddy/c-star-path.
Although the ebuddy github account is now closed, the Java project can still be found on github
at [ezoerner/c-star-path-j](https://github.com/ezoerner/c-star-path-j).

The project located here is a port in progress to Scala.
Because this port is forward-looking, the Cassandra Thrift API is not supported, only CQL.

C* Path
=======

Support for reading and writing structured objects in Cassandra.
Structured objects can be accessed whole or in part by hierarchical paths.

For more information see the blog post at the [eBuddy Tech Blog](http://tech.ebuddy.com/2013/10/28/overview-of-c-path/).
There is also a presentation about C* Path from [Cassandra Summit Europe 2013](http://www.slideshare.net/techblog/c-path).

On `writeToPath`, the domain object is first converted into a tree model with the help of
[Jackson JSON Processor](http://wiki.fasterxml.com/JacksonHome) (the fasterxml.com version).
How objects are converted can be customized by using annotations supported by Jackson. These structures are then
decomposed into key-value pairs where the keys are paths.

Note: Under investigation for the Scala port is which of the many options for JSON libraries to use.
The current plan is to use spray-json.

On `readFromPath`, the reverse process is used to recompose the key-value pairs back into a domain object.

Paths can be used to access structured data at different levels within the structure. A Path can also contain a special
element that refers to an index within a list (or array or collection).

**Note:** Special support for Sets of simple values is also planned but not yet implemented.
In the meantime, as a workaround, sets can be modeled as maps.

###Example:

    case class Class1(val a: Class2)
    case Class2(val b: List[Class3])
    case Class3(val c: Int)

    Class1(
      a=Class2(
        b=List(Class3(c=42),
               Class3(c=43)))
               
would be decomposed into the following key-value pairs:

`a/b/@0/c/ -> 42`  
`a/b/@1/c/ -> 43`


Maven Dependency
--------------
The artifacts are not yet published at Maven Central, but will be once they are functional.
To Be Determined: The syntax for including C* Path in SBT.

api module
----------
The main interface `StructuredDataAccessSupport` and helper classes.

cql module
----------
Implementation of `StructuredDataAccessSupport` for CQL3. Uses the
[Datastax Java Driver](https://github.com/datastax/java-driver) for transport and low level operations.

To use structured data in a CQL3 table, the following data modeling rules apply:

* The table should have one path column that is the first clustering key, i.e. the second column in the primary
  key after the partition key.
* There should be one other column for the values.
* The path and value columns should be typed as a textual type.

Note: The tests include system tests that run an embedded Cassandra.
These tests are in the "system" TestNG test group.

