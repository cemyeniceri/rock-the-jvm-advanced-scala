package lectures.part5ts

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HigherKindedTypes extends App {

  trait AHigherKindedType[F[_]]

  trait MyList[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  trait MyOption[T] {
    def flatMap[B](f: T => B): MyOption[B]
  }

  trait MyFuture[T] {
    def flatMap[B](f: T => B): MyFuture[B]
  }

  def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
    for {
      a <- listA
      b <- listB
    } yield (a, b)

  def multiply[A, B](optA: Option[A], optB: Option[B]): Option[(A, B)] =
    for {
      a <- optA
      b <- optB
    } yield (a, b)

  def multiply[A, B](fa: Future[A], fb: Future[B]): Future[(A, B)] =
    for {
      a <- fa
      b <- fb
    } yield (a, b)

  // use HKT
  private trait Monad[F[_], A] { // higher-kinded type
    def flatMap[B](f: A => F[B]): F[B]

    def map[B](f: A => B): F[B]
  }

  private implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)

    override def map[B](f: A => B): List[B] = list.map(f)
  }

  private implicit class MonadOption[A](opt: Option[A]) extends Monad[Option, A] {
    override def flatMap[B](f: A => Option[B]): Option[B] = opt.flatMap(f)

    override def map[B](f: A => B): Option[B] = opt.map(f)
  }

  private implicit class MonadFuture[A](fut: Future[A]) extends Monad[Future, A] {
    override def flatMap[B](f: A => Future[B]): Future[B] = fut.flatMap(f)

    override def map[B](f: A => B): Future[B] = fut.map(f)
  }

  private def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A, B)] =
    for {
      a <- ma
      b <- mb
    } yield (a, b)

  private val monadList = new MonadList(List(1, 2, 3))
  monadList.flatMap(x => List(x, x + 1)) // List[Int]
  // Monad[List, Int] => List[Int]
  monadList.map(_ * 2) // List[Int]
  // Monad[List, Int] => List[Int]

  println(multiply(List(1, 2), List("a", "b")))
  println(multiply(Some(1), Some(3)))
  // println(multiply(new MonadOption[Int](Some(1)), new MonadOption[Int](Some(3))))
  println(multiply(Future(1), Future(3)))
}
