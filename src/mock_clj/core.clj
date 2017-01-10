(ns mock-clj.core)

"""
------------------------------------
  Internal functions - ignore these
"""

(defmacro make-mock 
  ([m] `(make-mock ~m nil))
  ([m stub] 
   `(with-meta
      (fn [& ~'args] 
        (swap! (-> ~m meta :args) conj ~'args)
        ; If stub is a function, execute it
        (if (fn? ~stub) 
          (apply ~stub ~'args)
          ~stub))
      {:args (atom [])})))

(defn gen-redefs [[m stub & spec]]
  (into 
    [m `(make-mock ~m ~stub)]
    (when spec
      (gen-redefs spec))))

"""
------------------------------------
  APIs
"""

(defn calls [m] @(-> m meta :args))

(defn last-call [m] (last (calls m)))

(defn called? [m] (not-empty (calls m)))

(defn call-count [m] (-> m calls count))

(defn reset-calls! [m] (reset! (:args (meta m)) []))

(defmacro with-mock [specs & body] 
  `(with-redefs ~(gen-redefs specs)
     (do ~@body)))
