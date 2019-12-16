(ns nnichols.predicate-test
  (:require [nnichols.predicate :as np]
            [nnichols.util :as nu]
            #? (:clj  [clojure.test :refer [deftest is testing run-tests]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest boolean?-test
  (testing "Only boolean values evalutae true"
    (is (true? (np/boolean? true)))
    (is (true? (np/boolean? false)))
    (is (false? (np/boolean? nil)))
    (is (false? (np/boolean? "true")))
    (is (false? (np/boolean? 1)))
    (is (true? (string? "false")))))

(deftest guid-uuid-test
  (testing "Functional correctness"
    (is (true? (np/uuid? (nu/uuid))))
    (is (true? (np/guid? (nu/guid))))
    (is (true? (np/guid? (nu/uuid))))
    (is (true? (np/uuid? (nu/guid))))
    (is (false? (np/uuid? nil)))
    (is (false? (np/uuid? "705d0e70-08db-450e-9982-c897d483136a")))))
