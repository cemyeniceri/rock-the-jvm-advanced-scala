package lectures.part3concurrency

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration.*

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife    // calculates the meaning of life on ANOTHER thread
  } // (global) which is passed by the compiler

  println(aFuture.value)    // Option[Try[Int]]

  println("Waiting on the future")

  aFuture.onComplete {
    case Success(value) => println(s"the meaning of life is $value")
    case Failure(ex) => println(s"I have failed with $ex")
  } // SOME thread

  Thread.sleep(4000)

  // mini social-network

  case class Profile(id: String, name:String) {
    def poke(anotherProfile: Profile) =
      println(s"${this.name} poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    // database
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.3-dummy" -> "Dummy"
    )

    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      // fetching from the DB
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }
  // client: mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
  mark.onComplete {
    case Success(markProfile) => {
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(ex) => ex.printStackTrace()
      }
    }
    case Failure(ex) => ex.printStackTrace()
  }

  Thread.sleep(1000)

  // functional composition of futures
  // map, flatMap, filter
  val namesOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("z"))

  // for-comprehension
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("fb.id.3-dummy", "Forever alone")
  }

  val aFetchedProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.3-dummy")
  }

  // If you recover with something that already returns Future , use recoverWith , otherwise use recover

  val fallbackResult = SocialNetwork.fetchProfile("unknown id").fallbackTo(SocialNetwork.fetchProfile("fb.id.3-dummy"))


  // Online Banking App
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "Rock the JVM Banking"

    def fetchUser(name: String): Future[User] = Future {
      // simulate fetching from DB
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate some process
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      // fetch the user from db
      // create a function
      // WAIT for the transaction to finish
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds)
    }
  }

  println(BankingApp.purchase("Cem", "iPhone 12 Pro Max", "Rock the JVM Store", 3000))

  // promises

  val promise = Promise[Int] // "controller" over a future
  val future = promise.future

  // thread 1 - "consumer"
  future.onComplete {
    case Success(value) => println(s"[consumer] I've received $value")
  }

  // thread 2 - "producer"
  val producer = new Thread(() => {
    println("[producer] crunching numbers....")
    Thread.sleep(500)
    // "fulfilling" the promise
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)


  /*
    -EXERCISES-
    1) fulfill a future IMMEDIATELY with a value
    2) inSequence(fa, fb)
    3) first(fa, fb) => new Future with the first value of the two futures
    4) last(fa, fb) => new Future with the last value of the two futures
    5) retryUntil(action: () => Future[T], condition: T => Boolean): Future[T]
  */

  // 1- fulfill a future IMMEDIATELY with a value
  def fulfillImmediately[T](value: T): Future[T] = Future(value)
  println("Fulfill immediately : " + Await.result(fulfillImmediately(76), 2.seconds))

  // 2- inSequence(fa, fb)
  def inSequence[A, B](fa: Future[A], fb: Future[B]): Future[B] = {
    fa.flatMap(_ => fb)
  }

  // 3- first(fa, fb) => new Future with the first value of the two futures
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(_ => promise.tryComplete(_))

    /*fb.onComplete {
      case Success(value) => try {
        promise.success(value)
      } catch {
        case _ =>
      }
      case Failure(ex) => try {
        promise.failure(ex)
      } catch {
        case _ =>
      }
    }*/

    promise.future
  }

  // 4- last(fa, fb) => new Future with the last value of the two futures
  def last[A](fa: Future[A], fb: Future[A]): Future[A] = {
    // 1 promise which both futures will try to complete
    // 2 promise which the LAST future will complete

    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    val checkAndComplete = (result: Try[A]) =>
      if(!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }

  val slow = Future {
    Thread.sleep(200)
    49
  }

  first(fast, slow).foreach(x => println(s"First : $x"))
  last(fast, slow).foreach(x => println(s"Last : $x"))
  Thread.sleep(1000)

  // retryUntil(action: () => Future[T], condition: T => Boolean): Future[T]
  def retryUntil[T](action: () => Future[T], condition: T => Boolean): Future[T] =
    action().filter(condition).recoverWith {
      case _ => retryUntil(action, condition)
    }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(result => println("settled at " + result))
  Thread.sleep(20000)
}
