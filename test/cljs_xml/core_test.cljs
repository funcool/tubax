(ns cljs-xml.core-test
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [cemerick.cljs.test :refer (is deftest done)])
  (:require [cljs.core.async :as async :refer [<!]]
            [cljs-xml.core :as core]))

(deftest ^:async async-addition
  (go
    (let [result (<! (core/async-addition 2 2))]
      (is (= 4 result))
      (done))))
