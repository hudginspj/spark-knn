
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.mllib


object SparkKnn {

  class ArffInstance extends Serializable {
    var cls: String = ""
    var features: Array[Float] = Array()

    def this(strings: Array[String]) = {
      this()
      this.cls = strings(strings.length - 1)
      this.features = strings.slice(0, strings.length - 1).map(s => s.toFloat)
    }

    def distSquare(other: ArffInstance): Float = {
      return (this.features, other.features).zipped.map(_ - _).map(d => d * d).sum
    }

  }


  def main(args: Array[String]) {
    var inputFile = args(0)

    def reduceFunc(v1: (Float, String), v2: (Float, String)): (Float, String) = {
      if (v1._1 < v2._1) {
        return v1
      } else {
        return v2
      }
    }

    val conf = new SparkConf().setAppName("SparkKnn")
    val sc = new SparkContext(conf)
    var small = sc.textFile(inputFile)
    small = small.filter(line => !line.startsWith("@"))
    var lines = small.map(line => line.split(",").map(word => word.trim()))
    //lines = lines.filter(line => line.length > 1)
    var instances = lines.map(line => new ArffInstance(line))

    var startTime = System.currentTimeMillis

    var indexedInstances = instances.zipWithIndex().map { case (k, v) => (v, k) }
    //println(indexedInstances.count())
    var cart = indexedInstances.cartesian(instances)
    var distClassPairs = cart.map { case ((k, v1), v2) => (k, (v1.distSquare(v2), v2.cls)) }
    var predictions = distClassPairs.reduceByKey(reduceFunc)



    var correctPredictions = predictions
      .join(indexedInstances)
      .filter(p => p._2._1._2.equals(p._2._2.cls))
      .count()
    var totalPredictions = predictions.count()

    var runTime = System.currentTimeMillis - startTime

    println("="*25)
    println(correctPredictions + " out of " +
      totalPredictions + " predictions correct, accuracy " +
      100.0 * correctPredictions / totalPredictions + "%, runtime "
      + runTime + "ms")
    println("="*25)

    //sc.stop()
  }
}

