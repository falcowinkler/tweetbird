(ns tweetbird.twitter.twitter-datasource-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tweetbird.core :as co]
            [tweetbird.twitter.twitter-datasource :as ds]
            [tweetbird.twitter.rest-client :as r]
            [clojure.data.json :as json]
            [de.otto.tesla.util.test-utils :as tu]
            [tweetbird.metrics.metrics :as m]
            [tweetbird.kafka.create-topics :as t]
            [tweetbird.twitter.config-mock :refer :all])
  (:import (de.haw.tweetspace.avro CustomerRegistration CustomerTweet)))

(defn to-map [file] (json/read-str (slurp (io/resource file))
                                   :key-fn keyword))

(def test-data (to-map "test_data.json"))
(def test-data_timeline (to-map "test_data_user_timeline.json"))

(deftest test-process-message
  (testing "if a message from the twitter streaming API is processed"
    (with-redefs [tweetbird.kafka.producer/send-message
                  (fn [_producer record]
                    (let [v (.value record)]
                      (if (= CustomerRegistration (class v))
                        (is (= "@eleonorabruzual" (.getName v))))
                      (if (= CustomerTweet (class v))
                        (is (= "mysource.com" (.getSource v))))))
                  tweetbird.twitter.rest-client/get-timeline
                  (fn [userid _config]
                    (is (= 115057872 userid)))
                  m/stream-statistics (fn [_ _])
                  t/create_required_topics (fn [_])]
      (tu/with-started [system (co/tweetbird-system {})]
                       (ds/process-status (:backend system) test-data fake-config)))))

(deftest test-publish-timeline
  (testing "if publishing a users timeline works"
    (with-redefs [r/get-timeline (fn [id _config] (is (= id 1234)) test-data_timeline)
                  tweetbird.kafka.producer/send-message
                  (fn [_producer record]
                    (is (or (= 1066349019885125632 (.getTweetId (.value record)))
                            (= 1066350012219707392 (.getTweetId (.value record))))))]
      (ds/publish-tweet-history-for-user 1234 "fake-producer" fake-config))))