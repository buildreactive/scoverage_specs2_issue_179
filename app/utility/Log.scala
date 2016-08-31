package utility

import play.api.Logger

/**
  *
  * General purpose logging function. This really should not be called directly, unless you have a very good reason for not
  * using one of the `Logging` traits defined here (eg., `ApplicationLogging`). This class provides a general purpose logger
  * using the `ApplicationLogging` trait.
  *
  * @author zbeckman on 1/17/16.
  */
object Log extends ApplicationLogging

/**
  * Logger traits. Each trait implements specific logging support for various components. Override behaviors (such as by
  * adding a custom implementation of an Action) to inject logging into the framework. Use these loggers by mixing them in
  * with your class, for example, ''object MyGizmo extends Application with RequestLogging''.
  *
  * Default logging levels for each logger is defined in 'conf/logback.xml'.
  */
trait ApplicationLogging {
	val logger: Logger = Logger("pms")
}

trait TestLogging {
	val logger: Logger = Logger("pms.test")
}

trait RequestLogging {
	val logger: Logger = Logger("pms.request")
}

trait CouchbaseLogging {
	val logger: Logger = Logger("pms.couchbase")
}

trait HazelcastLogging {
	val logger: Logger = Logger("pms.hazelcast")
}

trait SecurityLogging {
	val logger: Logger = Logger("pms.security")
}

trait SocketLogging {
	val logger: Logger = Logger("pms.socket")
}
