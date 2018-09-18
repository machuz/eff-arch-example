package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import jp.eigosapuri.es.shared.adapter.config.SharedAdapterConf
import jp.eigosapuri.es.shared.adapter.secondary.client.{
  FileDownloadClient,
  LocalFileDownloadClientImpl,
  S3FileDownloadClientImpl
}

class FileDownloadClientModule extends AbstractModule {

  private lazy val USE_S3 = SharedAdapterConf.s3.use

  def configure(): Unit = {
    if (USE_S3) {
      bind(classOf[FileDownloadClient]).to(classOf[S3FileDownloadClientImpl])
    } else {
      bind(classOf[FileDownloadClient]).to(classOf[LocalFileDownloadClientImpl])
    }
  }

}
