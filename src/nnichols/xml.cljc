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

(def ^:const attrs-length
  (count "-attrs"))

(defn attrs-tag->tag
  [attrs-tag]
  (let [tag-length (count attrs-tag)]
    (subs attrs-tag 0 (- tag-length attrs-length))))

(defn tag->attrs-tag
  [tag]
  (keyword (str (name tag) "-attrs")))

(defn edn-attrs-tag?
  [tag all-tags]
  (boolean (and (cs/ends-with? tag "-attrs")
                (contains? all-tags (attrs-tag->tag tag)))))

(defn unique-tags?
  "Take an XML sequence as formatted by `clojure.xml/parse`, and determine if it exclusively contains unique tags"
  [xml-seq]
  (let [unique-tag-count (count (distinct (keep :tag xml-seq)))
        tag-count        (count (map :tag xml-seq))]
    (= unique-tag-count tag-count)))

(declare xml->edn)

(defn xml-seq->edn
  "Transform an XML sequence as formatted by `clojure.xml/parse`, and transform it into normalized EDN.
   By default, this also mutates keys from XML_CASE to lisp-case and ignores XML attributes within tags.
   To change this behavior, an option map be provided with the following keys:
   preserve-keys? - to maintain the exact keyword structure provided by `clojure.xml/parse`
   preserve-attrs? - to maintain embedded XML attributes"
  ([xml-seq]
   (xml-seq->edn xml-seq {}))

  ([xml-seq opts]
   (let [xml-transformer (fn [x] (xml->edn x opts))]
       (if (and (unique-tags? xml-seq) (> (count xml-seq) 1))
         (reduce into {} (mapv xml-transformer xml-seq))
         (if (or (string? (first xml-seq)) (nil? (first xml-seq)))
           (xml-transformer (first xml-seq))
           (mapv xml-transformer xml-seq))))))

(defn xml-map->edn
  "Transform an XML map as formatted by `clojure.xml/parse`, and transform it into normalized EDN.
   By default, this also mutates keys from XML_CASE to lisp-case and ignores XML attributes within tags.
   To change this behavior, an option map be provided with the following keys:
   preserve-keys? - to maintain the exact keyword structure provided by `clojure.xml/parse`
   preserve-attrs? - to maintain embedded XML attributes"
  ([xml-map]
   (xml-map->edn xml-map {}))

  ([{:keys [tag attrs content]} {:keys [preserve-keys? preserve-attrs?] :as opts}]
   (let [kw-function (fn [k] (if preserve-keys? k (xml-tag->keyword k)))
         edn-tag     (kw-function tag)]
     (if (and attrs preserve-attrs?)
       (let [attrs-suffix (if preserve-keys? "_ATTRS" "-attrs")
             attrs-key    (keyword (str (name edn-tag) attrs-suffix))
             attrs-val    (nu/update-keys attrs kw-function)]
         {edn-tag   (xml->edn content opts)
          attrs-key attrs-val})
       {edn-tag (xml->edn content opts)}))))

(defn xml->edn
  "Transform an XML document as formatted by `clojure.xml/parse`, and transform it into normalized EDN.
   By default, this also mutates keys from XML_CASE to lisp-case and ignores XML attributes within tags.
   To change this behavior, an option map may be provided with the following keys:
   preserve-keys? - to maintain the exact keyword structure provided by `clojure.xml/parse`
   preserve-attrs? - to maintain embedded XML attributes"
  ([xml-doc]
   (xml->edn xml-doc {}))

  ([xml-doc opts]
   (cond
     (or (nil? xml-doc)
         (string? xml-doc)) xml-doc
     (sequential? xml-doc)  (xml-seq->edn xml-doc opts)
     (and (map? xml-doc)
          (empty? xml-doc)) {}
     (map? xml-doc)         (xml-map->edn xml-doc opts)
     :else                  nil)))

(declare edn->xml)

(defn edn-seq->xml
  [edn]
  (mapv edn->xml edn))

(defn edn-map->xml
  [edn]
  (let [
        edn-keys (keys edn)
        key-set (set (map name edn-keys))
        {attrs true tags false} (group-by #(edn-attrs-tag? (name %) key-set) edn-keys)
        attrs-set (set (map #(attrs-tag->tag (name %)) attrs))
        tag-generator (fn [t] (merge {:tag t
                                      :content (edn->xml (get edn t))}
                                     (when (contains? attrs-set (name t))
                                       {:attrs (get edn (tag->attrs-tag t))})))]
    (if (= 1 (count tags))
      (tag-generator (first tags))
      (mapv tag-generator tags))))

(defn edn->xml
  [edn]
  (cond
     (or (nil? edn)
         (string? edn)) [edn]
     (sequential? edn)  (edn-seq->xml edn)
     (and (map? edn)
          (empty? edn)) {}
     (map? edn)         (edn-map->xml edn)
     :else              nil))
