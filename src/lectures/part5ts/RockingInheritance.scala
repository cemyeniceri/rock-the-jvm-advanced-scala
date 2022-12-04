package lectures.part5ts

object RockingInheritance extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem
  trait Animal {
    def name: String
  }

  trait Lion extends Animal {
    override def name: String = "lion"
  }

  trait Tiger extends Animal {
    override def name: String = "tiger"
  }

  class IbineTogi extends Lion with Tiger {
    override def name: String = "Ibine togi"
  }

  class Mutant extends Lion with Tiger

  val ibineTogi = new IbineTogi
  println(ibineTogi.name)


  val m = new Mutant
  println(m.name)

  /*
  Mutant
  extends Animal with { override def name: String = "lion" }
  with { override def name: String = "tiger" }

  LAST OVERRIDE GETS PICKED
  * */

  // the super problem + type linearization
  trait Cold {
    def print = println("cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }


  class Red {
    def print = println("red")
  }

  class White extends Red with Green with Blue {
    override def print = {
      println("white")
      super.print
    }
  }

  val whiteColor = new White
  whiteColor.print

  /*
  Cold = AnyRef with <Cold>
  Green = Cold with <Green>
        = AnyRef with <Cold> with <Green>

  Blue = Cold with <Blue>
        = AnyRef with <Cold> with <Blue>

  Red = AnyRef with <Red>

  White = Red with Green with Blue with <White>
        = AnyRef with <Red>
          with (AnyRef with <Cold> with <Green>)
          with (AnyRef with <Cold> with <Blue>)
          with <White>
        = AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>

      --->> skip the repeated elements

    result = white, blue, green, cold
  * */
}
