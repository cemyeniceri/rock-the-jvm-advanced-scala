package lectures.part5ts

object TypeMembers extends App {

  class Animal

  class Dog extends Animal

  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat
  }

  val ac = new AnimalCollection
  val dog: ac.AnimalType = ???
  // val cat: ac.BoundedAnimal = new Cat
  val pup: ac.SuperBoundedAnimal = new Dog
  val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // alternative to Generics
  trait Mylist {
    type T

    def add(element: T): Mylist
  }

  class NonEmptyList(value: Int) extends Mylist {
    override type T = Int

    override def add(element: Int): Mylist = ???
  }

  // .type
  type CatsType = cat.type
  val newCat: CatsType = cat
  // new CatsType ----> you can't instantiate a new but you can associate an existing one!
}
