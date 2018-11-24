(ns tweetbird.twitter.streaming-client-tests
  (:require [clojure.test :refer :all]
            [tweetbird.twitter.streaming-client :as s]))

(deftest start-works
  (testing "if start is working"
    (with-redefs
      [twitter.api.streaming/statuses-sample
       (fn [_ _ _ callback]
         (is (= callback "fake-callback")))]
      (s/start-consuming "fake-callback"))))

(deftest stop-works
  (testing "if stop is working"
    (with-redefs [twitter.core/default-client (fn [] "fake-client")
                  http.async.client/close (fn [client] (is (= client "fake-client")))]
      (s/stop-consuming))))