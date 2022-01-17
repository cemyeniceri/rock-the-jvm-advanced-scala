package lectures.part2afp

// Partially Applied Functions
object CurriesPAF extends App {

  // curried functions!
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int = y => 3 + y

  println(add3(5))
  println(superAdder(3)(5)) // curried function

  // curried method!
  // METHOD!!!!!!
  def curriedAdder(x: Int)(y: Int): Int = x + y

  // METHOD is converted to functions here! which means lifting!
  val add4: Int => Int = curriedAdder(4)

  // Lifting = ETA-EXPANSION *-------> we are going to convert method to functions to use it in HOFs. JVM doesnt allow methods to use in HOFs.!!
  // def (METHOD) is an instance of a class in Scala....

  // FUNCTIONS != METHODS (JVM Limitation)
  def inc(x: Int) = x + 1

  List(1, 2, 3).map(inc) // Compiler does ETA-Expansion for us.. this equals to List(1,2,3).map(x => inc(x))

  // PARTIAL FUNCTION APPLICATIONS
  val add5 = curriedAdder(5) _ // _ tells compiler to do ETA-Expansion for me......

  // EXAMPLE
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // add7: Int => Int = y => y + 7
  // as many different implementations of add7 using the above ones as you can
  // be creative!!!

  val add7_1 = (x: Int) => simpleAddFunction(x, 7) // simplest
  val add7_2 = simpleAddFunction.curried(7)
  val add7_3 = curriedAddMethod(7) _                // PAF -> Alternative syntax for turning methods into function values
  val add7_4 = curriedAddMethod(7)                  // PAF -> Alternative syntax for turning methods into function values
  val add7_5 = curriedAddMethod(7)(_)               // PAF -> Alternative syntax for turning methods into function values
  val add7_6 = simpleAddMethod(7, _:Int)            // PAF -> Alternative syntax for turning methods into function values
  // force compiler to do : y => simpleAddMethod(7, y)
  val  add7_7 = simpleAddFunction(7, _:Int)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("Hello, I'm ", _: String, ", how are you?") // x: String => concatenator("Hello", x, "howareyou")
  println(insertName("Cem"))

  val fillInTheBlanks = concatenator("Hello, ", _: String, _: String) // (x, y) => concatenator("Hello", x, y)
  println(fillInTheBlanks("Cem", " Scala is awesome!"))

  // EXERCISES
  /*
  * 1- Process a list of numbers and return their string representations with different formats
  * Use the %4.2f, %8.6f and %14.12f with a curried function
  * ex :"%4.2f".format(Math.PI)
  *   */

  def curriedFormatter(formatter: String)(number: Double) = formatter.format(number)

  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") // lifting
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f")(_)

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  /*
  * 2- difference between
  * - functions vs methods
  * - parameters: by-name vs 0-lambda
  *  */

  def byName(n: => Int) = n + 1

  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42

  def parenMethod(): Int = 42

  /*
  * calling byName and byFunction
  * - int
  * - parenMethod
  * - lambda
  * - PAF
  * */

  byName(23) // OK
  byName(method) // OK
  byName(parenMethod()) // OK
  // byName(parenMethod) // not OK
  // byName(parenMethod _) // not OK
  // byName(() => 42) // not OK
  byName((() => 42) ()) // OK, because lambda is called immediately!

  // byFunction(42) // not OK because, it expects lambda func, but INT is provided
  // byFunction(method) // not OK because compiler does not do ETA-Expansion
  byFunction(parenMethod _) // OK
  byFunction(parenMethod) // OK
  byFunction(() => 46) // OK
}
