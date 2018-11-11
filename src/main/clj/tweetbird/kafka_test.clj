(ns tweetbird.kafka-test
  (:require [tweetbird.kafka.producer :as p]
            [tweetbird.avro.builder :as b]
            [clojure.tools.logging :as log])
  (:import (org.apache.kafka.clients.producer ProducerRecord)))

(def example-user
  {:description "i like clojure"
   :id          768345786453278
   :verified    false
   :lang        "en"
   :name        "fluca"})

(let [producer (p/create-producer
                 (p/create-producer-properties
                   "localhost:9092" "http://localhost:8081"))
      record (p/create-producer-record nil "test" (b/build-registration-event example-user))]
  (log/info "SENDING")
  (.send producer record) (.flush producer))