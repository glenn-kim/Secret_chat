package the.accidental.billionaire.secretchat.utils

import akka.actor.{ActorRef, Actor}

/**
 * Trait implementing Receive Pipeline Pattern. Mixin this trait
 * for configuring a chain of interceptors to be applied around
 * Actor's current behavior.
 */
trait ReceivePipeline extends Actor {

  val connection:ActorRef
  private var pipeline: Vector[Receive ⇒ Receive] = Vector.empty
  private var aroundCache: Option[(Receive, Receive)] = None

  /**
   * Adds an inner interceptor, it will be applied lastly, near to Actor's original behavior
   * @param around A Receive decorator. Gets by parameter the next Receive in the chain
   *               and has to return a new Receive with the decoration applied
   */
  def pipelineInner(around: Receive ⇒ Receive): Unit = {
    pipeline :+= around
    aroundCache = None
  }
  /**
   * Adds an outer interceptor, it will be applied firstly, far from Actor's original behavior
   * @param around A Receive decorator. Gets by parameter the next Receive in the chain
   *               and has to return a new Receive with the decoration applied
   */
  def pipelineOuter(around: Receive ⇒ Receive): Unit = {
    pipeline +:= around
    aroundCache = None
  }

  /**
   * INTERNAL API.
   */
  override def aroundReceive(receive: Receive, msg: Any): Unit = {
    val around = aroundCache match {
      case Some((`receive`, cached)) ⇒ cached
      case _ ⇒
        val zipped = pipeline.foldRight[Receive](receive)((outer, inner) ⇒ outer(inner).orElse[Any, Unit](inner))
        aroundCache = Some((receive, zipped))
        zipped
    }
    super.aroundReceive(around, msg)
  }
}