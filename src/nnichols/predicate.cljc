(ns nnichols.predicate
  "A bunch of utility predicates"
  (:refer-clojure :exclude [boolean? uuid?])
  #?(:cljs (:require [cljs-time.core :as time]))
  #?(:clj (:import (org.joda.time DateTime))))

(defn boolean?
  "Returns true iff `x` is a boolean.
   This is an important safety check for cljs"
  [x]
  #?(:clj  (instance? Boolean x)
     :cljs (or (true? x)
               (false? x))))

#_:clj-kondo/ignore
(defn uuid?
  "Returns true iff `x` is a UUID."
  [x]
  (instance? #?(:clj java.util.UUID
                :cljs cljs.core.UUID) x))

(def guid?
  "Returns true iff `x` is a GUID."
  uuid?)

(def date?
  "Returns true iff 'x' is a date"
  #?(:clj  #(instance? DateTime %))
  #?(:cljs cljs-time.core/date?))
