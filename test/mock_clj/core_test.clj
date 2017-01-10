(ns mock-clj.core-test
  (:require [clojure.test :refer :all]
            [mock-clj.core :refer :all]))

(defn foo [a & rst] 
  (str "foo" a))

(defn bar [a]
  (str (foo a) a))

(deftest with-mock-basic
  (with-mock [type 1]
    (is (type "s") 1)
    (is (coll? (calls type))))
  (is (= java.lang.String (type "s"))))

(deftest with-mock-dynamic
  (with-mock [foo "y"]
    (is (= (bar "x")
           "yx")))
  (is (= (bar "x")
         "fooxx"))) 

(deftest with-mock-fn 
  (with-mock [foo #(str "bar" %)]
    (is (= (foo "a") "bara"))))

(deftest with-multiple-mock
  (with-mock [type "SOME"
              foo 3]
    (is (= (foo 4) 3))
    (is (= (type 4) "SOME"))))

(deftest calls-test
  (with-mock [foo 5]
    (foo 1 2 3)
    (foo 5 6 6)
    (is (calls foo) [[1 2 3] [5 6 6]])))

(deftest last-call-test
  (with-mock [foo "ok"]
    (is (nil? (last-call foo)))
    (foo 1 2 3)
    (is (= (last-call foo) [1 2 3]))
    (foo "bar")
    (is (= (last-call foo) ["bar"]))))

(deftest called-test
  (with-mock [foo "ok"]
    (is (not (called? foo)))
    (foo 1 2 3)
    (is (called? foo))
    (foo "bar")
    (is (called? foo))))

(deftest call-count-test
  (with-mock [foo (constantly "ok")]
    (is (= (foo 1) "ok"))
    (is (= 1 (call-count foo)))
    (foo "aaa")
    (is (= 2 (call-count foo)))))

(deftest reset-call-test 
  (with-mock [foo (constantly "ok")]
    (foo)
    (is (called? foo))
    (reset-calls! foo)
    (is (not (called? foo)))))
