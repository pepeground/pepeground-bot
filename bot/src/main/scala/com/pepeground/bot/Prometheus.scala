package com.pepeground.bot

import com.sun.net.httpserver.HttpExchange
import io.micrometer.prometheus.{PrometheusConfig, PrometheusMeterRegistry}
import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.InetSocketAddress

object Prometheus {
  val prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

  def start(): Unit = server.start()

  private lazy val server = buildServer()

  private def buildServer() = {
    try {
      val server = HttpServer.create(new InetSocketAddress(8080), 0)
      server.createContext("/metrics", (httpExchange: HttpExchange) => {
        def serve(httpExchange: HttpExchange) = {
          val response = prometheusRegistry.scrape()
          httpExchange.sendResponseHeaders(200, response.getBytes.length)
          try {
            val os = httpExchange.getResponseBody
            try
              os.write(response.getBytes)
            finally if (os != null) os.close()
          }
        }

        serve(httpExchange)
      })
      new Thread { server.start() }
    } catch {
      case e: IOException =>
        throw new RuntimeException(e)
    }
  }
}
