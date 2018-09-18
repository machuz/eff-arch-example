package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import jp.eigosapuri.es.shared.adapter.secondary.aws.ses.EmailClientImpl
import jp.eigosapuri.es.shared.adapter.secondary.client.EmailClient

class EmailClientModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[EmailClient]).to(classOf[EmailClientImpl])
  }

}
