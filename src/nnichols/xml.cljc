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
   By default, this also mutates keys from XML_CASE to lisp-case and ignores XML attributes within tags.
   To change this behavior, an option map be provided with the following keys:
   preserve-keys? - to maintain the exact keyword structure provided by `clojure.xml/parse`
   preserve-attrs? - to maintain embedded XML attributes"
  ([xml-doc]
   (xml->edn xml-doc {}))

  ([xml-doc {:keys [preserve-keys? preserve-attrs?]}]
   (let [kw-function (fn [k]
                       (if preserve-keys?
                         k
                         (xml-tag->keyword k)))
         xml-seq->edn (fn [e]
                        (if (> (count e) 1)
                          (if (unique-tags? e)
                            (reduce into {} (map xml->edn e))
                            (map xml->edn e))
                          (xml->edn (first e))))
         xml-map->edn (fn [{:keys [tag attrs content]}]
                        (let [edn-tag (kw-function tag)]
                          (if (and attrs preserve-attrs?)
                            (let [attrs-suffix (if preserve-keys? "_ATTRS" "-attrs")
                                  attrs-key    (keyword (str (name edn-tag) attrs-suffix))
                                  attrs-val    (nu/update-keys attrs kw-function)]
                              {edn-tag   (xml->edn content)
                               attrs-key attrs-val})
                            {edn-tag (xml->edn content)})))]
     (cond
       (nil? xml-doc)         nil
       (string? xml-doc)      xml-doc
       (sequential? xml-doc)  (xml-seq->edn xml-doc)
       (and (map? xml-doc)
            (empty? xml-doc)) {}
       (map? xml-doc)         (xml-map->edn xml-doc)
       :else                  nil))))
