(ns tubax.helpers)

;; Datastructure access
(defn is-node
  "Checks if the parameter matchs the tubax node contract"
  [node]
  (and (vector? node)
       (= (count node) 3)
       (keyword? (first node))
       (map? (second node))
       (vector? (nth node 2))))

(defn get-tag [[tag _ _]] tag)
(defn get-attributes [[_ attributes _]] attributes)
(defn get-children [[_ _ children]] children)

(defn get-text [node]
  (let [[value & _] (get-children node)]
    (if (string? value) value nil)))

(defn seq-tree [tree]
  (tree-seq is-node get-children tree))

(defn filter-tree [search-fn tree]
  (->> tree seq-tree (filter search-fn)))

(defn first-tree [search-fn tree]
  (->> tree (filter-tree search-fn) first))

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

(defn find-all-by-path [path node]
  (cond
    (empty? path) '()
    (and (= (count path) 1) (= (get-tag node) (first path))) (list node)
    (and (= (count path) 1)) '()
    :else
    (if (= (get-tag node) (first path))
      (apply concat (map (partial find-all-by-path (rest path)) (get-children node)))
      '())))

;; Dispatcher function for both 'find-first' an 'find-all'
(defn- find-multi-dispatcher [_ param]
  (let [key (-> param keys first)]
    (cond
      (and (= key :attribute)
           (keyword? (get param key)))
      [:attribute :keyword]
      (and (= key :attribute)
           (vector? (get param key)))
      [:attribute :vector]
      :else key
      )))

;; Find first
(defmulti find-first find-multi-dispatcher)

(defmethod find-first :tag [tree {:keys [tag]}]
  (first-tree #(= (get-tag %) tag) tree))

(defmethod find-first [:attribute :keyword] [tree {:keys [attribute]}]
  (first-tree #(contains? (get-attributes %) attribute) tree))

(defmethod find-first [:attribute :vector] [tree {:keys [attribute]}]
  (let [[key value] attribute]
    (first-tree #(and (contains? (get-attributes %) key)
                      (= (get (get-attributes %) key) value)) tree)))

(defmethod find-first :path [tree {:keys [path]}]
  (if (and (not (empty? path)) (= (first path) (first tree)))
    (find-first-by-path (rest path) tree)
    nil))

;; Find all
(defmulti find-all find-multi-dispatcher)

(defmethod find-all :tag [tree {:keys [tag]}]
  (filter-tree #(= (get-tag %) tag) tree))

(defmethod find-all [:attribute :keyword] [tree {:keys [attribute]}]
  (filter-tree #(contains? (get-attributes %) attribute) tree))

(defmethod find-all [:attribute :vector] [tree {:keys [attribute]}]
  (let [[key value] attribute]
    (filter-tree #(and (contains? (get-attributes %) key)
                       (= (get (get-attributes %) key) value)) tree)))

(defmethod find-all :path [tree {:keys [path]}]
  (find-all-by-path path tree))
