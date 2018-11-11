(ns endpoint-test
  (:require [clojure.test :refer :all]
            [tweetbird.core :as core]
            [tweetbird.endpoint :as endpoint]
            [de.otto.tesla.util.test-utils :as tu]))

(def test-config "{\"desired_users\":10123}")

(deftest endpoint-test
  (testing "if put and get endpoints works"
    (tu/with-started
      [system (core/tweetbird-system {})]
      (endpoint/put-config-handler system test-config)
      (is (= 10123 (:desired_users @(get-in system [:backend :config]))))
      (is (= test-config (endpoint/get-config-handler system nil))))))
