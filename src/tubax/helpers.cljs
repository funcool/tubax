(ns tubax.helpers)

;; Datastructure access
(defn get-tag [[tag _ _]] tag)
(defn get-attributes [[_ attributes _]] attributes)
(defn get-children [[_ _ children]] children)

(defn get-text [node]
  (let [[value & _] (get-children node)]
    (if (string? value) value nil)))

;; Find first
; (find-first result {:tag :item})
; (find-first result {:path [:rss :channel :description]})
; (find-first result {:attribute :isPermaLink})
; (find-first result {:attribute [:isPermaLink true]})

(defn search-first [search-fn node]
  (cond
    (string? node) nil
    (search-fn node) node
    :else
    (if (empty? (get-children node))
      nil
      (some identity (map (partial search-first search-fn)
                          (get-children node))))))

(defn find-first-by-path [path-left node]
  (cond
    (empty? path-left) node
    (nil? node) nil
    (string? node) nil
    :else
    (let [subtree (some #(if (= (get-tag %) (first path-left)) % nil)
                        (get-children node))]
      (recur (rest path-left)
             subtree))))

(defn find-first [tree {:keys [tag path attribute]}]
  (cond
    ;; Searching for tag
    tag
    (search-first #(= (get-tag %) tag) tree)

    ;; Searching for path
    (and path
         (not (empty? path))
         (= (first path) (first tree)))
    (find-first-by-path (rest path) tree)

    ;; Searching for attribute existence
    (keyword? attribute)
    (search-first #(contains? (get-attributes %) attribute) tree)

    ;; Searching for attribute equality
    (and (vector? attribute) (= (count attribute) 2))
    (search-first #(and (contains? (get-attributes %) (first attribute))
                        (= (get (get-attributes %) (first attribute)) (second attribute))) tree)

    ;; Not valid
    :else nil))


;; Find all
; (find-all result {:tag :link})
; (find-all result {:path [:rss :channel :item :title]})
; (find-all result {:attribute :isPermaLink})
; (find-all result {:attribute [:isPermaLink "true"]})
