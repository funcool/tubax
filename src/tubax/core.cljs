(ns tubax.core
  (:require [ext.saxjs :as sax]
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
  (if (not (empty? text))
    (zip/append-child document text)
    document))

(defn xml->clj [source & {:keys [strict trim normalize
                                lowercase xmlns position
                                strict-entities]
                          :or {strict true
                               trim true
                               normalize false
                               lowercase true
                               position true
                               strict-entities false}}]
  (let [parser (.parser js/sax strict #js {"trim" trim
                                           "normalize" normalize
                                           "lowercase" lowercase
                                           "xmlns" xmlns
                                           "position" position
                                           "strictEntities" strict-entities})
        document (atom (zip/vector-zip []))
        result (atom nil)]
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
          #(when (nil? @result)
            (reset! result {:success (first (zip/root @document))})))

    ;; ERROR
    (set! (.-onerror parser)
          #(reset! result {:error (str %)}))

    (.write parser source)
    (.close parser)

    (or (:success @result)
        (throw (js/Error. (:error @result))))))
