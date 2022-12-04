package exercises

import lectures.part4implicits.TypeClasses.{User, cem}

object EqualityPlayground extends App {

  /**
   * Equality
   * */

  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name && a.email == b.email
  }

  /*
    Exercise : implement the TC pattern for the Equality tc
  */

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer.apply(a, b)
  }

  val anotherCem = User("Cem", 31, "cem.yeniceri@gmail.com")
  private val cem: User = User("Cem", 32, "cem.yeniceri@unicraft.io")

  println(Equal.apply(cem, anotherCem)) // same
  println(Equal(cem, anotherCem)) // same

  /**
   * Exercise - improve the Equal TC with an implicit conversion class
   * ===(anotherValue: T)
   * !==(anotherValue: T)
   */

  implicit class TypeSafeEqual[T](value: T) {
    def ===(otherValue: T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(value, otherValue)

    def !==(otherValue: T)(implicit equalizer: Equal[T]): Boolean = !equalizer.apply(value, otherValue)
  }

  println(cem === anotherCem)

  /**
  cem.===(anotherCem)
   new TypeSafeEqual[User](cem).===(anotherCem)
   new TypeSafeEqual[User](cem).===(anotherCem)(NameEquality)
   * */

  /*
    TYPE SAFE
  */
  // println(cem == 43) will fail!!
  // println(cem === 43) will fail!!
}
