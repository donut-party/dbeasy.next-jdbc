(ns donut.dbeasy.next-jdbc
  (:require
   [honey.sql :as sql]
   [next.jdbc :as jdbc]
   [next.jdbc.sql :as jsql]
   [clojure.string :as str]))

(defn- database-product-name
  [db]
  (-> (if (isa? (class db) java.sql.Connection)
        db
        (jdbc/get-connection db))
      .getMetaData
      .getDatabaseProductName
      str/lower-case
      keyword))

(defn query-one
  [db q]
  (->> q
       sql/format
       (jsql/query db)
       first))

(defmulti get-inserted-record
  (fn [db _insert-result _table-name]
    (database-product-name db)))

(defmethod get-inserted-record
  :sqlite
  [db insert-result table-name]
  (query-one db {:select [:*]
                 :from [table-name]
                 :where [:= :rowid (first (vals insert-result))]}))

(defmethod get-inserted-record
  :default
  [_db insert-result _table-name]
  insert-result)

(defn insert!
  [db table record]
  (get-inserted-record db (jsql/insert! db table record)))
