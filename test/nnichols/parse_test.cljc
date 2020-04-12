(ns nnichols.parse-test
  (:require [nnichols.parse :as np]
            [nnichols.util :as nu]
            [nnichols.predicate :as npr]
            #? (:clj  [clojure.test :refer [deftest is testing]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest parse-radix-test
  (testing "Strings are turned into the positive integer they represent"
    (is (= 0 (np/parse-radix "000000000" 2) (np/parse-binary "000000000")))
    (is (= 0 (np/parse-radix "000000000" 4)))
    (is (= 4 (np/parse-radix 4 4)))
    (is (= 0 (np/parse-radix "000000000" 36)))
    (is (let [i (rand-int 1000000000)]
      (= i (np/parse-radix (str i) 10))))
    (is (= 170 (np/parse-radix "10101010" 2) (np/parse-binary "10101010")))
    (is (= 2054353 (np/parse-radix "7654321" 8) (np/parse-octal "7654321")))
    (is (= 13382451 (np/parse-radix "CC3333" 16) (np/parse-hex "cc3333")))))

(deftest try-parse-uuid-test
  (testing "functional correctness"
    (is (npr/uuid? (np/try-parse-uuid (nu/uuid))))
    (is (npr/uuid? (np/try-parse-uuid (str (nu/uuid)))))
    (is (npr/guid? (np/try-parse-guid (nu/guid))))
    (is (npr/guid? (np/try-parse-guid (str (nu/guid)))))
    (is (npr/uuid? (np/try-parse-uuid "705d0e70-08db-450e-9982-c897d483136a")))
    (is (npr/uuid? (np/try-parse-guid "705d0e70-08db-450e-9982-c897d483136a")))
    (is (npr/guid? (np/try-parse-uuid "705d0e70-08db-450e-9982-c897d483136a")))
    (is (npr/guid? (np/try-parse-guid "705d0e70-08db-450e-9982-c897d483136a")))
    (is (npr/uuid? (np/parse-guid "705d0e70-08db-450e-9982-c897d483136a")))
    (is (nil? (np/try-parse-guid 10)))
    (is (nil? (np/try-parse-uuid nil)))
    #?(:cljs (is (npr/guid? (np/try-parse-uuid "705d0e7008db450e9982c897d483136a"))))
    #?(:clj  (is (nil? (np/try-parse-uuid "705d0e7008db450e9982c897d483136a"))))))

(deftest parse-int-tests
  (testing "Ensure each of the integer parsing functions work as expected"
    (is (let [i (rand-int 1000000000)]
      (= i (np/parse-int (str i)))))
    (is (let [i (rand-int 1000000000)]
      (= i (np/parse-int i))))
    (is (let [i (rand-int 1000000000)]
      (= i (np/try-parse-int (str i)))))
    (is (let [i (rand-int 1000000000)]
      (= i (np/try-parse-int i))))
    (is (let [i (rand-int 1000000000)]
      (= i (np/parse-int-or-zero (str i)))))
    (is (let [i (rand-int 1000000000)]
      (= i (np/parse-int-or-zero i))))
    #?(:cljs (is (zero? (np/try-parse-int 0.0))))
    #?(:clj  (is (nil? (np/try-parse-int 0.0))))
    #?(:cljs (is (= 6 (np/try-parse-int "6.902"))))
    #?(:clj  (is (nil? (np/try-parse-int "6.902"))))
    (is (nil? (np/try-parse-int true)))
    (is (nil? (np/try-parse-int nil)))
    (is (zero? (np/parse-int-or-zero "ABC")))
    #?(:clj (is (= Integer/MAX_VALUE (np/try-parse-int (str Integer/MAX_VALUE)))))))

#?(:clj
   (deftest parse-double-tests
     (testing "Ensure each of the integer parsing functions work as expected"
       (is (= 20.0 (np/parse-double "20.0")))
       (is (= -12.34 (np/parse-double "-12.34")))
       (is (= 1134.0 (np/parse-double "1134.")))
       (is (= 20.0 (np/try-parse-double "20.0")))
       (is (= 1134.0 (np/try-parse-double "1134.")))
       (is (= 20.0 (np/parse-double-or-zero "20.0")))
       (is (= 1134.0 (np/parse-double-or-zero "1134.")))
       (is (double? (np/try-parse-double (double 1134.0))))
       (is (nil? (np/try-parse-double "banana")))
       (is (nil? (np/try-parse-double false)))
       (is (zero? (np/parse-double-or-zero [:a :b]))))))

(deftest float-string?
  (testing "Ensure float detection regex works"
    (is (true? (np/float-string? "123")))
    (is (true? (np/float-string? "123")))
    (is (true? (np/float-string? "-123")))
    (is (true? (np/float-string? "123.456")))
    (is (true? (np/float-string? "123e456")))
    (is (true? (np/float-string? "123.456e789")))
    (is (true? (np/float-string? "-123.456e789")))
    (is (true? (np/float-string? "123.")))
    (is (false? (np/float-string? "   . ")))
    (is (false? (np/float-string? ".")))
    (is (false? (np/float-string? "123.456howza")))
    (is (false? (np/float-string? "howza123.456")))))

(deftest parse-float-tests
  (testing "Ensure each of the integer parsing functions work as expected"
    (is (= 20.0 (np/parse-float "20.0")))
    (is (= 1134.0 (np/parse-float "1134.")))
    (is (= 20.0 (np/try-parse-float "20")))
    (is (= 1134.0 (np/try-parse-float "1134.")))
    (is (= 20.0 (np/parse-float-or-zero "20.0")))
    (is (= 1134.0 (np/parse-float-or-zero "1134.")))
    (is (= 1134.0 (np/try-parse-float "1134.")))
    (is (nil? (np/try-parse-float "banana")))
    (is (nil? (np/try-parse-float false)))
    (is (zero? (np/parse-float-or-zero [:a :b])))))

(deftest comp-partial-tests
  (testing "Test the composed functions for trys/0s"
    (is (every? nil? [(np/try-parse-radix "AB" 9) (np/try-parse-binary "AB") (np/try-parse-octal "AB")]))
    (is (every? nil? [(np/try-parse-radix true 3) (np/try-parse-binary true) (np/try-parse-octal true) (np/try-parse-hex true)]))
    (is (every? zero? [(np/parse-radix-or-zero :AB 9) (np/parse-binary-or-zero :AB) (np/parse-octal-or-zero :AB)]))
    (is (every? zero? [(np/parse-radix-or-zero false 3) (np/parse-binary-or-zero false) (np/parse-octal-or-zero false) (np/parse-hex-or-zero false)]))))

(deftest parse-boolean-test
  (testing "Ensure boolean parsing works as expected"
    (is (true? (np/parse-boolean true)))
    (is (false? (np/parse-boolean false)))
    (is (true? (np/parse-boolean-strict true)))
    (is (false? (np/parse-boolean-strict false)))
    (is (true? (np/parse-boolean "true")))
    (is (false? (np/parse-boolean "fAlSe")))
    (is (true? (np/parse-boolean-strict "true")))
    (is (false? (np/parse-boolean-strict "fAlSe")))
    (is (true? (np/parse-boolean [])))
    (is (true? (np/parse-boolean 17)))
    (is (false? (np/parse-boolean nil)))))
