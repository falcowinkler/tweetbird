(ns tweetbird.events
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [re-frame.core :as rf]
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [tweetbird.db :as db]))


(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))

(defonce do-timer (js/setInterval dispatch-timer-event 1000))

(rf/reg-event-db
  :data-available
  (fn [db [_ data]]
    (assoc-in db [:number-users] (:number-users (:body data)))))

(rf/reg-event-db
  :timer
  (fn [db [_ new-time]]
    (go (let [response (<! (http/get "http://localhost:8080/stats"
                                     {:with-credentials? false}))]
          (rf/dispatch [:data-available response])))
    db))
