(ns tweetbird.avro.builder
  (:require [clojure.tools.logging :as log])
  (:import (de.haw.tweetspace.avro CustomerRegistration)
           (org.joda.time DateTime)))


(defn build-registration-event [user]
  (-> (CustomerRegistration/newBuilder)
      (.setTimestamp (new DateTime))
      (.setDescription (:description user))
      (.setId (str (:id user)))
      (.setUsername (:name user))
      (.setVerified (:verified user))
      (.setLang (:lang user))
      (.build)))