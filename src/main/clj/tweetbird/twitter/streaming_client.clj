(ns tweetbird.twitter.streaming-client
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
        [twitter.api.restful :refer :all]
        [clojure.tools.logging :as log]
        [clojure.data.json :as json]
        [tweetbird.kafka.producer :as p]
        [tweetbird.avro.builder :as builder]
        [clojure.data.json :as json])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(def my-creds (make-oauth-creds "p2c24gKU0llacq324Hb0HVDw2"
                                "4qHXrT7p36l3kKkduXSBuGRmqZlQxiMLWbM8wo7B6DX9I82oxz"
                                "2787678847-2PboQQKDliWEgwFPAN1snJcTHgDBIZb5LYrh9me"
                                "xblXYXfQqMAjqxKnnxjk8K3uLzYJkGepBUYiH5CPkxh99"))

(defn publish-register-event [user-json producer]
  (p/send-message
    producer
    (p/create-producer-record
      (:id user-json)
      "user_registrations"
      (builder/build-registration-event user-json))))

(defn publish-tweet-event [tweet-json producer]
  (p/send-message
    producer
    (p/create-producer-record
      (:timestamp_ms tweet-json)
      "user_tweets"
      (builder/build-tweet-event tweet-json))))

(defn- ignore-json-errors [exception]
  ;process-status is sometimes called with incomplete json data, we ignore that for now
  (if not (contains? (:cause exception) "JSON")
          (log/error exception)))

(defn process-status [producer _response baos]
  (try
    (let [tweet (json/read-str (str baos) :key-fn keyword)]
      (publish-tweet-event tweet producer)
      (if (not (nil? (:user tweet)))
        (publish-register-event (:user tweet) producer)))
    (catch Exception e (ignore-json-errors e))))

(defn process-failure [failure] (log/error failure))

(defn process-exception [exception] (log/error exception))

(def ^:dynamic streaming-callback
  (AsyncStreamingCallback. (partial process-status
                                    (p/create-producer
                                      (p/create-producer-properties "localhost:9092" "http://localhost:8081")))
                           process-failure
                           process-exception))

(defn stop-consuming []
  (http.async.client/close (twitter.core/default-client)))

(defn start-consuming []
  (log/info "Starting to consume statuses/sample")
  (statuses-sample :oauth-creds my-creds :callbacks streaming-callback))
