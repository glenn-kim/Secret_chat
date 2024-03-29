import akka.actor.{Props, ActorSystem}
import akka.io.Tcp.{Close, Write, Received}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.{Config, ConfigFactory}
import org.bson.types.ObjectId
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import the.accidental.billionaire.secretchat.actor.{TcpHandler, UserService}
import the.accidental.billionaire.secretchat.security.UserData
import the.accidental.billionaire.secretchat.protocol.{AuthFailed, SessionLoginOkay, SessionLoginRequest}



/**
 * Created by infinitu on 2015. 4. 12..
 */
class UserServiceTest(_system:ActorSystem) extends TestKit(_system) with WordSpecLike with Matchers with MockFactory with BeforeAndAfterAll with ImplicitSender with MongoEmbedDatabase{
  def this()= this(ActorSystem("PingTest"))

  var mongoProps:MongodProps = null

  override protected def beforeAll(): Unit ={
    val config:Config = ConfigFactory.load().getConfig("mongo")
    val port = config.getInt("port")
    mongoProps = mongoStart(port = port)
  }

  override protected def afterAll(): Unit ={
    mongoStop(mongoProps)
    TestKit.shutdownActorSystem(system)
  }

  "UserService" should {
    import UserService._
    val config:Config = ConfigFactory.load().getConfig("mongo")
    val client = MongoClient("localhost",config.getInt("port"))
    val DB = client getDB config.getString("dbname")
    val actorRef = TestActorRef(Props(new UserService))

    "login success with correct parameter condition" in {
      if(DB.collectionExists(collectionName))
        (DB getCollection collectionName).drop()
      val coll = DB getCollection collectionName
      val devid = "test_deviceID"
      val accToken = "test_accToken"
      val encToken = "test_encToken"
      val obj = MongoDBObject(col_deviceId->devid, col_accessToken->accToken, col_encryptToken->encToken)
      coll insert obj
      val uid = (coll findOne obj).get("_id").asInstanceOf[ObjectId].toHexString

      actorRef ! LoginReqest(devid,accToken)
      expectMsg(LoginOkay(UserData(devid,accToken,uid,encToken)))
    }

    "login failed with incorrect parameter condition" in {
      val coll = DB getCollection collectionName
      actorRef ! LoginReqest("someid","someToken")
      expectMsg(LoginFailed)
    }

    implicit val a:Option[UserData]=None

    val serviceActorRef = TestActorRef(Props[UserService],UserService.actorPath)

    "works well with TCP Handler in correct Condition" in {
      if(DB.collectionExists(collectionName))
        (DB getCollection collectionName).drop()
      val coll = DB getCollection collectionName
      val devid = "test_deviceID"
      val accToken = "test_accToken"
      val encToken = "test_encToken"
      val obj = MongoDBObject(col_deviceId->devid, col_accessToken->accToken, col_encryptToken->encToken)
      coll insert obj
      val uid = (coll findOne obj).get("_id").asInstanceOf[ObjectId].toHexString

      val actorRef = TestActorRef(Props(new TcpHandler(self)))
      val actor:TcpHandler = actorRef.underlyingActor


       actorRef ! Received(SessionLoginRequest(" ",devid,accToken," "," "," ").serialize)
      expectMsg(Write(SessionLoginOkay().serialize))
      actor.userData should be (Some(UserData(devid,accToken,uid,encToken)))

      actorRef.stop()
    }

    "works well with TCP Handler in incorrect Condition" in {
      val actor = TestActorRef(Props(new TcpHandler(self)))
      actor ! Received(SessionLoginRequest(" ","will","works"," "," "," ").serialize)
      expectMsg(Write(AuthFailed("LoginFailed").serialize))
      expectMsg(Close)
      actor.stop()
    }

  }
}
