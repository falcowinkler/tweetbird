(ns endpoint-test
  (:require [clojure.test :refer :all]
            [tweetbird.core :as core]
            [tweetbird.endpoint :as endpoint]
            [de.otto.tesla.util.test-utils :as tu]
            [tweetbird.metrics.metrics :as m]
            [tweetbird.kafka.create-topics :as t]))

(def test-config "{\"desired_users\":10123}")

(deftest endpoint-test
  (testing "if put and get endpoints works"
    (with-redefs [m/stream-statistics (fn [_ _]) t/create_required_topics (fn [_])]
      (tu/with-started
        [system (core/tweetbird-system {})]
        (endpoint/put-config-handler system test-config)
        (is (= 10123 (:desired_users @(get-in system [:backend :runtime-configuration]))))
        (is (= {:desired_users 10123} (endpoint/get-config-handler system nil)))))))
