#!/bin/bash

rm -rf out

sbt assembly

~/spark-2.2.1-bin-hadoop2.7/bin/spark-submit --class SparkKnn --master local target/scala-2.11/SparkKnn-assembly-1.0.jar datasets/small.arff

#spark-submit --class SparkKnn --master local[4] SparkKnn-assembly-1.0.jar datasets/small.arff
#spark-submit --class Spark.Examples.WordCount --master local[4] target/Examples-0.0.1-SNAPSHOT.jar textfile.txt wordcount