(ns mock-clj.core)

"""
------------------------------------
  Internal functions - ignore these
"""

; Check if input is like '(var x) or #'x
(defn- var-symbol? [v] 
  (and
    (seq? v)
    (= 2 (count v))
    (= (first v) 'var)))

(defn- try-strip-var [v]
  (if (var-symbol? v)
    (second v)
    v))

(defmacro make-mock 
  ([] `(make-mock nil))
  ([stub] 
   `(let [~'state (atom [])]
     (with-meta
       (fn [& ~'args] 
         (swap! ~'state conj ~'args)
         ; If stub is a function, execute it
         (if (fn? ~stub) 
           (apply ~stub ~'args)
           ~stub))
       {:args ~'state}))))

(defn- gen-redefs [[m stub & spec]]
  (let [sm (try-strip-var m)]
   (into 
     [sm `(make-mock ~stub)]
     (when spec
       (gen-redefs spec)))))

(defn- if-var->obj [m]
  (if (var? m)
    (deref m)
    m))

"""
------------------------------------
  APIs
"""

(defn calls [m] @(-> m if-var->obj meta :args))

(defn last-call [m] (last (calls m)))

(defn called? [m] (not-empty (calls m)))

(defn call-count [m] (-> m calls count))

(defn reset-calls! [m] (reset! (:args (meta m)) []))

(defmacro with-mock [specs & body] 
  `(with-redefs ~(gen-redefs specs)
     (do ~@body)))
