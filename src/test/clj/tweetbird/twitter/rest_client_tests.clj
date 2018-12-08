(ns tweetbird.twitter.rest-client-tests
  (:require
    [clojure.test :refer :all]
    [twitter.core]
    [tweetbird.twitter.rest-client :as r]
    [clojure.java.io :as io]
    [clojure.tools.logging :as log]
    [clojure.data.json :as json]
    [tweetbird.twitter.config-mock :refer :all]))


(def test-data (json/read-str (slurp (io/resource "test_data_user_timeline.json")) :key-fn keyword))

(deftest test-get-timeline
  (testing "if get timeline works"
    (with-redefs [twitter.core/http-request (fn [_ _ _] test-data)]
      (is (= 1066350012219707392
             (:id (first (:body (r/get-timeline 115057872 fake-config)))))))))