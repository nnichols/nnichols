(ns nnichols.util-test
  (:require [nnichols.util :as nu]
            #? (:clj  [clojure.test :refer [deftest is testing run-tests]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing run-tests]])))

(deftest sort-keys-test
  (testing "Keywords are sorted lexicographically"
    (is (= [:a :b :c] (nu/sort-keys {:a 1 :b 2 :c 3})))
    (is (= [:B :a :c] (nu/sort-keys {:a "val" :B 2 :c nil})))
    (is (= ["A" "B" "C"] (nu/sort-keys {"C" 1 "A" :a "B" :foo})))
    (is (= [] (nu/sort-keys {}))))

  (testing "Functional safety"
    (is (= [] (nu/sort-keys [])))
    (is (= [] (nu/sort-keys '())))
    (is (= [] (nu/sort-keys nil)))))

(deftest sort-vals-test
  (testing "Values are sorted lexicographically"
    (is (= [1 2 3] (nu/sort-vals {:a 1 :b 2 :c 3})))
    (is (= ["a" "b" "c"] (nu/sort-vals {:a "a" :B "c" "b" "b"})))
    (is (= [1 2 3] (nu/sort-vals {"c" 1 :A 3 "B" 2})))
    (is (= [] (nu/sort-vals {}))))

  (testing "Functional safety"
    (is (= [] (nu/sort-vals [])))
    (is (= [] (nu/sort-vals '())))
    (is (= [] (nu/sort-vals nil)))))

(deftest rand-key-test
  (testing "Retreived keys belong to source map"
    (is (= :a (nu/rand-key {:a {:b 1}})))
    (is (#{:a :B :c } (nu/rand-key {:a 1 :B nil :c "foo"})))
    (is (#{"a" "B" "c"} (nu/rand-key {"a" :a "B" nil "c" 3.14})))
    (is (#{:a "B" :c } (nu/rand-key {:a 1 "B" nil :c "foo"}))))

  (testing "Functional safety"
    (is (nil? (nu/rand-key {})))
    (is (nil? (nu/rand-key [])))
    (is (nil? (nu/rand-key nil)))))

(deftest rand-val-test
  (testing "Retreived vals belong to source map"
    (is (#{:d 1 "foo"} (nu/rand-val {:a 1 :B :d :c "foo"})))
    (is (#{:a true 3.14} (nu/rand-val {"a" :a "B" true "c" 3.14})))
    (is (= {:a 1} (nu/rand-val {:c {:a 1}}))))

  (testing "Functional safety"
    (is (nil? (nu/rand-val {})))
    (is (nil? (nu/rand-val [])))
    (is (nil? (nu/rand-val nil)))))

(deftest rand-kv-test
  (testing "Keys and values belong to source map"
    (is (= {:a 1} (nu/rand-kv {:a 1})))
    (is (= {nil :v} (nu/rand-kv {nil :v})))
    (let [assertion-map {:a 1 :b {:c 2} :C :d "e" "F"}
          found-kv (nu/rand-kv assertion-map)]
      (is (map? found-kv))
      (is (not-empty found-kv))
      (is (get (set (keys assertion-map)) (first (keys found-kv))))
      (is (get (set (vals assertion-map)) (first (vals found-kv))))))

  (testing "Test stated opinions on safety and correctness"
    (is (= {} (nu/rand-kv {})))
    (is (= {} (nu/rand-kv [])))
    (is (= {} (nu/rand-kv #{})))
    (is (= {} (nu/rand-kv nil)))))

(deftest update-or-assoc-test
  (testing "Functional correctness"
    (is (= {:a 1} (nu/update-or-assoc {} :a 1 inc)))
    (is (= {:a 2} (nu/update-or-assoc {:a 1} :a 1 inc))))

  (testing "Throws an exception when a non-associative colelction is passed"
    (is (nil? (nu/try-or-nil nu/update-or-assoc [] :a nil dec)))))

(deftest dissoc-in-test
  (testing "Functional correctness"
    (is (= nil (nu/dissoc-in nil [:b])))
    (is (= {} (nu/dissoc-in {} [])))
    (is (= {} (nu/dissoc-in {} [:b])))
    (is (= {} (nu/dissoc-in {} [:a :b])))
    (is (= {:a nil} (nu/dissoc-in {:a nil} [:a :b])))
    (is (= {:a nil :b false} (nu/dissoc-in {:a nil :b false} [:a :b])))
    (is (= {:a nil :b false} (nu/dissoc-in {:a nil :b false} [])))
    (is (= {:a 1} (nu/dissoc-in {:a 1 :b 2} [:b])))
    (is (= {:a {:c 3}} (nu/dissoc-in {:a {:b 2 :c 3}} [:a :b])))
    (is (= {:a {:c {:d 4} :e 5} :d 4} (nu/dissoc-in {:a {:c {:d 4} :e 5} :d 4} [:a :c :f])))
    (is (= {:a {:c {:d 4} :e 5} :d 4} (nu/dissoc-in {:a {:c {:d 4 :f [1 2 3]} :e 5} :d 4} [:a :c :f]))))

  (testing "Throws an exception when a non-associative colelction is passed"
    (is (nil? (nu/try-or-nil nu/dissoc-in [:a :b :c] [:a])))))

(deftest ->yes-no-test
  (testing "Functional correctness"
    (is (= "Yes" (nu/->yes-no true)))
    (is (= "Yes" (nu/->yes-no {:some "value"})))
    (is (= "Yes" (nu/->yes-no (:some {:some "value"}))))
    (is (= "No"  (nu/->yes-no false)))
    (is (= "No"  (nu/->yes-no (:none {:some "value"}))))
    (is (= "No"  (nu/->yes-no nil)))))
