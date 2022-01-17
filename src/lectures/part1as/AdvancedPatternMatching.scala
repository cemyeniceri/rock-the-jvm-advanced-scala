package lectures.part1as

object AdvancedPatternMatching extends App {

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"the only element is $head")
    case _ =>
  }

  /*
  * - constants
  * - wildcards
  * - case classes
  * - tuples
  * - some special magic like above
  */

  class Person(val name: String, val age: Int) // not case class

  object Person {
    def unapply(person: Person): Option[(String, Int)] =
      if (person.age > 20) Some((person.name, person.age))
      else None

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n, I am $a years old."
  }

  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"my legal status is $status"
  }

  println(legalStatus)


  /* Exercise
  * - Design below mathPropertyBad without using annoying IFs statement by using elegant pattenr matching which we learnt recently */
  val n: Int = 8
  val mathPropertyBad = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "an even number"
    case _ => "no property"
  }

  object even {
    def unapply(arg: Int): Option[Boolean] =
      if (arg % 2 == 0) Some(true)
      else None
  }

  object singleDigit {
    def unapply(n: Int): Option[Boolean] =
      if (n > -10 && n < 10) Some(true)
      else None
  }

  val matchPropertyCool = n match {
    case singleDigit(_) => "single digit"
    case even(_) => "an even number"
    case _ => "no property"
  }

  println(matchPropertyCool)

  // infix patterns
  case class Or[A, B](a: A, b: B) // like Either

  val either = Or(2, "two")
  val humanDescription = either match {
    case number Or string => s"$number is written as $string" // syntax sugar
  }
  println(humanDescription)

  // decomposing sequences
  val varArg = numbers match {
    case List(1, _*) => "starting with 1"
  }

  abstract class MyList[+A] {
    def head: A = ???

    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]

  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]


  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if (list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) => "starting with 1,2"
    case _ => "sth else"
  }

  println(decomposed)

  // custom return types for unapply
  // isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean

    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      def isEmpty = false

      def get = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's is $n"
    case _ => "An alien"
  })
}
