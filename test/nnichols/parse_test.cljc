(ns nnichols.parse-test
  (:require [nnichols.parse :as np]
            #? (:clj  [clojure.test :refer [deftest is testing run-tests]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest parse-radix-test
  (testing "Strings are turned into the positive integer they represent"
    (is (= 0 (np/parse-radix "000000000" 2) (np/parse-binary "000000000")))
    (is (= 0 (np/parse-radix "000000000" 4)))
    (is (= 0 (np/parse-radix "000000000" 36)))
    (is (let [i (rand-int 1000000000)]
      (= i (np/parse-radix (str i) 10))))
    (is (= 170 (np/parse-radix "10101010" 2) (np/parse-binary "10101010")))
    (is (= 2054353 (np/parse-radix "7654321" 8) (np/parse-octal "7654321")))
    (is (= 13382451 (np/parse-radix "CC3333" 16) (np/parse-hex "cc3333")))))
