(defproject cljs-xml "0.0.1-SNAPSHOT"
  :description "A ClojureScript library to parse XML files"
  :url "https://github.com/funcool/cljs-xml"
  :license {:name "BSD (2 Clause)"
            :url "http://opensource.org/licenses/BSD-2-Clause"}

  :min-lein-version "2.3.4"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2629"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [com.cemerick/clojurescript.test "0.3.3"]]

  :hooks [leiningen.cljsbuild]

  :test-paths ["test"]
  :source-paths ["src" "test"]

  :cljsbuild {:builds [{:source-paths ["src" "test"]
                        :compiler {:output-to "target/cljs-xml.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}]
              :test-commands {"unit-tests" ["phantomjs" :runner
                                            "target/cljs-xml.js"]}})
