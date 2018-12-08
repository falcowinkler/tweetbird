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
        [overtone.at-at]))

(defn make-stream [config] (client/create-twitter-stream twitter.api.streaming/statuses-sample
                                          :oauth-creds (c/make-creds config)))
(defn stop-consuming [backend]
  (stop-and-reset-pool! (:pool (:scheduler backend)))
  (client/cancel-twitter-stream (:twitter-stream backend)))

(defn start-consuming [backend callback config]
  (log/info "Starting to consume statuses/sample")
  (reset! (:twitter-stream backend) (make-stream config))
  (client/start-twitter-stream @(:twitter-stream backend))
  (every 1000 (partial callback @(:twitter-stream backend))
         (s/pool (:scheduler backend))))

