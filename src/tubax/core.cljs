(ns tubax.core
  (:require tubax.saxjs))

(defn start-document []
  {:stack []
   :current nil})

(defn parse-node
  ([node]
   (parse-node node nil))

  ([node {:keys [keywordize-keys]
          :or   {keywordize-keys true}}]
   (let [tag   (cond-> (.-name node)
                 keywordize-keys (keyword))

         attrs (js->clj (.-attributes node) :keywordize-keys keywordize-keys)
         attrs (when-not (empty? attrs) attrs)]
     {:tag tag
      :attrs attrs
      :content nil})))

(defn push-node
  [{:keys [stack current] :as document} node]
  (let [new-current (parse-node node)
        new-stack (cond-> stack (some? current) (conj current))]
    (assoc document
           :stack new-stack
           :current new-current)))

(defn pop-node
  [{:keys [stack current] :as document} node]

  (let [tag (keyword node)]
    (if (empty? stack)
      document

      (let [new-stack   (pop stack)
            new-current (-> (peek stack)
                            (update :content (fnil conj []) current))]
        (assoc document
               :stack new-stack
               :current new-current)))))

(defn push-text
  [document text]
  (cond-> document
    (not (empty? text))
    (update-in [:current :content] (fnil conj []) text)))

(defn- create-parser [{:keys [strict trim normalize
                              lowercase xmlns position
                              strict-entities]
                       :or {strict true
                            trim true
                            normalize false
                            lowercase true
                            position true
                            strict-entities false}}]
  (.parser js/sax strict #js
           {"trim" trim
            "normalize" normalize
            "lowercase" lowercase
            "xmlns" xmlns
            "position" position
            "strictEntities" strict-entities}))

(defn xml->clj
  ([source] (xml->clj source {}))
  ([source options]
   (let [parser (create-parser options)
         document (atom (start-document))
         result (atom nil)]

     ;; OPEN TAG
     (set! (.-onopentag parser)
           #(swap! document push-node %))

     ;; CLOSE TAG
     (set! (.-onclosetag parser)
           #(swap! document pop-node %))

     ;; GET TEXT
     (set! (.-ontext parser)
           #(swap! document push-text %))

     ;; CDATA HANDLING
     (set! (.-oncdata parser)
           #(swap! document push-text %))

     ;; END PARSING
     (set! (.-onend parser)
           #(when-not (some? @result)
              (reset! result {:success (:current @document)})))

     ;; ERROR
     (set! (.-onerror parser)
           #(reset! result {:error (str %)}))

     (.write parser source)
     (.close parser)

     (or (:success @result)
         (throw (ex-info (str (:error @result)) {}))))))
