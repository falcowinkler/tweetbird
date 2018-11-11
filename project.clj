(defproject tweetbird "0.1.0-SNAPSHOT"

  :repositories {"confluent" "https://packages.confluent.io/maven/"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.10.238"]
                 [reagent "0.7.0"]
                 [re-frame "0.10.5"]
                 [twitter-api "1.8.0"]
                 [de.otto/tesla-microservice "0.11.25"]
                 [de.otto/tesla-httpkit "1.0.1"]
                 [org.clojure/tools.logging "0.4.0"]
                 [hiccup "1.0.5"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-mock "0.3.2"]
                 [org.apache.kafka/kafka-clients "2.0.0"]
                 [de.haw.tweetspace/avro-events "1.0-SNAPSHOT"]
                 [com.fasterxml.jackson.core/jackson-databind "2.9.7"]
                 [io.confluent/kafka-avro-serializer "4.1.2" :exclusions [org.slf4j/slf4j-log4j12]]]


  :plugins [[lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.8"]
            [deraen/lein-sass4clj "0.3.1"]]


  :min-lein-version "2.5.3"

  :source-paths ["src/main/clj" "src/main/cljs"]

  :test-paths ["src/test/clj"]

  :resource-paths ["resources" "src/main/resources"]

  :uberjar-name "tweetbird-deploy-standalone.jar"

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]}

  :profiles
  {:uberjar {:aot :all}
   :dev
            {:resource-paths ["src/test/resources"]
             :dependencies [[binaryage/devtools "0.9.10"]]

             :plugins      [[lein-figwheel "0.5.16"]]}
   :prod    {}}

  :sass {:source-paths ["src/main/scss/"]
         :target-path  "resources/public/css"}
  :main tweetbird.core
  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/main/cljs"]
     :figwheel     {:on-jsload "tweetbird.core/mount-root"}
     :compiler     {:main                 tweetbird.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}


    {:id           "min"
     :source-paths ["src/main/cljs"]
     :compiler     {:main            tweetbird.core
                    :output-to       "resources/public/js/compiled/app.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]})




