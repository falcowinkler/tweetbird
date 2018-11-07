(ns tweetbird.backend
  (:require [clojure.tools.logging :as log]
            [de.otto.status :as st]
            [de.otto.tesla.stateful.app-status :as as]
            [com.stuartsierra.component :as c]))

(defrecord Backend [app-status scheduler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting Backend")
    (update-in self [:data] assoc :users 1000 :watches ["football"]))
  (stop [_]
    (log/info "<- stopping Backend")))

(defn new-backend []
  (map->Backend {}))