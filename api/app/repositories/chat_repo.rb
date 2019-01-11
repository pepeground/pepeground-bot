java_import 'com.pepeground.core.entities.ChatEntity'
java_import 'com.pepeground.core.repositories.ChatRepository'

class ChatRepo < BaseRepo
  def get_list(limit = 20, offset = 0)
    wrap(
      Java::ComPepegroundCoreRepositories::ChatRepository.getList(
        limit.to_java(:int),
        offset.to_java(:int),
        session
      ).from_scala
    )
  end

  private

  def cast(obj)
    Chat.new(
      id: obj.id.from_scala,
      name: obj.name.maybe,
      telegram_id: obj.telegram_id.from_scala,
      random_chance: obj.random_chance.from_scala,
      chat_type: obj.chat_type.from_scala,
      updated_at: DateTime.parse(obj.updated_at.to_string),
      created_at: DateTime.parse(obj.created_at.to_string)
    )
  end

  def session
    @sesson ||= Java::ComPepegroundCoreEntities::ChatEntity.autoSession
  end
end
