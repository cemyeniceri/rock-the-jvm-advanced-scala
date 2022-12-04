package lectures.part5ts

object PathDependentTypes extends App {

  class Outer {
    class Inner

    object InnerObject

    type InnerType

    def print(i: Inner) = println(i)

    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String
    2
  }

  // per-instance
  private val o = new Outer
  private val inner = new o.Inner // o.Inner is a Type

  private val oo = new Outer
  // val otherInner: oo.Inner = new o.Inner -----------> NOT SAME TYPE

  o.print(inner)
  // oo.print(inner) --------------->> NOT SAME TYPE

  // path-dependent types

  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)

  /*
    Exercise
    DB keyed by Int or String, but maybe others
  */

  /*
    Hints!!
    use path-dependent types
    abstract type members and/or type aliases
  */


  private trait ItemLike {
    type Key
  }

  private trait Item[K] extends ItemLike {
    override type Key = K
  }

  private trait IntItem extends Item[Int]

  private trait StringItem extends Item[String]

  /*
  Note: this exercise (and its solution) is only applicable to Scala 2.
  Scala 3 considers abstract path-dependent types (aka general type projection) to be unsound:
  https://docs.scala-lang.org/scala3/reference/dropped-features/type-projection.html

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???
  get[IntItem](42) // ok
  get[StringItem]("home") // ok
  // get[IntItem]("scala") // not ok
*/

}
