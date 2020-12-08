(ns nnichols.xml
  "A bunch of utility functions for xml documents"
  (:require [clojure.string :as cs]
            [clojure.data.xml :as xml]
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
  "Remove a suffix of `-attrs/_ATTRS` from `attrs-tag`"
  [attrs-tag]
  (let [tag-length (count attrs-tag)]
    (subs attrs-tag 0 (- tag-length attrs-length))))

(defn tag->attrs-tag
  "Transform `tag` to look like an attributes map tag by appending it with `-attrs/_ATTRS` pedending on the value of `upper-case?`"
  [tag upper-case?]
  (let [suffix (if upper-case? "_ATTRS" "-attrs")]
    (keyword (str (name tag) suffix))))

(defn edn-attrs-tag?
  "Returns true iff the list of `all-tags` to see if it contains the normalized `tag`"
  [tag all-tags]
  (boolean (and (cs/ends-with? (cs/lower-case tag) "attrs")
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

  ([{:keys [tag attrs content]} {:keys [preserve-keys? preserve-attrs? stringify-values?] :as opts}]
   (let [kw-function  (fn [k] (if preserve-keys? k (xml-tag->keyword k)))
         val-function (fn [v] (if stringify-values? (str v) v))
         edn-tag      (kw-function tag)]
     (if (and attrs preserve-attrs?)
       (let [attrs-suffix (if preserve-keys? "_ATTRS" "-attrs")
             attrs-key    (keyword (str (name edn-tag) attrs-suffix))
             attrs-val    (nu/update-vals (nu/update-keys attrs kw-function) val-function)]
         {edn-tag   (xml->edn content opts)
          attrs-key attrs-val})
       {edn-tag (xml->edn content opts)}))))

(defn xml->edn
  "Transform an XML document as formatted by `clojure.xml/parse`, and transform it into normalized EDN.
   By default, this also mutates keys from XML_CASE to lisp-case and ignores XML attributes within tags.
   To change this behavior, an option map may be provided with the following keys:
   preserve-keys? - to maintain the exact keyword structure provided by `clojure.xml/parse`
   preserve-attrs? - to maintain embedded XML attributes
   stringify-values? - to coerce non-nil, non-string, non-collection values to strings"
  ([xml-doc]
   (xml->edn xml-doc {}))

  ([xml-doc {:keys [stringify-values?] :as opts}]
   (cond
     (or (nil? xml-doc)
         (string? xml-doc)) xml-doc
     (sequential? xml-doc)  (xml-seq->edn xml-doc opts)
     (and (map? xml-doc)
          (empty? xml-doc)) {}
     (map? xml-doc)         (xml-map->edn xml-doc opts)
     (and stringify-values?
          (some? xml-doc))  (str xml-doc))))

(declare edn->xml)

(defn edn-seq->xml
  "Transform an EDN sequence to the pseudo XML expected by `clojure.data.xml`.
   To change the default behavior, an option map may be provided with the following keys:
   to-xml-case? - To modify the keys representing XML tags to XML_CASE
   from-xml-case? - If the source EDN has XML_CASE keys
   stringify-values? - to coerce non-nil, non-string, non-collection values to strings"
  ([edn]
   (edn-seq->xml edn {}))

  ([edn opts]
   (mapv #(edn->xml % opts) edn)))

(defn edn-map->xml
  "Transform an EDN map to the pseudo XML expected by `clojure.data.xml`.
   To change the default behavior, an option map may be provided with the following keys:
   to-xml-case? - To modify the keys representing XML tags to XML_CASE
   from-xml-case? - If the source EDN has XML_CASE keys
   stringify-values? - to coerce non-nil, non-string, non-collection values to strings"
  ([edn]
   (edn-map->xml edn {}))

  ([edn {:keys [to-xml-case? from-xml-case? stringify-values?] :as opts}]
   (let [edn-keys (keys edn)
         key-set (set (map name edn-keys))
         {attrs true tags false} (group-by #(edn-attrs-tag? (name %) key-set) edn-keys)
         attrs-set (set (map #(attrs-tag->tag (name %)) attrs))
         kw-function (fn [k] (if to-xml-case? (keyword->xml-tag k) k))
         val-function (fn [v] (if stringify-values? (str v) v))
         tag-generator (fn [t]
                         (let [xml-tag     (kw-function t)
                               xml-content (edn->xml (get edn t) opts)
                               xml-attrs   (when (contains? attrs-set (name t))
                                             (-> (get edn (tag->attrs-tag t from-xml-case?))
                                                 (nu/update-keys kw-function)
                                                 (nu/update-vals val-function)))]
                           {:tag     xml-tag
                            :content xml-content
                            :attrs   xml-attrs}))]
     (if (= 1 (count tags))
       (tag-generator (first tags))
       (mapv tag-generator tags)))))

(defn edn->xml
  "Transform an EDN data structure to the pseudo XML expected by `clojure.data.xml`.
   To change the default behavior, an option map may be provided with the following keys:
   to-xml-case? - To modify the keys representing XML tags to XML_CASE
   from-xml-case? - If the source EDN has XML_CASE keys
   stringify-values? - to coerce non-nil, non-string, non-collection values to strings"
  ([edn]
   (edn->xml edn {}))

  ([edn {:keys [stringify-values?] :as opts}]
   (cond
     (or (nil? edn)
         (string? edn))     [edn]
     (sequential? edn)      (edn-seq->xml edn opts)
     (and (map? edn)
          (empty? edn))     {}
     (map? edn)             (edn-map->xml edn opts)
     (and stringify-values?
          (some? edn))      (str edn))))

(defn deformat
  "Remove line termination formatting specific to Windows (since we're ingesting XML) and double spacing"
  [s]
  (cs/replace (cs/replace s #"\r\n" "") #"\s\s+" ""))

(defn xml-str->edn
   "Parse an XML document with `clojure.xml/parse` and transform it into normalized EDN.
   By default, this also mutates keys from XML_CASE to lisp-case and ignores XML attributes within tags.
   To change this behavior, an option map may be provided with the following keys:
   preserve-keys? - to maintain the exact keyword structure provided by `clojure.xml/parse`
   preserve-attrs? - to maintain embedded XML attributes
   stringify-values? - to coerce non-nil, non-string, non-collection values to strings"
  ([xml-str]
   (xml-str->edn xml-str {}))

  ([xml-str opts]
   (-> xml-str
       deformat
       xml/parse-str
       (xml->edn opts))))

(defn edn->xml-str
  "Transform an EDN data structure to into an XML string via `clojure.data.xml`.
   To change the default behavior, an option map may be provided with the following keys:
     to-xml-case? - To modify the keys representing XML tags to XML_CASE
     from-xml-case? - If the source EDN has XML_CASE keys
     stringify-values? - to coerce non-nil, non-string, non-collection values to strings"
  ([edn]
   (edn->xml-str edn {}))
  
  ([edn opts]
   (-> edn
       (edn->xml opts)
       xml/emit-str)))
