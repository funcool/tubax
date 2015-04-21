(defproject cljs-xml "0.1.0-SNAPSHOT"
  :description "A ClojureScript library to parse XML files"
  :url "https://github.com/funcool/tubax"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3196"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [funcool/cljs-testrunners "0.1.0-SNAPSHOT"]
                 [funcool/promesa "0.1.1"]]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-externs "0.1.3"]]

  :hooks [leiningen.cljsbuild]

  :test-paths ["test"]
  :source-paths ["src" "test"]

  :cljsbuild {:test-commands {"test" ["node" "output/tests.js"]}
              :builds
              [{:id "dev"
                :source-paths ["src" "test" "assets"]
                :notify-command ["node" "output/tests.js"]
                :compiler {:output-to "output/tests.js"
                           :output-dir "output/out"
                           :source-map true
                           :static-fns true
                           :cache-analysis false
                           :main cljs-xml.core-test
                           :optimizations :none
                           :target :nodejs
                           :pretty-print true }}]})
