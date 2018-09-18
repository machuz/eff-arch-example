package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import play.api.libs.ws.WSClient

import jp.eigosapuri.es.shared.adapter.secondary.aws.s3.{ S3ClientProvider, S3ClientProviderImpl }
import jp.eigosapuri.es.shared.adapter.secondary.http.WSClientProvider

class HttpClientModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[WSClientProvider])
  }

}
