(require '[codox.main :as codox])

(codox/generate-docs
 {:output-path "doc/dist/latest"
  :metadata {:doc/format :markdown}
  :language :clojurescript
  :name "funcool/tubax"
  :themes [:rdash]
  :source-paths ["src"]
  :namespaces [#"^tubax\."]
  :source-uri "https://github.com/funcool/tubax/blob/master/{filepath}#L{line}"})
