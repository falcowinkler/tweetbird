(ns tweetbird.core
  (:require [com.stuartsierra.component :as c]
            [clojure.tools.logging :as log]
            [de.otto.tesla.system :as system]
            [de.otto.status :as st]
            [de.otto.tesla.stateful.app-status :as as]
            [tweetbird.backend :as b]
            [iapetos.collector.jvm :as jvm]
            [de.otto.tesla.serving-with-httpkit :as httpkit]
            [de.otto.goo.goo :as goo]
            [tweetbird.endpoint :as ep] [tweetbird.frontend :as fe])
  (:gen-class))


(defn tweetbird-system [runtime-config]
  (log/info "Runtime config: ")
  (log/info runtime-config)
  (-> (system/base-system runtime-config)
      (assoc
        :backend (c/using (b/new-backend) [:config :app-status :scheduler])
        :endpoint (c/using (ep/new-endpoint) [:config :handler :backend])
        :frontend (c/using (fe/new-frontend) [:handler]))
      (httpkit/add-server :endpoint :frontend)))

(defonce _ (jvm/initialize (goo/snapshot)))
(defonce _ (Thread/setDefaultUncaughtExceptionHandler
             (reify Thread$UncaughtExceptionHandler
               (uncaughtException [_ thread ex]
                 (log/error ex "Uncaught exception on " (.getName thread))))))

(defn -main [& _]
  (system/start (tweetbird-system {})))

