package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  override def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def +(elem: A): MySet[A]

  def ++(anotherSet: MySet[A]): MySet[A] // union

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(f: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  def -(elem: A): MySet[A]

  def --(anotherSet: MySet[A]): MySet[A] // difference

  def &(anotherSet: MySet[A]): MySet[A] // intersection

  // implement a unary_! => except the set set.unary_!([1 2 3]) ...-> except 1 2 3
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {

  override def contains(elem: A): Boolean = false

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]()

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]()

  override def filter(f: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def -(elem: A): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}

// all elements of type A which satisfy a property
// { x in A | property (x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {
  override def contains(elem: A): Boolean = property(elem)

  // { x in A | property(x) } + element = { x in A | property(x) || x == element }
  override def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == elem)

  override def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  // naturals = allInclusiveSet[Int] = all the natural numbers
  // naturals.map(x => x % 3) => ??
  // recurrent [0 1 2]
  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Illegal deep rabbit hole!")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {

  override def contains(elem: A): Boolean =
    (head == elem) || tail.contains(elem)

  override def +(elem: A): MySet[A] =
    if (this.contains(elem)) this
    else new NonEmptySet[A](elem, this)


  /*
  * [1 2 3] ++ [4 5] =
  * [2 3] ++ [4 5] + 1 =
  * [3] ++ [4 5] + 1 + 2 =
  * [] ++ [4 5] + 1 + 2 + 3 =
  * [4 5] + 1 + 2 + 3 = [4 5 1 2 3]
  * */

  override def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)

  override def filter(f: A => Boolean): MySet[A] =
    val filtered = tail filter f
    if (f(head)) filtered + head
    else filtered

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  override def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

  // !anotherSet.contains(x) == !anotherSet(x)
  //
  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))

  // anotherSet(x) == anotherSet.contains(x)
  // filter(x => anotherSet(x)) == filter(anotherSet)
  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection and filter are same!

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => this.contains(x))
}

object MySet {
  /*
  * val s = MySet(1,2,3) = buildSet(seq(1,2,3), [])
  * = buildSet(seq(2,3), [] + 1)
  * = buildSet(seq(3), [1] + 2)
  * = buildSet(seq(), [1 2] + 3)
  * = [1 2 3]
  * */

  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }

    buildSet(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val mySet = MySet(1, 2, 3, 4)

  mySet + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, x * 10)) filter (_ % 2 == 0) foreach println

  val notMySet = !mySet // s.unary_! = all the naturals not equal to 1,2,3,4
  println(notMySet(2))
  println(notMySet(5))

  val negativeNotMySet = notMySet.filter(x => x % 2 == 0)
  println(negativeNotMySet(5))

}