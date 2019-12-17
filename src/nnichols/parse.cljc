(ns nnichols.parse
  "A bunch of utility functions for parsing strings to the types they represent"
    (:require [nnichols.util :as nu])
  #?(:clj (:import (java.util UUID))))

(defn parse-radix
  "Convert `int-str` to the positive integer it represents w.r.t. the `radix`
   e.g. (parse-radix \"10010\" 2) would return 18"
  [int-str radix]
  #? (:clj  (Integer/parseInt int-str radix)
      :cljs (js/parseInt int-str radix)))

(defn parse-binary
  "Parse `binary-str` to the positive integer it represents in binary"
  [binary-str]
  (parse-radix binary-str 2))

(defn parse-octal
  "Parse `octal-str` to the positive integer it represents in octal"
  [octal-str]
  (parse-radix octal-str 8))

(defn parse-hex
  "Parse `hex-str` to the positive integer it represents in hexidecimal"
  [hex-str]
  (parse-radix hex-str 16))

(defn parse-uuid
  "Returns a UUID generated from the supplied string."
  [s]
  #?(:clj  (UUID/fromString s)
     :cljs (cljs.core/uuid s)))

(defn parse-guid
  "Returns a GUID generated from the supplied string."
  [s]
  (parse-uuid s))

(defn try-parse-uuid
  "Like parse-uuid, but returns u if it's already a UUID, or nil for invalid inputs."
  [u]
  (cond
    (nil? u)    nil
    (uuid? u)   u
    (string? u) (nu/try-or-nil parse-uuid u)))

(defn try-parse-guid
  "Like parse-guid, but returns u if it's already a GUID, or nil for invalid inputs."
  [u]
  (try-parse-uuid u))
