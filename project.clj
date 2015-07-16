(defproject funcool/tubax "0.1.2"
  :description "A ClojureScript library to parse XML files"
  :url "https://github.com/funcool/tubax"
  :license {:name "Apache 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0.txt"}

  :profiles {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                                  [org.clojure/clojurescript "0.0-3196"]
                                  [funcool/cljs-testrunners "0.1.0-SNAPSHOT"]]
                   :plugins [[lein-cljsbuild "1.0.5"]
                             [lein-externs "0.1.3"]
                             [lein-asciidoctor "0.1.14"]]
                   :asciidoctor {:sources "doc/*.adoc"
                                 :to-dir "target/doc"}}}

  :test-paths ["test"]
  :source-paths ["src" "test"]
  :resource-paths ["assets"]

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
                           :main tubax.testrunner
                           :optimizations :none
                           :target :nodejs
                           :pretty-print true }}]})
