(ns clj.tweetbird.avro.builder-tests
  (:require [clojure.test :refer :all]
            [tweetbird.avro.builder :as builder]))


(def example-user
  {:description "i like coding"
   :id          "768345786453278"
   :verified    false
   :lang        "en"
   :name        "fluca"})

(def example-full-user {:description nil, :profile_link_color "1B95E0", :profile_sidebar_border_color 000000, :profile_image_url "http://pbs.twimg.com/profile_images/1052305612451733504/K9ws3_Ef_normal.jpg", :profile_use_background_image false, :default_profile false, :profile_background_image_url "http://abs.twimg.com/images/themes/theme1/bg.png", :is_translator false, :profile_text_color 000000, :profile_banner_url "https://pbs.twimg.com/profile_banners/1052304562604765184/1539724899", :name "mona clark 💫", :profile_background_image_url_https "https://abs.twimg.com/images/themes/theme1/bg.png", :favourites_count 17528, :screen_name "clarkmo12", :listed_count 1, :profile_image_url_https "https://pbs.twimg.com/profile_images/1052305612451733504/K9ws3_Ef_normal.jpg", :statuses_count 7507, :contributors_enabled false, :following nil, :lang "en", :utc_offset nil, :notifications nil, :default_profile_image false, :profile_background_color 000000, :id "1052304562604765184", :follow_request_sent nil, :url nil, :translator_type "none", :time_zone nil, :profile_sidebar_fill_color 000000, :protected false, :profile_background_tile false, :id_str 1052304562604765184, :geo_enabled false, :location nil, :followers_count 95, :friends_count 678, :verified false, :created_at "Tue Oct 16 21:05:33 +0000 2018"})

(deftest builder-test
  (testing "Testing if building a registration event works"
    (let [avro-user (builder/build-registration-event example-user)]
      (is (= (.getDescription avro-user) "i like coding"))
      (is (= (.getId avro-user) "768345786453278"))
      (is (= (.getVerified avro-user) false))
      (is (= (.getLang avro-user) "en"))
      (is (= (.getUsername avro-user) "fluca")))))

(deftest test-full-user
  (testing "if a real-life example gets parsed"
    (let [avro-user (builder/build-registration-event example-full-user)]
      (is (= (.getDescription avro-user) nil)))))