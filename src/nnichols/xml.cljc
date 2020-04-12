(ns nnichols.xml
  "A bunch of utility functions for xml documents"
  (:require [clojure.string :as cs]
            [nnichols.util :as nu]))

(defn xml-tag->keyword
  "Take an XML tag as extracted by `clojure.data.xml` and turn it into a kebab-cased, lower case keyword"
  [xml-tag]
  (let [xml-str (cs/lower-case (name xml-tag))]
    (keyword (cs/replace xml-str "_" "-"))))

(defn keyword->xml-tag
  "Take a clojure keyord and turn it into the form expected by `clojure.data.xml` by making it UPPER CASE and snake_cased"
  [edn-keyword]
  (let [edn-str (cs/upper-case (name edn-keyword))]
    (keyword (cs/replace edn-str "-" "_"))))

(defn unique-tags?
  "Take an XML sequence as formatted by `clojure.xml/parse`, and determine if it exclusively contains unique tags"
  [xml-seq]
  (when xml-seq
    (let [unique-tag-count (count (distinct (keep :tag xml-seq)))
          tag-count        (count (map :tag xml-seq))]
      (= unique-tag-count tag-count))))

(defn xml->edn
  "Transform an XML document as formatted by `clojure.xml/parse`, and transform it into normalized EDN.
   This also mutates keys from XML_CASE to lisp-case."
  [element]
  (let [xml-seq->edn (fn [e]
                       (if (> (count e) 1)
                         (if (unique-tags? e)
                           (reduce into {} (map (partial xml->edn) e))
                           (map xml->edn e))
                         (xml->edn (first e))))
        xml-map->edn (fn [e]
                       (let [edn-tag (xml-tag->keyword (:tag e))]
                         (if (:attrs e)
                           (let [attrs-key (keyword (str (name edn-tag) "-attrs"))
                                 attrs-val (nu/update-keys (:attrs e) xml-tag->keyword)]
                             {edn-tag (xml->edn (:content e))
                              attrs-key attrs-val})
                           {edn-tag (xml->edn (:content e))})))]
    (cond
      (nil? element)         nil
      (string? element)      element
      (sequential? element)  (xml-seq->edn element)
      (and (map? element)
           (empty? element)) {}
      (map? element)         (xml-map->edn element)
      :else                  nil)))
