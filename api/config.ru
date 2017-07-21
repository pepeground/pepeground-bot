require_relative 'app'

run Rack::Cascade.new [
  ChatResource
]
