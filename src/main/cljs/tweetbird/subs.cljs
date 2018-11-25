(ns tweetbird.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 ::name
 (fn [db]
   (:name db)))

(rf/reg-sub
  :number-users
  (fn [db _]
    (:number-users db)))