(ns tweetbird.twitter.creds
  (:use [twitter.oauth]))

(defn make-creds [config] (make-oauth-creds (:app-key config)
                                (:app-secret config)
                                (:user-token config)
                                (:user-token-secret config)))
