package lectures.part5ts

import org.graalvm.compiler.nodeinfo.StructuralInput.Condition

object VarianceExample extends App {

  class Vehicle

  class Bike extends Vehicle

  class Car extends Vehicle

  class IList[T]

  /*
    1-
      Invariant, Covariant and Contravariant
      trait Parking[T](things: List[T]) {
          def park(vehicle: T): Unit

          def impound(vehicles: List[T]): Unit

          def checkVehicles(conditions: String): List[T]
        }
    2-
      use someone else's API : IList[T]
    3- Parking = monad!
        -flatMap
  */

  // PART - 1

  // Invariant
  class IParking[T](vehicles: List[T]) {
    def park(vehicle: T): IParking[T] = ???

    def impound(vehicles: List[T]): IParking[T] = ???

    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  // Covariant

  class CParking[+T](vehicle: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???

    def impound[S >: T](vehicles: List[S]): CParking[S] = ???

    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  private val value: CParking[Vehicle] = new CParking[Car](List(new Car()))
  value.park(new Bike())

  class XParking[-T](vehicle: List[T]) {
    def park(vehicle: T): XParking[T] = ???

    def impound(vehicles: List[T]): XParking[T] = ???

    def checkVehicles[S <: T](conditions: String): List[S] = ???

    def flatMap[R <: T, S](f: R => XParking[S]): XParking[S] = ???
  }

  /*
  * Rule of Thumb
    - use covariance - Collection of things
    - use contravariance - GROUP OF ACTIONS
  */


  // PART - 2
  class CParking2[+T](vehicle: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???

    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ???

    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }


  class XParking2[-T](vehicle: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???

    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???

    def checkVehicles[S <: T](conditions: String): IList[S] = ???
  }

  // PART - 3
}
