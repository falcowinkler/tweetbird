(ns tweetbird.twitter.streaming-client-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tweetbird.twitter.streaming-client :as s]))

(def test-data (slurp (io/resource "test_data.json")))

(deftest test-process-message
  (testing "if a message from the twitter streaming API is processed"
    (with-redefs [tweetbird.kafka.producer/send-message
                  (fn [producer record]
                    (let [value (.value record)]
                      (is (= "@eleonorabruzual" (.getUsername value))))
                    (is (= "fake-producer" producer)))]
      (s/process-status "fake-producer" nil test-data))))