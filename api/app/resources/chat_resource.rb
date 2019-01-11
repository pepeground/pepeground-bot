class ChatResource < Grape::API
  format :json

  helpers do
    def repository
      ChatRepo.new
    end
  end

  resource 'chats' do
    get do
      repository.get_list.map(&:to_h)
    end
  end
end
