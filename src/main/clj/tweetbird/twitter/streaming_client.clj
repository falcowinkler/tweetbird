(ns tweetbird.twitter.streaming-client
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.streaming]
        [twitter.api.restful :refer :all]
        [clojure.tools.logging :as log]
        [tweetbird.twitter.creds :as c]))

(defn stop-consuming []
  (http.async.client/close (twitter.core/default-client)))

(defn start-consuming [callback]
  (log/info "Starting to consume statuses/sample")
  (statuses-sample :oauth-creds c/my-creds :callbacks callback))


