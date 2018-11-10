(ns tweetbird.backend
  (:require [clojure.tools.logging :as log]
            [de.otto.status :as st]
            [de.otto.tesla.stateful.app-status :as as]
            [com.stuartsierra.component :as c]))

(defrecord Backend [app-status scheduler]
  c/Lifecycle
  (start [self]
    (log/info "-> starting Backend")
    (let [config-atom (atom {:desired_users 1000})]
      (assoc self :config config-atom)))
  (stop [_]
    (log/info "<- stopping Backend")))

(defn new-backend []
  (map->Backend {}))