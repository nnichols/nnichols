(ns nnichols.xml-test
  (:require [nnichols.xml :as nx]
            #? (:clj  [clojure.test :refer [deftest is testing]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing]])))

(deftest tag-keyword-conversion-test
  (testing "Tag formatted and EDN formatted keywords can be transformed"
    (is (= :XML_TAG (nx/keyword->xml-tag :xml-tag)))
    (is (= :edn-keyword (nx/xml-tag->keyword :EDN_KEYWORD)))
    (is (= :XML_TAG (nx/keyword->xml-tag (nx/xml-tag->keyword :XML_TAG))))
    (is (= :edn-keyword (nx/xml-tag->keyword (nx/keyword->xml-tag :edn-keyword))))))

(deftest unique-tags?-test
  (testing "Functional correctness"
    (is (true? (nx/unique-tags? [{:tag "One"} {:tag "Two"} {:tag "Three"}])))
    (is (true? (nx/unique-tags? [])))
    (is (true? (nx/unique-tags? [{:tag "One"}])))
    (is (false? (nx/unique-tags? [{:tag "One"} {:tag "One"}])))))

(deftest attrs-tag->tag-test
  (testing "Functional correctness"
    (is (= "HTML" (nx/attrs-tag->tag "HTML_ATTRS")))
    (is (= "edn" (nx/attrs-tag->tag "edn-attrs")))))

(deftest tag->attrs-tag-test
  (testing "Functional correctness"
    (is (= :HTML-attrs (nx/tag->attrs-tag "HTML" false)))
    (is (= :HTML-attrs (nx/tag->attrs-tag :HTML false)))
    (is (= :HTML_ATTRS (nx/tag->attrs-tag "HTML" true)))
    (is (= :HTML_ATTRS (nx/tag->attrs-tag :HTML true)))
    (is (= :edn-attrs (nx/tag->attrs-tag "edn" false)))))

(def xml-example
  {:tag :TEST_DOCUMENT
   :attrs {:XMLNS "https://www.fake.not/real"}
   :content
   [{:tag :HEAD
     :attrs nil
     :content
     [{:tag :META_DATA :attrs {:TYPE "title"} :content ["Some Fake Data!"]}
      {:tag :META_DATA :attrs {:TYPE "tag"} :content ["Example Content"]}]}
    {:tag :FILE
     :attrs
     {:POSTER "JANE DOE <j.doe@fake-email.not-real>"
      :DATE "2020/04/12"
      :SUBJECT "TEST DATA"}
     :content
     [{:tag :GROUPS
       :attrs nil
       :content
       [{:tag :GROUP :attrs nil :content ["test-data-club"]}]}
      {:tag :SEGMENTS
       :attrs nil
       :content
       [{:tag :SEGMENT
         :attrs {:BITS "00111010" :NUMBER "58"}
         :content ["more data"]}
        {:tag :SEGMENT
         :attrs {:BYTES "10100010" :NUMBER "-94"}
         :content ["more fake data"]}]}]}]})

(def edn-example
  {:test-document
   {:head [{:meta-data "Some Fake Data!"}
           {:meta-data "Example Content"}]
    :file {:groups [{:group "test-data-club"}]
           :segments [{:segment "more data"}
                      {:segment "more fake data"}]}}})

(def edn-example-original-keys
  {:TEST_DOCUMENT
   {:HEAD [{:META_DATA "Some Fake Data!"}
           {:META_DATA "Example Content"}]
    :FILE {:GROUPS [{:GROUP "test-data-club"}]
           :SEGMENTS [{:SEGMENT "more data"}
                      {:SEGMENT "more fake data"}]}}})

(def edn-example-with-attrs
  {:test-document
   {:head [{:meta-data "Some Fake Data!" :meta-data-attrs {:type "title"}}
           {:meta-data "Example Content" :meta-data-attrs {:type "tag"}}]
    :file {:groups [{:group "test-data-club"}]
           :segments [{:segment "more data" :segment-attrs {:bits "00111010" :number "58"}}
                      {:segment "more fake data" :segment-attrs {:bytes "10100010" :number "-94"}}]}
    :file-attrs {:poster "JANE DOE <j.doe@fake-email.not-real>"
                 :date "2020/04/12"
                 :subject "TEST DATA"}}
   :test-document-attrs {:xmlns "https://www.fake.not/real"}})

(def edn-example-with-attrs-and-original-keys
  {:TEST_DOCUMENT
   {:HEAD [{:META_DATA "Some Fake Data!" :META_DATA_ATTRS {:TYPE "title"}}
           {:META_DATA "Example Content" :META_DATA_ATTRS {:TYPE "tag"}}]
    :FILE {:GROUPS [{:GROUP "test-data-club"}]
           :SEGMENTS [{:SEGMENT "more data" :SEGMENT_ATTRS {:BITS "00111010" :NUMBER "58"}}
                      {:SEGMENT "more fake data" :SEGMENT_ATTRS {:BYTES "10100010" :NUMBER "-94"}}]}
    :FILE_ATTRS {:POSTER "JANE DOE <j.doe@fake-email.not-real>"
                 :DATE "2020/04/12"
                 :SUBJECT "TEST DATA"}}
   :TEST_DOCUMENT_ATTRS {:XMLNS "https://www.fake.not/real"}})

(deftest xml->edn-test
  (testing "Functional correctness"
    (is (= (nx/xml->edn xml-example) edn-example))
    (is (= (nx/xml->edn xml-example {:preserve-keys? true}) edn-example-original-keys))
    (is (= (nx/xml->edn xml-example {:preserve-attrs? true}) edn-example-with-attrs))
    (is (= (nx/xml->edn xml-example {:preserve-keys? true :preserve-attrs? true}) edn-example-with-attrs-and-original-keys))
    (is (nil? (nx/xml->edn nil)))
    (is (nil? (nx/xml->edn :edn)))
    (is (= (nx/xml->edn {}) {}))
    (is (= (nx/xml->edn "XML") "XML"))))

(deftest edn->xml-test
  (testing "Functional correctness"
    (is (= xml-example (nx/edn->xml (nx/xml->edn xml-example {:preserve-attrs? true}) {:to-xml-case? true :stringify-values? true})))
    (is (= (nx/edn->xml edn-example-with-attrs-and-original-keys {:to-xml-case? true :from-xml-case? true :stringify-values? true}) xml-example))
    (is (= (nx/edn->xml edn-example-with-attrs {:to-xml-case? true :stringify-values? true}) xml-example))
    (is (= (nx/edn->xml nil) [nil]))
    (is (nil? (nx/edn->xml :edn)))
    (is (= (nx/edn->xml {}) {}))
    (is (= (nx/edn->xml "XML") ["XML"]))
    (is (= (nx/edn->xml 100 {:stringify-values? true}) "100"))
    (is (nil? (nx/edn->xml 100)))))
