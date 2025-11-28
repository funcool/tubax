(ns tubax.tests.main
  (:require
   [cljs.test :as t]
   [tubax.tests.core-test]
   [tubax.tests.helpers-test]))

(enable-console-print!)

(defmethod t/report [:cljs.test/default :end-run-tests]
  [m]
  (if (t/successful? m)
    (set! (.-exitCode js/process) 0)
    (set! (.-exitCode js/process) 1)))

(defn init
  []
  (t/run-tests
   'tubax.tests.core-test
   'tubax.tests.helpers-test))
