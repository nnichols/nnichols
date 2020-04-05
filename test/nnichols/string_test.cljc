(ns nnichols.string-test
  (:require [nnichols.string :as nstr]
            #? (:clj  [clojure.test :refer [deftest is testing run-tests]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest prepare-for-compare-test
  (testing "Strings are appropriately re-cased and trimmed of whitespace"
    (is (= "" (nstr/prepare-for-compare "   ")))
    (is (= "clojure" (nstr/prepare-for-compare "ClOjUrE  ")))
    (is (= "clojure" (nstr/prepare-for-compare "clojure")))
    (is (= "100 lines of code" (nstr/prepare-for-compare "  100 lines of CODE")))
    (is (= "a   b" (nstr/prepare-for-compare "   a   b  ")))
    (is (= "CLOJURE" (nstr/prepare-for-compare "ClOjUrE  " {:upper-case? true})))
    (is (= "CLOJURE" (nstr/prepare-for-compare "clojure" {:upper-case? true})))
    (is (= "100 LINES OF CODE" (nstr/prepare-for-compare "  100 lines of CODE" {:upper-case? true})))
    (is (= "A   B" (nstr/prepare-for-compare "   a   b  " {:upper-case? true})))
    (is (= ":CLOJURE" (nstr/prepare-for-compare :ClOjUrE {:upper-case? true :coerce? true})))
    (is (= "CLOJURE" (nstr/prepare-for-compare "clojure" {:upper-case? true :coerce? true})))
    (is (= "100" (nstr/prepare-for-compare 100 {:upper-case? true :coerce? true})))
    (is (= "true" (nstr/prepare-for-compare true {:coerce? true})))
    (is (= "" (nstr/prepare-for-compare nil {:coerce? true})))))

(deftest string-compare-test
  (testing "Strings containing matching characters after perparation match"
    (is (true? (nstr/string-compare "   clojure" "CLOJURE   ")))
    (is (true? (nstr/string-compare "clojure   " "   CLOJURE   " {:upper-case? true})))
    (is (true? (nstr/string-compare "   100 LINES OF CODE" "100 LINES OF CODE   ")))
    (is (false? (nstr/string-compare "clo jure" "CLOJURE")))
    (is (false? (nstr/string-compare "100" "!))" {:upper-case? true})))
    (is (true? (nstr/string-compare true "true" {:coerce? true})))
    (is (true? (nstr/string-compare nil "" {:coerce? true})))
    (is (true? (nstr/string-compare :carrot ":CARROT" {:coerce? true})))))

(deftest string-includes?-test
  (testing "Strings containing matching characters after perparation match"
    (is (true? (nstr/string-includes? "   clojure" "CLOJURE   ")))
    (is (true? (nstr/string-includes? "CLOJURE   " "c")))
    (is (true? (nstr/string-includes? "clojure   " "   CLOJURE   " {:upper-case? true})))
    (is (true? (nstr/string-includes? "100 LINES OF CODE   " "   100 ")))
    (is (false? (nstr/string-includes? "clo" "CLOJURE")))
    (is (false? (nstr/string-includes? "100" "!))" {:upper-case? true})))
    (is (true? (nstr/string-includes? "falsefaasetrue" true {:coerce? true})))
    (is (true? (nstr/string-includes? nil "" {:coerce? true})))
    (is (true? (nstr/string-includes? :carrot ":CARROT" {:coerce? true})))))
