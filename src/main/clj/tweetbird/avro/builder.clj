(ns tweetbird.avro.builder
  (:require [clojure.tools.logging :as log])
  (:import (de.haw.tweetspace.avro CustomerRegistration CustomerTweet)
           (org.joda.time DateTime)
           (org.joda.time.format DateTimeFormat)
           (java.util Locale)))

(def utc-date-formatter
  (-> (DateTimeFormat/forPattern "EEE MMM dd HH:mm:ss '+0000' yyyy")
      (.withLocale Locale/ENGLISH)
      (.withOffsetParsed)))


(defn build-registration-event [user]
  (-> (CustomerRegistration/newBuilder)
      (.setTimestamp (new DateTime))
      (.setDescription (:description user))
      (.setTwitterUserId (:id user))
      (.setName (:name user))
      (.setVerified (:verified user))
      (.setLang (:lang user))
      (.build)))

(defn build-tweet-event [tweet]
  (-> (CustomerTweet/newBuilder)
      (.setTimestamp
        (new DateTime (Long/valueOf
                        (if (not (nil? (:timestamp_ms tweet)))
                          (:timestamp_ms tweet)
                          (quot (System/currentTimeMillis) 1000)))))
      (.setTweetId (:id tweet))
      (.setText (:text tweet))
      (.setCreatedAt (DateTime/parse (:created_at tweet) utc-date-formatter))
      (.setSource (:source tweet))
      (.setInReplyToStatusId (:in_reply_to_status_id tweet))
      (.setInReplyToTwitterUserId (:in_reply_to_user_id tweet))
      (.setTwitterUserId (get-in tweet [:user :id]))
      (.setLang (:lang tweet))
      (.build)))