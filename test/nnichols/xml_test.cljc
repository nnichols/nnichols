(ns nnichols.xml-test
  (:require [nnichols.xml :as nx]
            #? (:clj  [clojure.test :refer [deftest is testing]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

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

(def xml-example
  {:tag :TEST_DOCUMENT
   :attrs {:xmlns "https://www.fake.not/real"}
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
         :attrs {:bytes "10100010" :number "-94"}
         :content ["more fake data"]}]}]}]})

(def edn-example
  {:test-document
   {:head [{:meta-data "Some Fake Data!"}
           {:meta-data "Example Content"}]
    :file {:groups {:group "test-data-club"}
           :segments [{:segment "more data"}
                      {:segment "more fake data"}]}}})

(def edn-example-original-keys
  {:TEST_DOCUMENT
   {:HEAD [{:META_DATA "Some Fake Data!"}
           {:META_DATA "Example Content"}]
    :FILE {:GROUPS {:GROUP "test-data-club"}
           :SEGMENTS [{:SEGMENT "more data"}
                      {:SEGMENT "more fake data"}]}}})

(def edn-example-with-attrs
  {:test-document
   {:head [{:meta-data "Some Fake Data!" :meta-data-attrs {:type "title"}}
           {:meta-data "Example Content" :meta-data-attrs {:type "tag"}}]
    :file {:groups {:group "test-data-club"}
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
    :FILE {:GROUPS {:GROUP "test-data-club"}
           :SEGMENTS [{:SEGMENT "more data" :SEGMENT_ATTRS {:BITS "00111010" :NUMBER "58"}}
                      {:SEGMENT "more fake data" :SEGMENT_ATTRS {:bytes "10100010" :number "-94"}}]}
    :FILE_ATTRS {:POSTER "JANE DOE <j.doe@fake-email.not-real>"
                 :DATE "2020/04/12"
                 :SUBJECT "TEST DATA"}}
   :TEST_DOCUMENT_ATTRS {:xmlns "https://www.fake.not/real"}})

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
