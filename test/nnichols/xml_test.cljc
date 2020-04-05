(ns nnichols.xml-test
  (:require [nnichols.xml :as nx]
            #? (:clj  [clojure.test :refer [deftest is testing run-tests]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest tag-keyword-conversion-test
  (testing "Tag formatted and EDN formatted keywords can be transformed"
    (is (= :XML_TAG (nx/keyword->xml-tag :xml-tag)))
    (is (= :edn-keyword (nx/xml-tag->keyword :EDN_KEYWORD)))
    (is (= :XML_TAG (nx/keyword->xml-tag (nx/xml-tag->keyword :XML_TAG))))
    (is (= :edn-keyword (nx/xml-tag->keyword (nx/keyword->xml-tag :edn-keyword))))))
