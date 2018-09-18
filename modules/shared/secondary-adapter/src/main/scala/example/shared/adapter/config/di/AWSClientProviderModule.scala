package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import jp.eigosapuri.es.shared.adapter.secondary.aws.s3.{ S3ClientProvider, S3ClientProviderImpl }
import jp.eigosapuri.es.shared.adapter.secondary.aws.ses.{ SESClientProvider, SESClientProviderImpl }

class AWSClientProviderModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[S3ClientProvider]).to(classOf[S3ClientProviderImpl])
    bind(classOf[SESClientProvider]).to(classOf[SESClientProviderImpl])
  }

}
