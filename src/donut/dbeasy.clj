(ns donut.dbeasy
  (:require
   [clojure.walk :as walk]))

(defn ns-expand-symbol
  [sym]
  (if (symbol? sym)
    (let [sns (some-> sym namespace symbol)]
      (if-let [aliased (get (ns-aliases *ns*) sns)]
        (-> aliased .getName str (symbol (name sym)))
        sym))
    sym))

(defn ns-expand-symbols
  [form]
  (walk/postwalk ns-expand-symbol form))

;; TODO check that methods given actually exist on protocol
(defmacro def-protocol-template
  [protocol template-name & methods]
  `(alter-var-root (:var ~protocol)
                   assoc-in
                   [::templates ~template-name]
                   (quote ~(ns-expand-symbols methods))))

(defn- protocol-methods
  [protocol-name template-name term-mapping]
  (walk/postwalk (fn [x]
                   (if-let [substitution (and term-mapping (term-mapping x))]
                     substitution
                     x))
                 (get-in (deref (resolve protocol-name)) [::templates template-name])))

(defmacro implement-protocol-template
  [protocol-name type template-name & [term-mapping]]
  `(extend-type ~type
     ~protocol-name
     ~@(protocol-methods protocol-name template-name term-mapping)))
