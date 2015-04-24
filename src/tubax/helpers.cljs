(ns tubax.helpers)

;; Datastructure access
(defn tag [[tag _ _]] tag)
(defn attributes [[_ attributes _]] attributes)
(defn children [[_ _ children]] children)

(defn text [node]
  (let [[value & _] (children node)]
    (if (string? value) value nil)))

;; Find first
; (find-first result {:tag :item})
; (find-first result {:path [:rss :channel :description]})
; (find-first result {:attribute :isPermaLink})
; (find-first result {:attribute [:isPermaLink true]})

;; Find all
; (find-all result {:tag :link})
; (find-all result {:path [:rss :channel :item :title]})
; (find-all result {:attribute :isPermaLink})
; (find-all result {:attribute [:isPermaLink "true"]})
