(ns tweetbird.backend
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as c]
            [tweetbird.metrics.metrics :as metrics]))

(defrecord Backend [config app-status scheduler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting Backend")
    (metrics/stream-statistics self)
    self)
  (stop [_]
    (log/info "<- stopping Backend")))

(defn new-backend []
  (map->Backend {:config (atom {:config {:desired_users 1000}})
                 :registered-users (atom #{})
                 :twitter-stream (atom nil)}))