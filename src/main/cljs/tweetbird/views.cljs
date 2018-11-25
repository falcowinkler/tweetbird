(ns tweetbird.views
  (:require
    [re-frame.core :as rf]
    [tweetbird.subs :as subs]))


(defn dashboard []
  (let [name (rf/subscribe [::subs/name])]
    [:div [:div {:class "flex-grid"}
           [:div {:class "col"} (str @(rf/subscribe [:number-users]) " registered users")]
           [:div {:class "col"} "8500 messages/sec"]]
     [:div {:class "flex-grid"}
      [:div {:class "col"}
       "targeted messages/sec"
       [:input {:type "text" :class "default-text"}]
       [:button {:type "button" :class "default-button"} "set target messages/s"]]
      [:div {:class "col start-stop"} "Start/Stop"
       [:button {:type "button"} "Stop"]]]]))

