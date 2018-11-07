(ns tweetbird.kafka.producer
  (:require [franzy.serialization.serializers :as serializers]
            [franzy.examples.configuration :as config]
            [franzy.clients.producer.client :as producer]
            [franzy.clients.producer.defaults :as pd]
            [franzy.clients.producer.protocols :refer :all]
            [franzy.clients.producer.types :as pt]))


(let [;;Use a vector if you wish for multiple servers in your cluster
      pc {:bootstrap.servers ["localhost:9092"]}
      ;;Serializes producer record keys that may be keywords
      key-serializer (io.confluent.kafka.serializers.KafkaAvroDeserializer)
      ;;Serializes producer record values as EDN, built-in
      value-serializer (io.confluent.kafka.serializers.KafkaAvroSerializer)
      ;;optionally create some options, even just use the defaults explicitly
      ;;for those that don't need anything fancy...
      options (pd/make-default-producer-options {:schema.registry.url ""})
      topic "test"
      partition 0]
  (with-open [p (producer/make-producer pc key-serializer value-serializer options)]
    (let [send-fut (send-async! p topic partition :inconceivable {:things-in-fashion
                                                                  [:masks :giants :kerry-calling-saul]} options)]

      (println "Async send results:" @send-fut))))