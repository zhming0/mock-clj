# mock-clj

[![Build Status](https://travis-ci.org/zhming0/mock-clj.svg?branch=master)](https://travis-ci.org/zhming0/mock-clj)
[![Clojars Project](https://img.shields.io/clojars/v/mock-clj.svg)](https://clojars.org/mock-clj)

Minimalist & non-invasive API for mocking in Clojure. 
It was accidently written when I was testing my code.
Basically, it is a syntax sugar around `with-redefs`. 

## Usage

```clojure
(with-mock specs & body)
```

`specs => var-symbol value-expr/functions`

Temporarily redefines `var` while executing `body`. 
If the right-hand side of a spec is a value, then it equals to create a function constantly return the value (stub).
If the right-hand side of a spec is a function, then the var-symbol will temporaily be replaced by the function directly.

### Example 

```clojure

(require ['mock-clj.core :as 'mc])

(defn foo [a] (str "foo" a))

(defn bar [a] (str (foo a) "bar"))

(deftest test-bar
  (mc/with-mock [foo "oof"] ; equals to [foo (constantly "oof")] 
    (is (= (bar "test") "oofbar"))
    ; Calls history
    (is (= (mc/calls foo) [["test"]]))
    ; Last-call
    (is (= (mc/last-call foo) ["test"]))
    ; call-count
    (is (= 1 (mc/call-count foo)))
    ; Called? 
    (is (mc/called? foo))
    ; Reset calls history
    (mc/reset-calls! foo)))

```

## License

Copyright © 2017 Ming

Distributed under the Eclipse Public License.
