package lectures.part5ts

object FBoundedPolymorphism extends App {

  /*
    trait Animal {
      def breed: List[Animal]
    }

    class Cat extends Animal {
      override def breed: List[Animal] = ???
    }

    class Dog extends Animal {
      override def breed: List[Animal] = ???
    }
  */


  // Solution 1 - NAIVE
  /*
    trait Animal {
      def breed: List[Animal]
    }

    class Cat extends Animal {
      override def breed: List[Cat] = ???             // compile not forcing me to put me correct type as CAT or DOG
    }

    class Dog extends Animal {
      override def breed: List[Cat] = ???             // compile not forcing me to put me correct type as CAT or DOG
    }
  */

  // Solution 2 - FBP
  /*
    trait Animal[A <: Animal[A]] { // recursive type: F-Bounded Polymorphism
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] {
      override def breed: List[Animal[Cat]] = ??? // List[Cat]
    }

    class Dog extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ??? // List[Dog]
    }

    trait Entity[E <: Entity[E]] // ORM
    class Person extends Comparable[Person] { // FBP
      override def compareTo(o: Person): Int = ???
    }

    class Crocodile extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ??? // List[Dog] Compiler still doesn't prevent you
    }
  */

  // Solution 3 - FBP + self-types

  /*
    trait Animal[A <: Animal[A]] {
      self: A =>
      def breed: List[Animal[A]]
    }

    class Cat extends Animal[Cat] {
      override def breed: List[Animal[Cat]] = ??? // List[Cat]
    }

    class Dog extends Animal[Dog] {
      override def breed: List[Animal[Dog]] = ??? // List[Dog]
    }

    /*
      class Crocodile extends Animal[Dog] {
        override def breed: List[Animal[Dog]] = ??? // List[Dog] Compiler finally does prevent you
      }
    */

    trait Fish extends Animal[Fish]
    class Shark extends Fish {
      override def breed: List[Animal[Fish]] = List(new Cod) // wrong!!!!!!!!!!!
    }

    class Cod extends Fish {
      override def breed: List[Animal[Fish]] = ???
    }

    */
  // Solution 4 - Type Classes
  /*
    trait Animal
    trait CanBreed[A] {
      def breed(a: A): List[A]
    }

    class Dog extends Animal
    class Cat extends Animal
    object Dog {
      implicit object DogsCanBreed extends CanBreed[Dog] {
        override def breed(a: Dog): List[Dog] = List()
      }
    }

    object Cat {
      implicit object CatsCanBreed extends CanBreed[Dog] {
        override def breed(a: Dog): List[Dog] = List()
      }
    }

    implicit class CanBreedOps[A](animal: A) {
      def breed(implicit canBreed: CanBreed[A]): List[A] =
        canBreed.breed(animal)
    }

    private val dog = new Dog
    dog.breed // List[Dog]
    /*
      new CanBreedOps[Dog](dog).breed(Dog.DogsCanBreed)
      implicit value to pass to breed: Dog.DogsCanBreed
    */

    /*
      val cat = new Cat
      cat.breed
    */
  */

  // Solution #5 - Pure Type Classes

  trait Animal[A] { //pure type classes
    def breed(a: A): List[A]
  }

  class Dog

  object Dog {
    implicit object DogAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  class Cat

  object Cat {
    implicit object CatAnimal extends Animal[Dog] {
      override def breed(a: Dog): List[Dog] = List()
    }
  }

  implicit class AnimlaOps[A](animal: A) {
    def breed(implicit animalTypeClassInstance: Animal[A]): List[A] =
      animalTypeClassInstance.breed(animal)
  }

  private val dog = new Dog
  dog.breed

  val cat = new Cat
  //cat.breed
}
