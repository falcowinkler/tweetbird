(ns tweetbird.twitter.twitter-datasource
  (:require [clojure.tools.logging :as log]
            [tweetbird.kafka.producer :as p]
            [tweetbird.avro.builder :as builder]
            [tweetbird.twitter.rest-client :as r]
            [twitter-streaming-client.core :as client]))

(defn publish-register-event [user-json producer]
  (p/send-message
    producer
    (p/create-producer-record
      "user_registrations"
      (:id user-json)
      (builder/build-registration-event user-json))))

(defn publish-tweet-event [tweet-json producer]
  (p/send-message
    producer
    (p/create-producer-record
      "user_tweets"
      (:id tweet-json)
      (builder/build-tweet-event tweet-json))))

(defn publish-tweet-history-for-user [userid producer]
  (doseq [tweet (:body (r/get-timeline userid))]
    (publish-tweet-event tweet producer)))

(defn process-status [status producer]
  (try
      (if (not (nil? (:user status)))
        (do (publish-register-event (:user status) producer)
            (publish-tweet-history-for-user (get-in status [:user :id]) producer)))
      (publish-tweet-event status producer)
    (catch Exception e (log/error e))))

(def producer (p/create-producer
                (p/create-producer-properties "localhost:9092" "http://localhost:8081")))

(defn streaming-callback [stream]
  (doseq [tweet (:tweet (client/retrieve-queues stream))]
    (process-status tweet producer)))



