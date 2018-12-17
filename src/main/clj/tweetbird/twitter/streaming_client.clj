(ns tweetbird.twitter.streaming-client
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
        [twitter.api.restful :refer :all]
        [clojure.tools.logging :as log]
        [twitter-streaming-client.core :as client]
        [tweetbird.twitter.creds :as c]
        [de.otto.tesla.stateful.scheduler :as s]
        [overtone.at-at])
  (:require [overtone.at-at :as at]))

(defn make-stream [config] (client/create-twitter-stream twitter.api.streaming/statuses-sample
                                          :oauth-creds (c/make-creds config)))
(defn stop-consuming [backend]
  (at/stop-and-reset-pool! (:scheduler backend) :strategy :kill)
  (Thread/sleep 1000) ; Need to find a way to wait for overtone shutdown
  (client/cancel-twitter-stream @(:twitter-stream backend))
  (await @(:twitter-stream backend))
  (.flush @(:kafka-producer backend))
  (.close @(:kafka-producer backend))
  (reset! (:kafka-producer backend) nil)
  (log/info "Stopped consuming statuses/sample"))

(defn start-consuming [backend callback config]
  (log/info "Starting to consume statuses/sample")
  (reset! (:twitter-stream backend) (make-stream config))
  (client/start-twitter-stream @(:twitter-stream backend))
  (every 1000 (partial callback @(:twitter-stream backend))
         (s/pool (:scheduler backend))))

