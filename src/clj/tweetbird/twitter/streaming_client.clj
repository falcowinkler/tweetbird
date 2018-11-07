(ns tweetbird.twitter.streaming-client
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
        [twitter.api.restful :refer :all]
        [clojure.tools.logging :as log]
        [clojure.data.json :as json]
        [twitter.api.search :as search])
  (:require [clojure.data.json :as json]
            [http.async.client :as ac])
  (:import (twitter.callbacks.protocols AsyncStreamingCallback)
           (de.haw.tweetspace.avro :refer :all CustomerDeregistration)))

(def my-creds (make-oauth-creds "p2c24gKU0llacq324Hb0HVDw2"
                                "4qHXrT7p36l3kKkduXSBuGRmqZlQxiMLWbM8wo7B6DX9I82oxz"
                                "2787678847-2PboQQKDliWEgwFPAN1snJcTHgDBIZb5LYrh9me"
                                "xblXYXfQqMAjqxKnnxjk8K3uLzYJkGepBUYiH5CPkxh99"))

(defn process-status [_response baos]
  (try
    (log/info (:user (json/read-str (str baos) :key-fn keyword)))
    (catch Exception e))) ;discard incomplete or faulty data

(defn process-failure [failure] (log/error failure))

(defn process-exception [ex] (log/error ex))

(def ^:dynamic
 *custom-streaming-callback*
  (AsyncStreamingCallback. process-status
                           process-failure
                           process-exception))

(statuses-sample :oauth-creds my-creds :callbacks *custom-streaming-callback* CustomerDeregistration)
