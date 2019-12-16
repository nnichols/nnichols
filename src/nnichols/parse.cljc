(ns nnichols.parse
  "A bunch of utility functions for parsing strings to the types they represent")

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
