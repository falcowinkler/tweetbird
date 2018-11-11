(ns tweetbird.views
  (:require
    [re-frame.core :as re-frame]
    [tweetbird.subs :as subs]))


(defn dashboard []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div [:div {:class "flex-grid"}
           [:div {:class "col"} "1000 registered users"]
           [:div {:class "col"} "8500 messages/sec"]]
     [:div {:class "flex-grid"}
      [:div {:class "col"}
       "targeted messages/sec"
       [:input {:type "text" :class "default-text"}]
       [:button {:type "button" :class "default-button"} "set target messages/s"]]
      [:div {:class "col start-stop"} "Start/Stop"
       [:button {:type "button"} "Stop"]]]]))

