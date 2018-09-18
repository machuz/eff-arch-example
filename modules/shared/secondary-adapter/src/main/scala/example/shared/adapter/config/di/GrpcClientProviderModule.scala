package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import jp.eigosapuri.es.notificationApi.adapter.primary.grpc.service.EsNotificationApiServiceGrpc.EsNotificationApiServiceStub
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.esAccountApi.{
  EsAccountApiGrpcClientProvider,
  EsAccountApiGrpcClientProviderImpl
}
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.esCoachApi.{
  EsCoachApiGrpcClientProvider,
  EsCoachApiGrpcClientProviderImpl
}
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.esCommunicationApi.{
  EsCommunicationApiGrpcClientProvider,
  EsCommunicationApiGrpcClientProviderImpl
}
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.esDeliveryApi.{
  EsDeliveryApiGrpcClientProvider,
  EsDeliveryApiGrpcClientProviderImpl
}
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.esNotificationApi.EsNotificationApiGrpcClientProvider
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.esPaymentApi.{
  EsPaymentApiGrpcClientProvider,
  EsPaymentApiGrpcClientProviderImpl
}
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.esStoreApi.{
  EsStoreApiGrpcClientProvider,
  EsStoreApiGrpcClientProviderImpl
}
import jp.eigosapuri.es.shared.adapter.primary.grpc.es.studyTargetApi.{
  EsStudyTargetApiGrpcClientProvider,
  EsStudyTargetApiGrpcClientProviderImpl
}

class GrpcClientProviderModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[EsPaymentApiGrpcClientProvider]).to(classOf[EsPaymentApiGrpcClientProviderImpl])
    bind(classOf[EsStudyTargetApiGrpcClientProvider]).to(classOf[EsStudyTargetApiGrpcClientProviderImpl])
    bind(classOf[EsAccountApiGrpcClientProvider]).to(classOf[EsAccountApiGrpcClientProviderImpl])
    bind(classOf[EsStoreApiGrpcClientProvider]).to(classOf[EsStoreApiGrpcClientProviderImpl])
    bind(classOf[EsDeliveryApiGrpcClientProvider]).to(classOf[EsDeliveryApiGrpcClientProviderImpl])
    bind(classOf[EsNotificationApiServiceStub]).toProvider(classOf[EsNotificationApiGrpcClientProvider])
    bind(classOf[EsCommunicationApiGrpcClientProvider]).to(classOf[EsCommunicationApiGrpcClientProviderImpl])
    bind(classOf[EsCoachApiGrpcClientProvider]).to(classOf[EsCoachApiGrpcClientProviderImpl])
  }
}
