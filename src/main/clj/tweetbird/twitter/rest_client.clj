(ns tweetbird.twitter.rest-client
  (:use [twitter.api.restful]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [tweetbird.twitter.creds :as c])
  (:require [clojure.data.json :as json]))


(defn get-timeline [user_id]
  (json/read-str
    (statuses-user-timeline
      :oauth-creds c/my-creds
      :params {:user_id user_id})
    :key-fn keyword))

