(ns tweetbird.twitter.streaming-client-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [tweetbird.twitter.streaming-client :as s]
            [clojure.tools.logging :as log])
  (:import (de.haw.tweetspace.avro CustomerTweet CustomerRegistration)))

(def test-data (slurp (io/resource "test_data.json")))

(deftest test-process-message
  (testing "if a message from the twitter streaming API is processed"
    (with-redefs [tweetbird.kafka.producer/send-message
                  (fn [producer record]
                    (let [v (.value record)]
                      (if (= CustomerRegistration (class v))
                        (is (= "@eleonorabruzual" (.getName v))))
                      (if (= CustomerTweet (class v))
                        (is (= "mysource.com" (.getSource v)))))
                    (is (= "fake-producer" producer)))]
      (s/process-status "fake-producer" nil test-data))))