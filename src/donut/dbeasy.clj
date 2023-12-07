(ns donut.dbeasy
  (:require
   [clojure.walk :as walk]))

;; TODO check that methods given actually exist on protocol
(defmacro def-protocol-template
  [protocol template-name & methods]
  `(alter-var-root (:var ~protocol) assoc-in [::templates ~template-name] (quote ~methods)))

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
