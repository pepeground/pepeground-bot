require 'java'
require 'pry'
require 'dry-struct'
require 'grape'

require_relative 'core.jar'
require 'jruby/scala_support'
require_relative 'lib/scalike_jdbc'

module Types
  include Dry::Types.module
end

class Object
  def maybe
    case self
    when Java::jruby.collection.Utils.None
      nil
    when Java::scala.Some
      self.get.from_scala
    end
  end
end

ScalikeJDBC.setup!

Dir['app/**/*.rb'].each { |f| require_relative f }
