(ns cljs-xml.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [>! <! timeout chan]]))

(defn async-addition [x y]
  (let [c (chan)]
    (go
      (<! (timeout 1000))
      (.log js/console ">> Aftertimeout")
      (>! c (+ x y)))
    c))
