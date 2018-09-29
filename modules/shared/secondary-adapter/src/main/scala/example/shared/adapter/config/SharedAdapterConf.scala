package example.shared.adapter.config

import example.shared.adapter.config.support.ApplicationConfBase

object SharedAdapterConf extends ApplicationConfBase {

  object Project {
    lazy val originUrl = getString("project.origin_url")
  }

  object ApiProject {
    lazy val AllowOrigin = getStringOpt("project.api.cors.access_control_allow_origin")
  }

  object jwt {
    lazy val iss         = getString("project.api.jwt.iss")
    lazy val expTerm     = getLong("project.api.jwt.exp_term")
    lazy val esSecretKey = getString("project.api.jwt.secret_key.egs")
  }

  object redis {

    lazy val use = getBoolean("redis.use")
    object master {
      lazy val host     = getString("redis.master.host")
      lazy val port     = getInt("redis.master.port")
      lazy val password = getStringOpt("redis.master.password")
      lazy val dbNum    = getIntOpt("redis.master.dbNum")
    }
  }

  object kinesis {
    lazy val use       = getBoolean("kinesis.use")
    lazy val accessKey = getString("kinesis.aws_access_key_id")
    lazy val secretKey = getString("kinesis.aws_secret_access_key")
  }

  object s3 {
    lazy val use       = getBoolean("s3.use")
    lazy val accessKey = getString("s3.aws_access_key_id")
    lazy val secretKey = getString("s3.aws_secret_access_key")

    object buckets {}
  }

  object ses {
    lazy val use        = getBoolean("ses.use")
    lazy val accessKey  = getString("ses.aws_access_key_id")
    lazy val secretKey  = getString("ses.aws_secret_access_key")
    lazy val useSandbox = getBoolean("ses.use_sandbox")

    object buckets {}
  }

  object dynamodb {
    lazy val region    = getString("dynamodb.region")
    lazy val accessKey = getString("dynamodb.aws_access_key_id")
    lazy val secretKey = getString("dynamodb.aws_secret_access_key")
    lazy val endpoint  = getStringOpt("dynamodb.endpoint")

    object default {
      object throughput {
        lazy val readCapacityUnit  = getLongOpt("dynamodb.default.throughput.read_capacity_unit").getOrElse(1L)
        lazy val writeCapacityUnit = getLongOpt("dynamodb.default.throughput.write_capacity_unit").getOrElse(1L)
      }
    }

    object userMaster {
      lazy val tableName = getString("dynamodb.user_master.table_name")
      lazy val readCapacityUnit =
        getLongOpt("dynamodb.user_master.throughput.read_capacity_unit").getOrElse(default.throughput.readCapacityUnit)
      lazy val writeCapacityUnit = getLongOpt("dynamodb.user_master.throughput.write_capacity_unit").getOrElse(
        default.throughput.writeCapacityUnit
      )
    }
  }

  object grpc {
    object es {
      object accountApi {
        lazy val host = getString("grpc.es.accountApi.host")
        lazy val port = getInt("grpc.es.accountApi.port")
      }
      object paymentApi {
        lazy val host = getString("grpc.es.paymentApi.host")
        lazy val port = getInt("grpc.es.paymentApi.port")
      }
      object studyTargetApi {
        lazy val host = getString("grpc.es.studyTargetApi.host")
        lazy val port = getInt("grpc.es.studyTargetApi.port")
      }
      object storeApi {
        lazy val host = getString("grpc.es.storeApi.host")
        lazy val port = getInt("grpc.es.storeApi.port")
      }
      object deliveryApi {
        lazy val host = getString("grpc.es.deliveryApi.host")
        lazy val port = getInt("grpc.es.deliveryApi.port")
      }
      object coachApi {
        lazy val host = getString("grpc.es.coachApi.host")
        lazy val port = getInt("grpc.es.coachApi.port")
      }
      object notificationApi {
        lazy val host = getString("grpc.es.notificationApi.host")
        lazy val port = getInt("grpc.es.notificationApi.port")
      }
      object communicationApi {
        lazy val host = getString("grpc.es.communicationApi.host")
        lazy val port = getInt("grpc.es.communicationApi.port")
      }
    }
  }

}
