(ns dbeasy.core)

(defprotocol DBEasy
  (database-product-name [db])
  (query-one [db q]))
