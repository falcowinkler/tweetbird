(ns tweetbird.kafka.producer-tests
  (:require [clojure.test :refer :all]
            [tweetbird.kafka.producer :as producer]
            [clojure.tools.logging :as log])
  (:import (org.apache.kafka.clients.producer ProducerConfig KafkaProducer ProducerRecord)
           (io.confluent.kafka.serializers KafkaAvroSerializer)))


(def test-config
  (producer/create-producer-properties "http://bootstraps:5231" "http://www.avroregistry.ru:1234"))

(deftest test-create-properties
  (testing "if config creation works"
    (log/info test-config)
    (is (= (.getProperty test-config ProducerConfig/BOOTSTRAP_SERVERS_CONFIG) "http://bootstraps:5231"))
    (is (= (.getProperty test-config "schema.registry.url") "http://www.avroregistry.ru:1234"))
    (is (= (.get test-config ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG) KafkaAvroSerializer))
    (is (= (.get test-config ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG) KafkaAvroSerializer))))


(deftest test-create-producer-record
  (testing "if producer record creation works"
    (is (= ProducerRecord
           (class (producer/create-producer-record "key" "topic" "value"))))))