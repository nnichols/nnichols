(ns nnichols.palette-test
  (:require [nnichols.palette :as palette]
            #? (:clj  [clojure.test :refer [deftest is testing run-tests]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest srm-number-to-rgba-test
  (testing "SRM color lookup behaves as expected"
    (is (= palette/srm-13 (palette/srm-number-to-rgba 13)))
    (is (= palette/srm-1  (palette/srm-number-to-rgba 0)))
    (is (= palette/srm-1  (palette/srm-number-to-rgba -1)))
    (is (= palette/srm-6  (palette/srm-number-to-rgba 6.2)))
    (is (= palette/srm-6  (palette/srm-number-to-rgba 6.8)))
    (is (= palette/srm-40 (palette/srm-number-to-rgba 41)))))
