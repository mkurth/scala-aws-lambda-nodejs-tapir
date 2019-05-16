package com.mcurse.server

import com.mcurse.domain.DomainLogic
import com.mcurse.endpoints.Endpoints.{byeEndpoint, helloEndpoint}
import sttp.tapir.serverless.aws.lambda._
import sttp.tapir.serverless.aws.lambda.js._

import scala.annotation.nowarn
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.scalajs.js.annotation.JSExportTopLevel

object LambdaMain {

  @nowarn
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val options: AwsServerOptions[Future] = AwsServerOptions(encodeResponseBody = false, Nil)

  val route: Route[Future] = AwsFutureServerInterpreter(options).toRoute(
    List(
      helloEndpoint.serverLogic(DomainLogic.helloLogic[Future]),
      byeEndpoint.serverLogic(DomainLogic.byeLogic[Future])
    )
  )

  @JSExportTopLevel(name = "handler")
  def handler(event: AwsJsRequest, context: Any): scala.scalajs.js.Promise[AwsJsResponse] =
    AwsJsRouteHandler.futureHandler(event, route)
}
