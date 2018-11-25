(ns tweetbird.backend
  (:require [clojure.tools.logging :as log]
            [de.otto.status :as st]
            [de.otto.tesla.stateful.app-status :as as]
            [com.stuartsierra.component :as c]
            [tweetbird.metrics.metrics :as metrics]))

(defrecord Backend [app-status scheduler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting Backend")
    (log/info self)
    (metrics/stream-statistics self)
    self)
  (stop [_]
    (log/info "<- stopping Backend")))

(defn new-backend []
  (map->Backend {:config (atom {:config {:desired_users 1000}}) :registered-users (atom #{})}))