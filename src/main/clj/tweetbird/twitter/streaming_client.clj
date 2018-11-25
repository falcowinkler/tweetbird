(ns tweetbird.twitter.streaming-client
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
        [twitter.api.restful :refer :all]
        [clojure.tools.logging :as log]
        [twitter-streaming-client.core :as client]
        [tweetbird.twitter.creds :as c]
        [overtone.at-at]))

(def stream (client/create-twitter-stream twitter.api.streaming/statuses-sample
                                          :oauth-creds c/my-creds))
(defn stop-consuming [pool]
  (stop-and-reset-pool! pool)
  (client/cancel-twitter-stream stream))

(defn start-consuming [backend callback]
  (log/info "Starting to consume statuses/sample")
  (client/start-twitter-stream stream)
  (every 1000 (partial callback stream) (de.otto.tesla.stateful.scheduler/pool (:scheduler backend))))

