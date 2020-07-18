(ns nnichols.util
  "A bunch of utility functions"
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cskx])
  (:refer-clojure :exclude [uuid]))

;;
;; RUNTIME SAFETY
;;
(defn try-or-nil
  "Try applying f to args, returning nil in case of an exception."
  [f & args]
  (try
    (apply f args)
    (catch #? (:clj Exception :cljs :default) _
      nil)))

;;
;; FUNCTIONAL CONVENIENCE
;;
(defn rcomp
  "Right-compose a list of functions: like comp, but in the opposite direction."
  [& fns]
  (apply comp (reverse fns)))

(defn rpartial
  "Right-partial a list of functions: like partial, but in the opposite direction."
  [f & args]
  (fn [& inner-args]
    (apply f (concat inner-args args))))

;;
;; COLLECTION FUNCTIONS
;;
(defn only
  "Attempt to return the first and only element in `coll`.
   If the collection does not contain exactly one element, throw an exception"
  [coll]
  (if (seq (rest coll))
    (throw (ex-info "Collection does not contain exactly one element!" {}))
    (first coll)))

;;
;; MAP FUNCTIONS
;;
(def sort-keys
  "Return the sorted keys of a map"
  (comp sort keys))

(def sort-vals
  "Return the sorted vals of a map"
  (comp sort vals))

(defn rand-key
  "Safely return a random key of a map, giving nil for empty maps"
  [m]
  (try-or-nil (comp rand-nth keys) m))

(defn rand-val
  "Safely return a random val of a map, giving nil for empty maps"
  [m]
  (try-or-nil (comp rand-nth vals) m))

(defn rand-kv
  "Return a random key-val pair of a map as a map.
   OPINIONATED: Empty collections return empty maps."
  [m]
  (if (and (map? m) (not-empty m))
    (let [k (rand-key m)
          v (get m k)]
      {k v})
    {}))

(defn filter-by-values
  "Return `m` with only the key:value pairs whose values cause `f` to evaluate truthily"
  [f m]
  (let [reducing-fn (fn [m' k v] (if (f v) (assoc m' k v) m'))]
    (reduce-kv reducing-fn {} m)))

(defn filter-by-keys
  "Return `m` with only the key:value pairs whose keys cause `f` to evaluate truthily"
  [f m]
  (let [reducing-fn (fn [m' k v] (if (f k) (assoc m' k v) m'))]
    (reduce-kv reducing-fn {} m)))

(defn remove-by-values
  "Return `m` with only the key:value pairs whose values cause `f` to evaluate falsily"
  [f m]
  (let [reducing-fn (fn [m' k v] (if (f v) m' (assoc m' k v)))]
    (reduce-kv reducing-fn {} m)))

(defn remove-by-keys
  "Return `m` with only the key:value pairs whose keys cause `f` to evaluate falsily"
  [f m]
  (let [reducing-fn (fn [m' k v] (if (f k) m' (assoc m' k v)))]
    (reduce-kv reducing-fn {} m)))

(defn update-vals
  "Return `m` with `f` applied to each val in `m` with its `args`"
  [m f & args]
  (reduce-kv (fn [m' k v] (assoc m' k (apply f v args))) {} m))

(defn update-keys
  "Return `m` with `f` applied to each key in `m` with its `args`"
  [m f & args]
  (reduce-kv (fn [m' k v] (assoc m' (apply f k args) v)) {} m))

(defn update-or-assoc
  "If `k` exists in `m` apply the `update-fn`.
   Else, assoc `v` to that `k` in `m`"
  [m k v update-fn]
  (if (get m k)
    (update m k update-fn)
    (assoc m k v)))

(defn dissoc-in
  "Dissoc the value in `m` at `ks`"
  [m [k & ks]]
  (if ks
    (if (contains? m k)
      (update m k dissoc-in ks)
      m)
    (dissoc m k)))

(def ->kebab-keys
  "Takes a map and returns the map with kebab-cased keys."
  (partial cskx/transform-keys csk/->kebab-case-keyword))

(def ->snake-keys
  "Takes a map and returns the map with snake_cased keys."
  (partial cskx/transform-keys csk/->snake_case_keyword))

(def only-key
  "Attempt to return the first and only key in a map.
   If the map does not contain exactly one key:value pair, throw an exception"
  (comp only keys))

(def only-val
  "Attempt to return the first and only value in a map.
   If the map does not contain exactly one key:value pair, throw an exception"
  (comp only vals))

;;
;; UUID
;;
(defn uuid
  "Split operator to generate v1 uuids based on runtime env"
  []
  #? (:clj  (java.util.UUID/randomUUID)
      :cljs (random-uuid)))

(def guid
  "Split operator to generate v1 guids based on runtime env"
  uuid)

;;
;; CONVENIENCE
;;
(defn ->yes-no
  "If a value is truthy, returns the string \"Yes\".
   Else, return the string \"No\""
  [value]
  (if value "Yes" "No"))

(defn pluralize
  "Naively pluralize `given-string` based on `amount`
   Optionally, an `override-string` can be passed for special cases:
   e.g \"peach\" -> \"peaches\""
  ([given-string amount]
   (pluralize given-string amount (str given-string "s")))

  ([given-string amount override-string]
   (if (< 1 amount) override-string given-string)))
