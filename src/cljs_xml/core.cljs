(ns cljs-xml.core
  (:require [cljs.core.async :as async :refer [put! chan]]
            [js.sax :as sax]
            [clojure.zip :as zip]
            [clojure.string :as str]))

(defn- add-node-document [node document]
  (let [keytag (keyword (.-name node))
        att-map (js->clj (.-attributes node) :keywordize-keys true)
        node-value [keytag att-map nil]]
    (-> document
        (zip/append-child (vector keytag att-map []))
        zip/down
        zip/rightmost
        zip/down
        zip/rightmost)))

(defn- close-node-document [node document]
  (-> document zip/up zip/up))

(defn- add-text [text document]
  (let [trimmed (str/trim text)]
    (if (not (empty? trimmed))
      (zip/append-child document trimmed)
      document)))

(defn xml->clj [source]
  (let [ret-chan (chan)
        parser (.parser js/sax true)
        document (atom (zip/vector-zip []))]
    ;; OPEN TAG
    (set! (.-onopentag parser)
          #(swap! document (partial add-node-document %)))

    ;; CLOSE TAG
    (set! (.-onclosetag parser)
          #(swap! document (partial close-node-document %)))

    ;; GET TEXT
    (set! (.-ontext parser)
          #(swap! document (partial add-text %)))

    ;; END PARSING
    (set! (.-onend parser)
          #(put! ret-chan (first (zip/root @document))))

    (set! (.-onerror parser)
          #(put! ret-chan {:error (str error)}))

    (.write parser source)
    (.close parser)
    ret-chan))
