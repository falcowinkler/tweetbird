(ns endpoint-test
  (:require [clojure.test :refer :all]
            [tweetbird.core :as core]
            [de.otto.tesla.util.test-utils :as tu]))


(deftest endpoint-test
  (testing "if put and get endpoints works"
    (tu/with-started [system (core/tweetbird-system {})])))