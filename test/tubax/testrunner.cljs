(ns tubax.testrunner
  (:require [cljs-testrunners.node :as node]
            [tubax.core-test]
            [tubax.helpers-test]))

(defn main []
  (node/run-tests 'tubax.core-test
                  'tubax.helpers-test))

(set! *main-cli-fn* main)
