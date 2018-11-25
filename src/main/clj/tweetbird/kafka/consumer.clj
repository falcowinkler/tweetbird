(ns tweetbird.kafka.consumer
  (:require [clojure.core.async :as async])
  (:import (org.apache.kafka.clients.consumer KafkaConsumer)))

(defn create-kafka-consumer [properties]
  (new KafkaConsumer properties))

(defn for-each-event-in-topic [consumer topic handler-fn]
  (async/thread
    (.subscribe consumer [topic])
    (while true
      (let [records (.poll consumer 10)]
        (doseq [record records] (handler-fn record))
        (.commitAsync consumer)))))