package lectures.part5ts

object SelfTypes extends App {

  // requiring a type to be mixed in


  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer {
    self: Instrumentalist => // SELF TYPE whoever implements Singer to implement Instrumentalist

    // rest of the implementation or API
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  // NOT Compilable --> missing Instrumentalist
  /*  class Vocalist extends Singer {
      override def sing(): Unit = ???
    }*/

  private val jamesHetfield: Singer with Instrumentalist = new Singer with Instrumentalist {
    override def sing(): Unit = ???

    override def play(): Unit = ???
  }

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("(guitar solo)")
  }

  val ericClapton = new Guitarist with Singer {
    override def sing(): Unit = ???
  }

  // vs inheritance
  class A

  class B extends A // B is an A

  trait T

  trait S {
    self: T => // S requires a T
  }

  // CAKE PATTERN like => "Dependency Injection"

  // DI
  class Component {
    // API
  }

  class ComponentA extends Component

  class ComponentB extends Component

  class DependentComponent(val component: Component)

  // CAKE Pattern
  private trait ScalaComponent {
    def action(x: Int): String
  }

  private trait ScalaDependentComponent {
    self: ScalaComponent =>
    def dependentAction(x: Int): String = action(x) + " this rocks!"
  }


  private trait ScalaApplication {
    self: ScalaDependentComponent with ScalaComponent =>
  }

  // layer 1 - small components
  private trait Picture extends ScalaComponent

  private trait Stats extends ScalaComponent

  // layer 2 - compose
  private trait Profile extends ScalaDependentComponent with Picture

  private trait Analytics extends ScalaDependentComponent with Stats

  // layer 3 - app
  private trait AnalyticsApp extends ScalaApplication with Analytics


  // cyclical dependencies
  // NOT POSSIBLE TO COMPILE THIS !!!!!
  /*
    class X extends Y
    class Y extends X
  */

  // THIS IS ABSOLUTELY OK !!!!
  private trait X {
    self: Y =>
  }

  private trait Y {
    self: X =>
  }

  // CAKE PATTERN => works in compile-time
  // DI           => works in run-time
}
