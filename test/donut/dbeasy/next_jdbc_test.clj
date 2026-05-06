(ns donut.dbeasy.next-jdbc-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [donut.dbeasy.next-jdbc :as sut]
   [next.jdbc :as jdbc]))

(def ^:dynamic *dbconn* nil)

(defn execute-many!
  [conn stmts]
  (doseq [stmt stmts]
    (jdbc/execute! conn [stmt])))

(defn create-tables
  [conn create-table-statements]
  (doseq [table-name ["child_records" "parent_records" "unconnected_records"]]
    (try
      (jdbc/execute! conn [(str "DROP TABLE " table-name)])
      (catch Exception _)))
  (execute-many! conn create-table-statements))

(defmacro with-test-db
  [test-db & body]
  `(let [test-db-def# ~test-db
         test-db#     test-db-def#]
     (with-open [test-db# (jdbc/get-connection test-db#)]
       (binding [*dbconn* test-db#]
         (create-tables test-db# (:create-tables test-db-def#))
         (testing (str "db: " test-db#)
           ~@body)))))

(def sqlite-create-tables
  [(str "CREATE TABLE records ("
        "  id           integer NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"
        "  varchar_ex   varchar(256) NOT NULL UNIQUE"
        ")")])

(def ^:private sqlite-mem-config
  {:dbtype         "sqlite"
   :connection-uri "jdbc:sqlite::memory:"
   :create-tables  sqlite-create-tables})

(deftest test->sql
  (is (= ["SELECT * FROM foo"]
         (sut/->sql {:select :*
                     :from   :foo})))

  (is (= ["SELECT x FROM y"]
         (sut/->sql ["SELECT x FROM y"])))

  (is (= "SELECT x FROM y"
         (sut/->sql "SELECT x FROM y"))))

(deftest test-database-product-name
  (is (= :sqlite
         (sut/database-product-name sqlite-mem-config))))

(deftest test-insert!
  (with-test-db sqlite-mem-config
    (is (= #:records{:id 1, :varchar_ex "foo"}
           (sut/insert! *dbconn* :records {:varchar_ex "foo"})))))
