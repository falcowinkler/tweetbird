(ns tweetbird.twitter.twitter-datasource
  (:require [clojure.tools.logging :as log]
            [tweetbird.kafka.producer :as p]
            [tweetbird.avro.builder :as builder]
            [tweetbird.twitter.rest-client :as r]
            [clojure.data.json :as json])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))


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

(defn- ignore-json-errors [exception]
  ;process-status is sometimes called with incomplete json data, we ignore that for now
  (log/error exception))

(defn publish-tweet-history-for-user [userid producer]
  (doseq [tweet (:body (r/get-timeline userid))]
    (publish-tweet-event tweet producer)))


(defn process-status [producer _response baos]
  (try
    (let [tweet (json/read-str (str baos) :key-fn keyword)]
      (if (not (nil? (:user tweet)))
        (do (publish-register-event (:user tweet) producer)
            (publish-tweet-history-for-user (get-in tweet [:user :id]) producer)))
      (publish-tweet-event tweet producer))
    (catch Exception e (ignore-json-errors e))))

(defn process-failure [failure] (log/error failure))

(defn process-exception [exception] (log/error exception))

(def ^:dynamic streaming-callback
  (AsyncStreamingCallback. (partial process-status
                                    (p/create-producer
                                      (p/create-producer-properties "localhost:9092" "http://localhost:8081")))
                           process-failure
                           process-exception))


