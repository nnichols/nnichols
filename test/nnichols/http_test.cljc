(ns nnichols.http-test
  (:require [nnichols.http :as http]
            #? (:clj  [clojure.test :refer [deftest is testing]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest bodiless-json-response
  (testing "Response maps are properly created"
    (is (= 200 (:status (http/bodiless-json-response 200))))))
