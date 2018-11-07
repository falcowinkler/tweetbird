(ns tweetbird.avro.builder
  (:import (de.haw.tweetspace.avro CustomerRegistration)
           (org.joda.time DateTime)))


(defn build-registration-event [user]
  (-> (CustomerRegistration/newBuilder)
      (.setTimestamp (new DateTime))
      (.setDescription (:description user))
      (.setId (:id user))
      (.setVerified (:verified user))
      (.setLang (:lang user))
      (.build)))



