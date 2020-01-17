(ns nnichols.spec
  "A bunch of spec definitions with conformers"
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.test.check.generators] ;; required to work with clojurescript
            [nnichols.parse :as np]))

;; Specs that can coerce data using nnichols.parse
(s/def ::n-boolean
  (s/with-gen
   (s/conformer
    #(try (np/parse-boolean-strict %)
          (catch #? (:clj Exception :cljs :default) _ ::s/invalid)))
   sgen/boolean))

(s/def ::n-uuid
  (s/with-gen
   (s/conformer
    #(let [parsed-uuid (np/try-parse-uuid %)]
       (if parsed-uuid parsed-uuid ::s/invalid)))
   sgen/uuid))

(s/def ::n-integer
  (s/with-gen
   (s/conformer
    #(try (np/parse-int %)
          (catch #? (:clj Exception :cljs :default) _ ::s/invalid)))
   sgen/int))

(s/def ::n-keyword
  (s/with-gen
   (s/conformer
    #(cond
       (keyword? %) %
       (string? %) (keyword %)
       :else ::s/invalid))
   sgen/keyword))
