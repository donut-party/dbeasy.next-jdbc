(ns donut.dbeasy-test
  (:require
   [clojure.test :refer [deftest is]]
   [donut.dbeasy :as dbe]))


(defprotocol ExampleProtocol
  (foo [_]))

(dbe/def-protocol-template
  ExampleProtocol
  :test
  (foo [_] "hi"))

(deftest implements-template
  (dbe/implement-protocol-template
   ExampleProtocol
   java.util.Date
   :test)
  (is (= "hi" (foo (java.util.Date.)))))

(deftest implements-template-with-overrides
  (dbe/implement-protocol-template
   ExampleProtocol
   java.util.Date
   :test
   {"hi" "hello"})
  (is (= "hello" (foo (java.util.Date.)))))
