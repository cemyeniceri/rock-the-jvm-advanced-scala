package lectures.part4implicits

object OrganizingImplicits extends App {

  implicit def reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  // implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)  // : same with this
  // implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _)  // if you define second implicit, compiler will crash
  println(List(1, 2, 3, 45, 65, 4).sorted)

  // If you don't define, it will come from scala.Predef

  /*
    Implicits (used as implicit parameters):
      - val/var
      - object
      - accessor methods = defs with NO PARENTHESIS
  */

  // Exercise
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Cem", 31),
    Person("Tolga", 32),
    Person("Veli", 30)
  )

  /*  object Person{
      implicit val alphabeticalOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
    }*/
  // implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.age < b.age)
  // println(persons.sorted)

  /*
      Implicit scope
        - normal scope
        - imported scope
        - companions of all types involved in the method signature
          * ()
          - List
          - Ordering
          - all the types involved = A or any supertype
  */

  object AlphabeticalOrdering {
    implicit val alphabeticalOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering {
    implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.age < b.age)
  }

  import AgeOrdering.*

  println(persons.sorted)

  /**
   * Exercise
   *  - totalPrice = most used (%50)
   *  - by unitCount = (%25)
   *  - by unitPrice = (%25)
   */
  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => (a.unitPrice * a.nUnits) < (b.unitPrice * b.nUnits))
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.nUnits < b.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }
}
