(ns tweetbird.twitter.creds
  (:use [twitter.oauth])
  (:require [clojure.tools.logging :as log]))

(defn make-creds [{:keys [config]}]
  (make-oauth-creds (:twitter-app-key config)
                    (:twitter-app-secret config)
                    (:twitter-user-token config)
                    (:twitter-user-token-secret config)))
