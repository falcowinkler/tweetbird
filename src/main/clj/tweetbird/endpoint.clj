(ns tweetbird.endpoint
  (:require [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log]
            [clojure.data.json :as json]
            [de.otto.tesla.stateful.handler :as handler]
            [ring.middleware.json :as json-middleware]
            [ring.middleware.keyword-params :as kparams]
            [tweetbird.twitter.streaming-client :as s]
            [de.otto.goo.goo :as metrics]
            [compojure.core :as cc]
            [tweetbird.twitter.twitter-datasource :as ds]
            [ring.middleware.params :as params]))

(defn get-stats-handler [{:keys [backend]} req]
  ;(log/info req)
  {:status  200
   :headers {"content-type" "application/json"
             "Access-Control-Allow-Origin" "http://localhost:3449"
             "Access-Control-Allow-Credentials" "true"
             "Access-Control-Allow-Methods" "GET,PUT,POST,DELETE,OPTIONS"
             "Access-Control-Allow-Headers" "X-Requested-With,Content-Type,Cache-Control"}
   :body    (json/write-str
              {:number-users (count @(:registered-users backend))})})

(defn get-config-handler [{:keys [backend]} _req]
  @(:config backend))

(defn put-config-handler [{:keys [backend]} body]
  (let [cfg (json/read-str (str body) :key-fn keyword)]
    (log/info (str "Setting new config: " cfg))
    (reset! (:config backend) cfg)))

(defn start-handler [{:keys [backend]} body]
  (s/start-consuming backend (partial ds/streaming-callback backend))
  "OK")

(defn stop-handler [{:keys [backend]} body]
  (s/stop-consuming (:pool (:scheduler backend)))
  "OK")

(defn create-routes [self]
  (cc/routes
    (cc/POST "/stop/" req (stop-handler self req))
    (cc/POST "/stop" req (stop-handler self req))
    (cc/POST "/start/" req (start-handler self req))
    (cc/POST "/start" req (start-handler self req))
    (cc/GET "/stats/" req (get-stats-handler self req))
    (cc/GET "/stats" req (get-stats-handler self req))
    (cc/GET "/config/" req (get-config-handler self req))
    (cc/GET "/config" req (get-config-handler self req))
    (cc/PUT "/config/" {body :body} (put-config-handler self body))
    (cc/PUT "/config" {body :body} (put-config-handler self body))))



(defrecord Endpoint [handler backend]
  c/Lifecycle
  (start [self]
    (log/info "-> starting Endpoint")
    (handler/register-handler
      handler
        (kparams/wrap-keyword-params
          (params/wrap-params
            (create-routes self))))
    self)
  (stop [_]
    (log/info "<- stopping Endpoint")))

(defn new-endpoint []
  (map->Endpoint {}))