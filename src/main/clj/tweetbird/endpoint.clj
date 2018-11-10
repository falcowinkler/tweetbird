(ns tweetbird.endpoint
  (:require [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [de.otto.tesla.stateful.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [ring.middleware.keyword-params :as kparams]
            [de.otto.goo.goo :as metrics]
            [compojure.core :as cc]))


(defn get-config-handler [{:keys [backend]} _req] (json/write-str @(:config backend)))

(defn put-config-handler [{:keys [backend]} body]
  (let [cfg (json/read-str (str body) :key-fn keyword)]
    (log/info (str "Setting new config: " cfg))
    (reset! (:config backend) cfg)))

(defn create-routes [self]
  (cc/routes
    (cc/GET "/config/" req (get-config-handler self req))
    (cc/GET "/config" req (get-config-handler self req))
    (cc/PUT "/config/" {body :body} (put-config-handler self body))
    (cc/PUT "/config" {body :body} (put-config-handler self body))))

(defrecord Endpoint [handler backend]
  c/Lifecycle
  (start [self]
    (log/info "-> starting Endpoint")
    (handler/register-handler handler (json-middleware/wrap-json-body (create-routes self)))
    self)
  (stop [_]
    (log/info "<- stopping Endpoint")))

(defn new-endpoint []
  (map->Endpoint {}))