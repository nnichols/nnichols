(ns nnichols.predicate-test
  (:require [nnichols.predicate :as np]
            [nnichols.util :as nu]
            #? (:clj  [clojure.test :refer [deftest is testing]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing]])
            #? (:clj  [clj-time.core :as time])
            #? (:cljs [cljs-time.core :as time])))

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

(deftest date-test
  (testing "Functional correctness"
    (is (true? (np/date? (time/now))))
    (is (false? (np/date? "December 26, 2019 12:34 AM")))
    (is (false? (np/date? nil)))
    (is (false? (np/date? 1577394213)))
    (is (false? (np/date? "1577394213")))))
