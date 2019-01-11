module ScalikeJDBC
  module DB
    include_package 'scalikejdbc.DB'
  end

  module Config
    include_package 'scalikejdbc.config'
  end

  class << self
    def setup!
      ScalikeJDBC::Config::DBs.setupAll
    end
  end
end