(ns tweetbird.kafka.producer
  (:require [clojure.tools.logging :as log])
  (:import (java.util Properties)
           (org.apache.kafka.clients.producer ProducerConfig KafkaProducer ProducerRecord)
           (io.confluent.kafka.serializers KafkaAvroSerializer)))

(defn create-producer-properties [bootstrap-servers schema-registry-url]
  (doto (new Properties)
    (.put ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG KafkaAvroSerializer)
    (.put ProducerConfig/BOOTSTRAP_SERVERS_CONFIG bootstrap-servers)
    (.put ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG KafkaAvroSerializer)
    (.put ProducerConfig/CLIENT_ID_CONFIG "Tweetbird")
    (.put "request.timeout.ms" (new Integer 1000))
    (.put "max.block.ms" (new Integer 1000))
    (.put "schema.registry.url" schema-registry-url)))

(defn create-producer [properties]
  (new KafkaProducer properties))

(defn create-producer-record [key topic avro-record]
  (new ProducerRecord topic key avro-record))

(defn send-message [producer producer-record]
  (log/info "sending message ")
  (log/info producer)
  (log/info producer-record)
  (.send producer producer-record)
  (.flush producer))