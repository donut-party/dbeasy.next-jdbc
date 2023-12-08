(ns donut.dbeasy-test
  (:require
   [clojure.test :refer [deftest is]]
   [donut.dbeasy :as dbe]
   [donut.ns-protocol :as nsp]))


(defprotocol ExampleProtocol
  (foo [_]))

(dbe/def-protocol-template
  ExampleProtocol
  :test
  (foo [_] "hi"))

(deftest implements-template
  (dbe/extend-type-with-protocol-template
   java.util.Date
   ExampleProtocol
   :test)
  (is (= "hi" (foo (java.util.Date.)))))

(deftest implements-template-with-overrides
  (dbe/extend-type-with-protocol-template
   java.util.Date
   ExampleProtocol
   :test
   {"hi" "hello"})
  (is (= "hello" (foo (java.util.Date.)))))


(deftest implements-template-with-ns-alias
  (dbe/extend-type-with-protocol-template
   nsp/NSProtocol
   java.util.Date
   :test)
  (is (= #{:b :a :clojure.set/foo} (nsp/foo (java.util.Date.)))))
