(ns donut.ns-protocol
  (:require
   [clojure.set :as set]
   [donut.dbeasy :as dbe]))

(defprotocol NSProtocol
  (foo [_]))

(dbe/def-protocol-template
  NSProtocol
  :test
  (foo [_] (set/union #{:a} #{:b})))
