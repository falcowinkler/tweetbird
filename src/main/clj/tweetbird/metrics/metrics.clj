(ns tweetbird.metrics.metrics
  (:require [tweetbird.kafka.consumer :as consumer]
            [clojure.tools.logging :as log])
  (:import (org.apache.kafka.clients.consumer ConsumerConfig)
           (io.confluent.kafka.serializers KafkaAvroDeserializer)
           (java.util Properties UUID)))

(defn consume-from-beginning-kafka-properties [bootstrap-servers schema-registry-url]
  (log/info "dem values" bootstrap-servers schema-registry-url)
  (doto (new Properties)
    (.put ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG KafkaAvroDeserializer)
    (.put ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-servers)
    (.put ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG KafkaAvroDeserializer)
    (.put ConsumerConfig/AUTO_OFFSET_RESET_CONFIG "earliest")
    (.put ConsumerConfig/CLIENT_ID_CONFIG "Tweetbird_Consumer")
    (.put ConsumerConfig/GROUP_ID_CONFIG (.toString (UUID/randomUUID)))
    (.put "request.timeout.ms" (new Integer 1000))
    (.put "schema.registry.url" schema-registry-url)))

(defn consumer-from-beginning [{:keys [config]}]
  (consumer/create-kafka-consumer
    (consume-from-beginning-kafka-properties
      (:bootstrap.servers config)
      (:schema-registry-url config))))

(defn handle-registration-record [backend reg]
  (swap! (:registered-users backend) conj (.get (.value reg) "id")))

(defn stream-statistics [backend config]
  (consumer/for-each-event-in-topic
    (consumer-from-beginning config)
    "user_registrations"
    (partial handle-registration-record backend)))

