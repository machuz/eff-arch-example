package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import jp.eigosapuri.es.shared.adapter.primary.play.output.json.model.error.unknown.UnknownErrorPresenterImpl
import jp.eigosapuri.es.shared.adapter.primary.play.output.presenter.error.unknown.UnknownErrorPresenter

class PresenterModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[UnknownErrorPresenter]).to(classOf[UnknownErrorPresenterImpl])

  }

}
