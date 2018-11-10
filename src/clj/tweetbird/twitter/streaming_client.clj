(ns tweetbird.twitter.streaming-client
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
        [twitter.api.restful :refer :all]
        [clojure.tools.logging :as log]
        [clojure.data.json :as json]
        [tweetbird.kafka.producer :as p]
        [tweetbird.avro.builder :as builder])
  (:require [clojure.data.json :as json]
            [http.async.client :as ac])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)))

(def my-creds (make-oauth-creds "p2c24gKU0llacq324Hb0HVDw2"
                                "4qHXrT7p36l3kKkduXSBuGRmqZlQxiMLWbM8wo7B6DX9I82oxz"
                                "2787678847-2PboQQKDliWEgwFPAN1snJcTHgDBIZb5LYrh9me"
                                "xblXYXfQqMAjqxKnnxjk8K3uLzYJkGepBUYiH5CPkxh99"))

(defn process-status [producer _response baos]
  (try
    (let [user (:user (json/read-str (str baos) :key-fn keyword))]
      (if (not (nil? user))
        (.send producer (p/create-producer-record nil "test" (builder/build-registration-event user)))))
    (catch Exception e (if not (contains? (:cause e) "JSON") (log/error e)))))

(defn process-failure [failure] (log/error failure))

(defn process-exception [ex] (log/error ex))

(def ^:dynamic
 *custom-streaming-callback*
  (AsyncStreamingCallback. (partial process-status
                                    (p/create-producer
                                      (p/create-producer-properties "localhost:9092" "http://localhost:8081")))
                           process-failure
                           process-exception))

(statuses-sample :oauth-creds my-creds :callbacks *custom-streaming-callback*)
