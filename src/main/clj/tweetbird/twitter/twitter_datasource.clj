(ns tweetbird.twitter.twitter-datasource
  (:require [clojure.tools.logging :as log]
            [tweetbird.kafka.producer :as p]
            [tweetbird.avro.builder :as builder]
            [tweetbird.twitter.rest-client :as r]
            [twitter-streaming-client.core :as client]))

(defn publish-register-event [backend user-json producer]
  (if (not (contains? @(:registered-users backend) (:id user-json)))
    (p/send-message
      producer
      (p/create-producer-record
        "user_registrations"
        (:id user-json)
        (builder/build-registration-event user-json)))))

(defn publish-tweet-event [tweet-json producer]
  (p/send-message
    producer
    (p/create-producer-record
      "user_tweets"
      (:id tweet-json)
      (builder/build-tweet-event tweet-json))))

(defn publish-tweet-history-for-user [userid producer config]
  (doseq [tweet (:body (r/get-timeline userid config))]
    (publish-tweet-event tweet producer)))

(defn process-status [backend status producer config]
  (try
    (if (not (nil? (:user status)))
      (do (publish-register-event backend (:user status) producer)
          (publish-tweet-history-for-user (get-in status [:user :id]) producer config)))
    (publish-tweet-event status producer)
    (catch Exception e (log/error e))))

(defn make-producer
  [config] (p/create-producer
             (p/create-producer-properties (:bootstrap.servers config) (:schema-registry-url config))))

(defn streaming-callback [backend config stream]
  (doseq [tweet (:tweet (client/retrieve-queues stream))]
    (process-status backend tweet (make-producer config) config)))