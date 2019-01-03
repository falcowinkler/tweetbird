(ns tweetbird.kafka.create-topics
  (:require [clojure.tools.logging :as log])
  (:import (org.I0Itec.zkclient ZkClient ZkConnection)
           (kafka.utils ZKStringSerializer$ ZkUtils)
           (kafka.admin AdminUtils RackAwareMode$Disabled$)
           (java.util Properties)
           (org.apache.kafka.common.errors TopicExistsException)))

(def required-topics ["user_registrations" "user_tweets"])

(defn create-topic [topic-name config]
  (let [zk-client (new ZkClient (:zk.connect config) (* 1000 10) (* 1000 10) ZKStringSerializer$/MODULE$)
        zk-utils (new ZkUtils zk-client (new ZkConnection (:zk.connect config)) false)]
    (try
      (AdminUtils/createTopic
        zk-utils
        topic-name
        (:topics-partition-count config)
        (:topics-replication-count config)
        (new Properties)
        RackAwareMode$Disabled$/MODULE$)
      (catch TopicExistsException _e
        (log/info "Topic " topic-name " already exists.")))))

(defn create_required_topics [{:keys [config]}]
  (doseq [topic required-topics] (create-topic topic config)))

