(ns cljs-xml.core
  ;(:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [put! chan]]
            [js.sax :as sax]))

(defn xml->clj [source]
  (let [ret-chan (chan)
        parser (.parser js/sax true)
        result (atom {})
        current-path (atom [])]
    (set! (.-onerror parser) (fn [error]
                               ;(print (str "error " error))
                               (put! ret-chan {:error (str error)})))
    (set! (.-ontext parser) (fn [text]
                              (let [old-value (get-in @result @current-path)]
                                (if (nil? old-value)
                                  (swap! result #(assoc-in % @current-path text))
                                  (swap! result #(assoc-in % @current-path [old-value text]))))))
    (set! (.-onopentag parser) (fn [node]
                                 ;(print (str "onopentag " (js->clj (.-attributes node))))
                                 (let [attributes (js->clj (.-attributes node))
                                       keytag (keyword (.-name node))
                                       reduce-fn (fn [acc val] (assoc acc (keyword (first val)) (second val)))
                                       att-map (reduce reduce-fn {} attributes)]
                                   (swap! result assoc keytag (if (empty? att-map) nil att-map))
                                   (swap! current-path conj keytag)
                                   ;(print (str "> " @current-path))
                                   )))
    (set! (.-onclosetag parser) (fn [node]
                                  (let [keytag (keyword node)]
                                    (swap! current-path pop keytag)
                                    ;(print (str "< " @current-path))
                                    )))
;     (set! (.-onattribute parser) (fn [attr]
;                                    (let [keyattr (keyword (.-name attr))
;                                          value (.-value attr)]
;                                         ;(print (str "onattribute " (js->clj attr)))
;                                      (print (str "assoccing " @current-path " " keyattr " " value))
;                                      (swap! result update-in @current-path assoc keyattr value))))
    (set! (.-onend parser) (fn []
                             (print (str @result))
                             (put! ret-chan @result)))
    (.write parser source)
    (.close parser)
    ret-chan
    ))
