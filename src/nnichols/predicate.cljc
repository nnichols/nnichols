(ns nnichols.predicate
  "A bunch of utility predicates"
  (:refer-clojure :exclude [boolean? uuid?]))

(defn boolean?
  "Returns true iff x is a boolean.
   This is an important safety check for cljs"
  [x]
  #?(:clj  (instance? Boolean x)
     :cljs (or (true? x)
               (false? x))))

(defn uuid?
  "Returns true iff the value is a UUID."
  [x]
  (instance? #?(:clj java.util.UUID
                :cljs cljs.core.UUID) x))

(def guid?
  "Returns true iff the value is a GUID."
  uuid?)
