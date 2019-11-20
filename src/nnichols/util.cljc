(ns nnichols.util
  "A bunch of utility functions"
  (:refer-clojure :exclude [uuid]))

;;
;; RUNTIME SAFETY
;;
(defn try-or-nil
  "Try applying f to args, returning nil in case of an Exception."
  [f & args]
  (try
    (apply f args)
    (catch #? (:clj Exception :cljs :default) _
      nil)))

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

;;
;; UUID
;;
(defn uuid
  "Split operator to generate v1 uuids based on runtime env"
  []
  #? (:clj  (java.util.UUID/randomUUID)
      :cljs (random-uuid)))

;;
;; CONVENIENCE
;;
(defn ->yes-no
  "If a value is truthy, returns the string Yes.
   Else, return the string No"
  [value]
  (if value "Yes" "No"))
