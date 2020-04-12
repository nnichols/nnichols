(ns nnichols.string
  "A bunch of utility functions for strings"
  (:require [clojure.string :as cs]))

(defn prepare-for-compare
  "Takes a string `s` trims it and coerces it to lower case.
   Since the mapping between upper and lower case characters isn't perfect for every language,
   an option flag `:upper-case?` may be passed to coerce in the other direction.
   To cast non-strings to strings, the `coerce?` flag may also be passed."
  ([s]
   (prepare-for-compare s {}))
  
  ([s {:keys [upper-case? coerce?]}]
   (let [casing-fn (if upper-case? cs/upper-case cs/lower-case)
         s1 (if coerce? (str s) s)]
     (-> s1 cs/trim casing-fn))))

(defn string-compare
  "Compares `s1` and `s2` as lower-case, trimmed strings.
   Since the mapping between upper and lower case characters isn't perfect for every language,
   an option flag `:upper-case?` may be passed to coerce in the other direction.
   To cast non-strings to strings, the `coerce? `flag may also be passed."
  ([s1 s2]
   (string-compare s1 s2 {}))
  
  ([s1 s2 opts]
   (= (prepare-for-compare s1 opts) (prepare-for-compare s2 opts))))

(defn string-includes?
  "Checks to see if `s1` contains `s2` as lower-case, trimmed strings.
   Since the mapping between upper and lower case characters isn't perfect for every language,
   an option flag `:upper-case?` may be passed to coerce in the other direction.
   To cast non-strings to strings, the `coerce? `flag may also be passed."
  ([s1 s2]
   (string-includes? s1 s2 {}))
  
  ([s1 s2 opts]
   (cs/includes? (prepare-for-compare s1 opts) (prepare-for-compare s2 opts))))
