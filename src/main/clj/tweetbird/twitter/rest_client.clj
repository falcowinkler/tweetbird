(ns tweetbird.twitter.rest-client
  (:use [twitter.api.restful]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [tweetbird.twitter.creds :as c]))

(defn get-timeline [user_id config]
    (statuses-user-timeline
      :oauth-creds (c/make-creds config)
      :params {:user_id user_id}))

