(ns tweetbird.twitter.twitter-datasource-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tweetbird.twitter.twitter-datasource :as ds]
            [tweetbird.twitter.rest-client :as r]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log])
  (:import (de.haw.tweetspace.avro CustomerRegistration CustomerTweet)))


(def test-data (slurp (io/resource "test_data.json")))
(def test-data_timeline (json/read-str (slurp (io/resource "test_data_user_timeline.json"))
                                       :key-fn keyword))

(deftest test-process-message
  (testing "if a message from the twitter streaming API is processed"
    (with-redefs [tweetbird.kafka.producer/send-message
                  (fn [producer record]
                    (let [v (.value record)]
                      (if (= CustomerRegistration (class v))
                        (is (= "@eleonorabruzual" (.getName v))))
                      (if (= CustomerTweet (class v))
                        (is (= "mysource.com" (.getSource v)))))
                    (is (= "fake-producer" producer)))
                  tweetbird.twitter.rest-client/get-timeline
                  (fn [userid]
                    (is (= 115057872 userid)))]
      (ds/process-status "fake-producer" nil test-data))))

(deftest test-publish-timeline
  (testing "if publishing a users timeline works"
    (with-redefs [r/get-timeline (fn [id] (is (= id 1234)) test-data_timeline)
                  tweetbird.kafka.producer/send-message
                  (fn [producer record]
                    (is (= "fake-producer" producer))
                    (is (or (= 1066349019885125632 (.getId (.value record)))
                            (= 1066350012219707392 (.getId (.value record))))))]
      (ds/publish-tweet-history-for-user 1234 "fake-producer"))))