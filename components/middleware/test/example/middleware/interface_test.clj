(ns example.middleware.interface-test
  (:require [clojure.test :as test :refer :all]
            [example.middleware.interface :as middleware]))

(deftest dummy-test
  (is (= 1 1)))
