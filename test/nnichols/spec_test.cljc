(ns nnichols.spec-test
  (:require [clojure.spec.alpha :as csa]
            [clojure.spec.gen.alpha :as sgen]
            [nnichols.predicate :as np]
            [nnichols.spec :as s]
            [nnichols.util :as nu]
            #? (:clj  [clojure.test :refer [deftest is testing]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest boolean-spec-test
  (testing "Testing the boolean spec can conform/validate data"
    (is (csa/valid? ::s/n-boolean true))
    (is (false? (csa/valid? ::s/n-boolean 15)))
    (is (false? (csa/conform ::s/n-boolean "fAlSe")))
    (is (= ::csa/invalid (csa/conform ::s/n-boolean nil)))
    (is (every? np/boolean? (sgen/sample (csa/gen ::s/n-boolean) 100)))))

(deftest uuid-spec-test
  (testing "Testing the uuid spec can conform/validate data"
    (is (csa/valid? ::s/n-uuid (nu/uuid)))
    (is (false? (csa/valid? ::s/n-uuid 15)))
    (is (np/uuid? (csa/conform ::s/n-uuid "d6fa29c1-0592-41cd-ac91-880e5d25a826")))
    (is (= ::csa/invalid (csa/conform ::s/n-uuid nil)))
    (is (every? np/uuid? (sgen/sample (csa/gen ::s/n-uuid) 100)))))

(deftest integer-spec-test
  (testing "Testing the integer spec can conform/validate data"
    (is (csa/valid? ::s/n-integer 165464))
    (is (false? (csa/valid? ::s/n-integer false)))
    (is (= 123456789 (csa/conform ::s/n-integer "123456789")))
    (is (= ::csa/invalid (csa/conform ::s/n-integer :nope)))
    (is (every? int? (sgen/sample (csa/gen ::s/n-integer) 100)))))

(deftest keyword-spec-test
  (testing "Testing the keyword spec can conform/validate data"
    (is (csa/valid? ::s/n-keyword :hello-there))
    (is (false? (csa/valid? ::s/n-keyword 12.70)))
    (is (= :aBc (csa/conform ::s/n-keyword "aBc")))
    (is (= ::csa/invalid (csa/conform ::s/n-keyword [])))
    (is (every? keyword? (sgen/sample (csa/gen ::s/n-keyword) 100)))))
