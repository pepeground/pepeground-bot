class BaseRepo
  private

  def wrap(obj)
    if obj.respond_to?(:to_a)
      obj.map { |o| cast(o) }
    else
      cast(obj)
    end
  end
end
