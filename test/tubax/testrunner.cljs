(ns tubax.testrunner
  (:require [cljs.test :as test]
            [tubax.core-test]
            [tubax.helpers-test]))

(enable-console-print!)

(defn main
  []
  (println ">> Running tests")
  (test/run-tests
   (test/empty-env)
   'tubax.core-test
   'tubax.helpers-test))

(defmethod test/report [:cljs.test/default :end-run-tests]
  [m]
  (if (test/successful? m)
    (set! (.-exitCode js/process) 0)
    (set! (.-exitCode js/process) 1)))

(set! *main-cli-fn* main)
