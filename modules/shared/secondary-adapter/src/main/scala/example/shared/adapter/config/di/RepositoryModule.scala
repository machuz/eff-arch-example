package example.shared.adapter.config.di

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.google.inject.name.Names
import com.google.inject.{ AbstractModule, TypeLiteral }

import slick.dbio.DBIO

import jp.eigosapuri.es.shared.adapter.secondary.aws.dynamodb.{
  DynamoDBClientProvider,
  DynamoDBClientProviderImpl,
  DynamoDBIOTaskRunnerImpl
}
import jp.eigosapuri.es.shared.adapter.secondary.repository.DBIOTaskRunnerImpl
import jp.eigosapuri.es.shared.adapter.secondary.repository.notification.push.token.PushTokenRepositoryImpl
import jp.eigosapuri.es.shared.interface.repository.notification.push.PushTokenRepository
import jp.eigosapuri.es.shared.lib.dddSupport.adapter.secondary.repository.{ DBIOTaskRunner, ReaderTaskRunner }
import jp.eigosapuri.es.shared.lib.dddSupport.domain.{
  EsRandom,
  EsRandomImpl,
  EsSecureRandomImpl,
  UUIDIdGenerator,
  UUIDIdGeneratorImpl
}

class RepositoryModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[UUIDIdGenerator]).to(classOf[UUIDIdGeneratorImpl])

    bind(classOf[EsRandom]).annotatedWith(Names.named("general")).to(classOf[EsRandomImpl])
    bind(classOf[EsRandom]).annotatedWith(Names.named("secure")).to(classOf[EsSecureRandomImpl])

    bind(new TypeLiteral[PushTokenRepository]() {}).to(classOf[PushTokenRepositoryImpl])
    bind(classOf[DynamoDBClientProvider]).to(classOf[DynamoDBClientProviderImpl])
    bind(new TypeLiteral[DBIOTaskRunner[DBIO]]()                   {}).to(classOf[DBIOTaskRunnerImpl])
    bind(new TypeLiteral[ReaderTaskRunner[AmazonDynamoDBClient]]() {}).to(classOf[DynamoDBIOTaskRunnerImpl])
  }

}
