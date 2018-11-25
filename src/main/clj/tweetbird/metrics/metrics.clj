(ns tweetbird.metrics.metrics
  (:require [clojure.tools.logging :as log]
            [tweetbird.kafka.consumer :as consumer])
  (:import (org.apache.kafka.clients.consumer ConsumerConfig)
           (io.confluent.kafka.serializers KafkaAvroDeserializer)
           (java.util Properties UUID)))

(defn consume-from-beginning-kafka-properties [bootstrap-servers schema-registry-url]
  (doto (new Properties)
    (.put ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG KafkaAvroDeserializer)
    (.put ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-servers)
    (.put ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG KafkaAvroDeserializer)
    (.put ConsumerConfig/AUTO_OFFSET_RESET_CONFIG "earliest")
    (.put ConsumerConfig/CLIENT_ID_CONFIG "Tweetbird_Consumer")
    (.put ConsumerConfig/GROUP_ID_CONFIG (.toString (UUID/randomUUID)))
    (.put "request.timeout.ms" (new Integer 1000))
    (.put "schema.registry.url" schema-registry-url)))

(def consumer-from-beginning
  (consumer/create-kafka-consumer
    (consume-from-beginning-kafka-properties
      "localhost:9092"
      "http://localhost:8081")))

(defn handle-registration-record [backend reg]
  (swap! (:registered-users backend) conj (.get (.value reg) "id")))

(defn stream-statistics [backend]
  (consumer/for-each-event-in-topic consumer-from-beginning "user_registrations"
                                    (partial handle-registration-record backend)))

