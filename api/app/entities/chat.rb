class Chat < Dry::Struct
  attribute :id, Types::Strict::Int
  attribute :name, Types::Strict::String.optional
  attribute :telegram_id, Types::Strict::Int
  attribute :chat_type, Types::Strict::Int
  attribute :random_chance, Types::Strict::Int
  attribute :created_at, Types::Strict::DateTime
  attribute :updated_at, Types::Strict::DateTime
end

