package lectures.part5ts

object Variance extends App {

  trait Animal

  class Dog extends Animal

  class Cat extends Animal

  class Crocodile extends Animal


  // What is variance ?
  // "inheritance"  -- type substitution of generics

  class Cage[T]
  // Question ???????????  Cage <Cat> extends Cage <Animal>


  // yes - covariance
  class CCage[+T]

  val ccage: CCage[Animal] = new CCage[Cat]

  //no - invariance
  class ICage[T]
  // val icage: ICage[Animal] = new ICage[Cat] // same stupid error
  // val x: Int = "hello"                      // same stupid error

  // hell no - opposite = contravariance
  class XCage[-T]

  val xcage: XCage[Cat] = new XCage[Animal]


  class InvariantCage[T](val animal: T) // INVARIANT -- OK

  class CovariantCage[+T](val animal: T) // COVARIANT POSITION -- OK

  // class ContraVariantCage[-T](val animal: T) // Not Compilable


  /* if the compiler allows, you would do this wrong thing!!
    val catCage: XCage[Cage] = new XCage[Animal](new Crocodile)
  */

  // class CovariantVariableCage[+T](var animal: T) // Not Compilable - types of "vars(reassign)" are in CONTRAVARIANT POSITION

  /* if the compiler allows, you would do this wrong thing!!
    val animalCage: CCage[Animal] = new CCage[Cat](new Cat)
    animalCage.animal = new Crocodile
  */

  // class ContravariantVariableCage[-T](var animal: T) // also in COVARIANT position
  /* if the compiler allows, you would do this wrong thing!!
    val catCage: XCage[Cage] = new XCage[Animal](new Crocodile)
  */

  class InvariantVariableCage[T](var animal: T) // OK


  //  trait AnotherCovariantCage[+T] {
  //    def addAnimal(animal: T) // CONTRAVARIANT POSITION
  //  }

  /* if the compiler allows, you would do this wrong thing!!
    val ccage: CCage[Animal] = new CCage[Dog]
    ccage.addAnimal(new Cat)
  */

  class AnotherContraVariantCage[-T] {
    def addAnimal(animal: T) = true
  }

  val acc: AnotherContraVariantCage[Cat] = new AnotherContraVariantCage[Animal]
  acc.addAnimal(new Cat)

  class Kitty extends Cat

  acc.addAnimal(new Kitty)


  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)


  // METHOD ARGUMENTS ARE IN CONTRAVARIANT POSITION

  //return types
  class PetShop[-T] {
    // def get(isItaPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /* if the compiler allows, you would do this wrong thing!!
      val catShop = new PetShop[Animal] {
        def get(isItaPuppy: Boolean): Animal = new Cat
      }
      val dogShop: PetShop[Dog] = catShop
      dogShop.get(true)     // EVIL CAT!!!!!!!!!!!
    */

    def get[S <: T](isItaPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]

  // val evilCat = shop.get(true, new Cat)
  class TerraNova extends Dog

  val bigFurry = shop.get(true, new TerraNova)

  /*

    Big Rule
      - method arguments are in CONTRAVARIANT position
      - return types are in COVARIANT position
  */
}
