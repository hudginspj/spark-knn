class ArffInstance {
  var cls : Int = 0
  var features : Array[Float] = Array()


  def this(strings : Array[String]) = {
    this()
    this.cls = strings(strings.length - 1).toInt
    this.features = strings.slice(0, strings.length-1).map(s => s.toFloat)
  }

}
