(ns nnichols.parse
  "A bunch of utility functions for parsing strings to the types they represent"
    (:require [clojure.string :as cs]
              #?(:cljs [goog.string])
              [nnichols.predicate :as np]
              [nnichols.util :as nu])
  #?(:clj (:import (java.util UUID)
                   (java.lang Double))))

(defn parse-radix
  "Convert `int-str` to the positive integer it represents w.r.t. the `radix`
   e.g. (parse-radix \"10010\" 2) would return 18"
  [int-str radix]
  (cond
    (int? int-str) int-str
    (string? int-str) #? (:clj  (Integer/parseInt int-str radix)
                          :cljs (let [result (js/parseInt int-str radix)]
                                  (if (js/isNaN result)
                                    (throw (js/Error. "Argument is not a number"))
                                    result)))
    :else (throw (ex-info "Can't parse input" {:parse-target int-str :radix radix}))))

(defn parse-binary
  "Parse `binary-str` to the positive integer it represents in binary"
  [binary-str]
  (parse-radix binary-str 2))

(defn parse-octal
  "Parse `octal-str` to the positive integer it represents in octal"
  [octal-str]
  (parse-radix octal-str 8))

(defn parse-int
  "Parses `dec-str` to the positive integer it represents"
  [dec-str]
  (cond
    (int? dec-str) dec-str
    (string? dec-str) #?(:clj  (Integer/parseInt dec-str)
                         :cljs (let [result (goog.string/parseInt dec-str)]
                                 (if (js/isNaN result)
                                   (throw (js/Error. "Argument is not a number"))
                                   result)))
    :else (throw (ex-info "Can't parse into an integer" {:argument dec-str}))))

(defn parse-hex
  "Parse `hex-str` to the positive integer it represents in hexidecimal"
  [hex-str]
  (parse-radix hex-str 16))

(defn parse-uuid
  "Parse `uuid-str` to the UUID it represents"
  [uuid-str]
  #?(:clj  (UUID/fromString uuid-str)
     :cljs (cljs.core/uuid uuid-str)))

(defn parse-guid
  "Parse `guid-str` to the GUID it represents"
  [guid-str]
  (parse-uuid guid-str))

(defn try-parse-uuid
  "Like `parse-uuid`, but returns `u` if it's already a UUID, or nil for invalid inputs."
  [u]
  (cond
    (nil? u) nil
    (np/uuid? u) u
    (string? u) (nu/try-or-nil parse-uuid u)))

(defn try-parse-guid
  "Like `parse-guid`, but returns `u` if it's already a GUID, or nil for invalid inputs."
  [u]
  (try-parse-uuid u))

(def try-parse-radix
  "Like `parse-radix`, but returns nil for invalid input."
  (partial nu/try-or-nil parse-radix))

(def try-parse-binary
  "Like `parse-binary`, but returns nil for invalid input."
  (partial nu/try-or-nil parse-binary))

(def try-parse-octal
  "Like `parse-octal`, but returns nil for invalid input."
  (partial nu/try-or-nil parse-octal))

(def try-parse-int
  "Like `parse-int`, but returns nil for invalid input."
  (partial nu/try-or-nil parse-int))

(def try-parse-hex
  "Like `parse-hex`, but returns nil for invalid input."
  (partial nu/try-or-nil parse-hex))

(defn parse-radix-or-zero
  "Like `try-parse-radix`, but returns a 0 instead of nil.
   Useful when passing return values into numeric operations."
  [int-str radix]
  (or (try-parse-radix int-str radix) 0))

(defn parse-binary-or-zero
  "Like `try-parse-binary`, but returns a 0 instead of nil.
   Useful when passing return values into numeric operations."
  [binary-str]
  (or (try-parse-binary binary-str) 0))

(defn parse-octal-or-zero
  "Like `try-parse-octal`, but returns a 0 instead of nil.
   Useful when passing return values into numeric operations."
  [octal-str]
  (or (try-parse-octal octal-str) 0))

(defn parse-int-or-zero
  "Like `try-parse-int`, but returns a 0 instead of nil.
   Useful when passing return values into numeric operations."
  [int-str]
  (or (try-parse-int int-str) 0))

(defn parse-hex-or-zero
  "Like `try-parse-hex`, but returns a 0 instead of nil.
   Useful when passing return values into numeric operations."
  [hex-str]
  (or (try-parse-hex hex-str) 0))

(def ^:const float-regex
  #"^[-+]?[0-9]*\.?[0-9]*([eE][-+]?[0-9]+)?$")

(defn float-string?
  "Returns true if `float-string` representss a valid float, false otherwise."
  [float-string]
  (boolean (and (re-matches float-regex float-string)
                (not= "." (cs/trim float-string)))))

(defn parse-float
  "Parse `float-string` to the floating point decimal it represents"
  [float-string]
  #?(:clj (Float. float-string)
     :cljs (js/parseFloat float-string)))

(defn try-parse-float
  "Like `parse-float`, but returns float-string for floats, and nil for invalid input"
  [float-string]
  (let [float-string? (and (string? float-string)
                           (float-string? float-string))]
    (cond
      (nil? float-string) nil
      (int? float-string) (float float-string)
      (float? float-string) float-string
      float-string? (parse-float float-string)
      :else nil)))

(defn parse-float-or-zero
  "Like `try-parse-float`, but returns 0.0 instead of nil.
   Useful when passing return values into numeric operations."
  [float-string]
  (or (try-parse-float float-string) 0.0))

;; Javascript has no native Double class.
#?(:clj (defn parse-double
          "Parse `double-string` to an instance of the JVM's Double class"
          [double-string]
          (Double/parseDouble double-string)))

#?(:clj (defn try-parse-double
          "Like `parse-double`, but returns double-string for doubles, and nil for invalid input"
          [double-string]
          (cond
            (nil? double-string) nil
            (double? double-string) double-string
            (string? double-string) (nu/try-or-nil parse-double double-string))))

#?(:clj (defn parse-double-or-zero
          "Like `try-parse-double`, but returns 0.0 instead of nil.
           Useful when passing return values into numeric operations."
          [double-string]
          (or (try-parse-double double-string) (double 0))))
