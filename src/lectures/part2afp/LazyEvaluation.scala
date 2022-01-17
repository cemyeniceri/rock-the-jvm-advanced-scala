package lectures.part2afp

object LazyEvaluation extends App {

  // lazy values are evaluated once but only when they're used for the first time
  // this DELAYS the evaluation of values
  lazy val x: Int = {
    println("hello")
    42
  }

  println(x)
  println(x)

  // examples of implications:
  // side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if (simpleCondition && lazyCondition) "yes" else "no")

  // in conjunction with call by name
  def byNameMethodWithoutLazyVal(n: => Int): Int = n + n + n + 1

  def byNameMethodLazyVal(n: => Int): Int = {
    // CALL BY NEED
    lazy val t = n // only evaluated once
    t + t + t + 1
  }

  def retrieveMagicValue = {
    // side effect or a long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethodWithoutLazyVal(retrieveMagicValue))
  println("--------------------------------------------")
  println(byNameMethodLazyVal(retrieveMagicValue))
  // use lazy vals

  // filtering with lazy vals
  def lessThan30(n: Int): Boolean = {
    println(s"$n is less than 30?")
    n < 30
  }

  def greaterThan20(n: Int): Boolean = {
    println(s"$n is greater than 20?")
    n > 20
  }

  println("--------------------------------------------")

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20)

  println("--------------------------------------------")

  val lt30Lazy = numbers.withFilter(lessThan30) // use lazy vals under the hood
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  gt20Lazy.foreach(println)

  // for-comprehensions use withFilter with guards
  for {
    a <- List(1, 25, 40, 5, 23) if a % 2 == 0 // use lazy vals!
  } yield a + 1

  // equals
  List(1, 25, 40, 5, 23).withFilter(_ % 2 == 0).map(_ + 1) // List[Int]
}