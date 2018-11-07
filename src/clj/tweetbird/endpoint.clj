(ns tweetbird.endpoint
  (:require [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [de.otto.tesla.stateful.handler :as handler]
            [ring.middleware.params :as params]
            [ring.middleware.keyword-params :as kparams]
            [de.otto.goo.goo :as metrics]
            [compojure.core :as cc]))


(defn get-users-handler [{:keys [backend]} req] (get-in backend [:data :users]))

(defn put-users-handler [{:keys [backend]} req] (assoc-in backend [:data :users] 1))

(defn get-watches-handler [{:keys [backend]} req] (get-in backend [:data :watches]))

(defn put-watches-handler [{:keys [backend]} req] (assoc-in backend [:data :watches] ["watch"]))

(defn delete-watches-handler [{:keys [backend]} req] (remove "watch" (get-in backend [:data :watches])))

(defn create-routes [self]
  (cc/routes
    (cc/GET "/users/" req (get-users-handler self req))
    (cc/GET "/users" req (get-users-handler self req))
    ;TODO query parameters
    (cc/PUT "/users/" req (put-users-handler self req))
    (cc/PUT "/users" req (put-users-handler self req))
    (cc/GET "/watches/" req (get-watches-handler self req))
    (cc/GET "/watches" req (get-watches-handler self req))
    (cc/PUT "/watches/" req (put-watches-handler self req))
    (cc/PUT "/watches" req (put-watches-handler self req))
    (cc/DELETE "/watches/" req (delete-watches-handler self req))
    (cc/DELETE "/watches" req (delete-watches-handler self req))))

(defrecord Endpoint [handler backend]
  c/Lifecycle
  (start [self]
    (log/info "-> starting Endpoint")
    (handler/register-handler handler (create-routes self))
    self)
  (stop [_]
    (log/info "<- stopping Endpoint")))

(defn new-endpoint []
  (map->Endpoint {}))