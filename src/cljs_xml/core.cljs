(ns cljs-xml.core
  ;(:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [put! chan]]
            [js.sax :as sax]
            [clojure.zip :as zip]
            [clojure.string :as str]))

(def tag-idx 0)
(def att-idx 1)
(def val-idx 2)

(defn xml->clj [source]
  (let [ret-chan (chan)
        parser (.parser js/sax true)
        document (atom (zip/vector-zip []))]
    ;; OPEN TAG
    (set! (.-onopentag parser)
          (fn [node]
            ;(println (str "Open >> " (.-name node)) )
            (let [keytag (keyword (.-name node))
                  att-map (js->clj (.-attributes node) :keywordize-keys true)
                  node-value [keytag att-map nil]]
              (swap! document
                     #(-> %
                          (zip/append-child (vector keytag att-map []))
                          zip/down
                          zip/rightmost
                          zip/down
                          zip/rightmost)))))

    ;; CLOSE TAG
    (set! (.-onclosetag parser)
          (fn [node]
            (swap! document #(-> % zip/up zip/up))))

    ;; GET TEXT
    (set! (.-ontext parser)
          (fn [text]
            (let [trimmed (str/trim text)]
              (when (not (empty? trimmed))
                (swap! document zip/append-child trimmed)))))

    ;; END PARSING
    (set! (.-onend parser)
          (fn []
            (put! ret-chan (first (zip/root @document)))))

    (set! (.-onerror parser)
          (fn [error]
            (put! ret-chan {:error (str error)})))

    (.write parser source)
    (.close parser)
    ret-chan))
