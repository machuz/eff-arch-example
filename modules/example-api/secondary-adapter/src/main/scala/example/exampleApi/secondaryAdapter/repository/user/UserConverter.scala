package example.exampleApi.secondaryAdapter.repository.user
import example.exampleApi.domain.model.user.{ User, UserId }
import example.exampleApi.secondaryAdapter.db.dataModel.UserDataModel

trait UserConverter {

  def convertToDomainModel(dataModel: UserDataModel): User = {
    User(
      id = UserId(dataModel.id),
      name = dataModel.name,
      createdAt = dataModel.createdAt,
      updatedAt = dataModel.updatedAt
    )
  }

}
