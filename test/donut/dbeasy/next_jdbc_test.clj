(ns donut.dbeasy.next-jdbc-test
  (:require
   [clojure.test :refer [deftest is]]
   [donut.dbeasy.next-jdbc :as sut]))

(deftest test->sql
  (is (= ["SELECT * FROM foo"]
         (sut/->sql {:select :*
                     :from   :foo})))

  (is (= ["SELECT x FROM y"]
         (sut/->sql ["SELECT x FROM y"])))

  (is (= "SELECT x FROM y"
         (sut/->sql "SELECT x FROM y"))))
