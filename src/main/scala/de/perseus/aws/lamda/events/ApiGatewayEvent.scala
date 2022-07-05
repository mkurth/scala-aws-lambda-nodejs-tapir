package de.perseus.aws.lamda.events

final case class ApiGatewayEvent(
    path: String,
    httpMethod: String,
    headers: Map[String, String],
    pathParameters: Map[String, String],
    body: String | Null,
    isBase64Encoded: Boolean
)

final case class ApiGatewayResponse(
    body: String                                 = "",
    statusCode: Int                              = 200,
    headers: Map[String, String]                 = Map.empty,
    isBase64Encoded: Boolean                     = false,
    multiValueHeaders: Map[String, List[String]] = Map.empty
)
