(ns nnichols.util-test
  (:require [nnichols.util :as nu]
            #? (:clj  [clojure.test :refer [deftest is testing]])
            #? (:cljs [cljs.test    :refer-macros [deftest is testing]])))

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
    (is (#{:a :B :c} (nu/rand-key {:a 1 :B nil :c "foo"})))
    (is (#{"a" "B" "c"} (nu/rand-key {"a" :a "B" nil "c" 3.14})))
    (is (#{:a "B" :c} (nu/rand-key {:a 1 "B" nil :c "foo"}))))

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

(deftest filter-by-values-test
  (testing "Only k-v pairs whose values when applied to f are truthy remains"
    (is (= {} (nu/filter-by-values some? {})))
    (is (= {} (nu/filter-by-values nil? {})))
    (is (= {:a 2 :c 4 :d 6} (nu/filter-by-values even? {:a 2 :b 1 :c 4 :d 6 :e 7})))
    (is (= {:a nil} (nu/filter-by-values nil? {:a nil :b {:c nil}})))
    (is (= {"a" 1 "b" 3 "c" 5} (nu/filter-by-values odd? {"a" 1 "b" 3 "c" 5})))
    (is (= {} (nu/filter-by-values map? {:a [] :b 5 :c "hello"})))))

(deftest remove-by-values-test
  (testing "Only k-v pairs whose values when applied to f are falsey remains"
    (is (= {} (nu/remove-by-values some? {})))
    (is (= {} (nu/remove-by-values nil? {})))
    (is (= {:b 1 :e 7} (nu/remove-by-values even? {:a 2 :b 1 :c 4 :d 6 :e 7})))
    (is (= {:b {:c nil}} (nu/remove-by-values nil? {:a nil :b {:c nil}})))
    (is (= {} (nu/remove-by-values odd? {"a" 1 "b" 3 "c" 5})))
    (is (= {:a [] :b 5 :c "hello"} (nu/remove-by-values map? {:a [] :b 5 :c "hello"})))))

(deftest filter-by-keys-test
  (testing "Only k-v pairs whose keys when applied to f are truthy remains"
    (is (= {} (nu/filter-by-keys some? {})))
    (is (= {} (nu/filter-by-keys nil? {})))
    (is (= {2 :a 4 :c 6 :d} (nu/filter-by-keys even? {2 :a 1 :b 4 :c 6 :d 7 :e})))
    (is (= {nil :a} (nu/filter-by-keys nil? {nil :a :b {:c nil}})))
    (is (= {"a" 1 "b" 3 "c" 5} (nu/filter-by-keys string? {"a" 1 "b" 3 "c" 5})))
    (is (= {} (nu/filter-by-keys string? {:a [] :b 5 :c "hello"})))))

(deftest remove-by-keys-test
  (testing "Only k-v pairs whose keys when applied to f are falsey remains"
    (is (= {} (nu/remove-by-keys some? {})))
    (is (= {} (nu/remove-by-keys nil? {})))
    (is (= {1 :b 7 :e} (nu/remove-by-keys even? {2 :a 1 :b 4 :c 6 :d 7 :e})))
    (is (= {:b {:c nil}} (nu/remove-by-keys nil? {nil :a :b {:c nil}})))
    (is (= {} (nu/remove-by-keys string? {"a" 1 "b" 3 "c" 5})))
    (is (= {:a [] :b 5 :c "hello"} (nu/remove-by-keys string? {:a [] :b 5 :c "hello"})))))

(deftest update-vals-test
  (testing "Functional correctness"
    (is (= {:a 2 :b 3} (nu/update-vals {:a 1 :b 2} inc)))
    (is (= {} (nu/update-vals {} dec)))
    (is (= {:b 3 :c 4} (nu/update-vals {:b 1 :c 2} + 2)))))

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
    (is (= {:a {:c {:d 4} :e 5} :d 4} (nu/dissoc-in {:a {:c {:d 4 :f [1 2 3]} :e 5} :d 4} [:a :c :f])))
    (is (= {:a {:b {:d 2}}} (nu/dissoc-in {:a {:b {:c 1 :d 2}}} [:a :b :c])))
    (is (= {:a {:b {}}} (nu/dissoc-in {:a {:b {:c 1}}} [:a :b :c]))))

  (testing "Throws an exception when a non-associative colelction is passed"
    (is (nil? (nu/try-or-nil nu/dissoc-in [:a :b :c] [:a])))))

(deftest ->kebab-keys-test
  (testing "Functional correctness"
    (is (= nil (nu/->kebab-keys nil)))
    (is (= {:foo :bar} (nu/->kebab-keys {:foo :bar})))
    (is (= {:foo-bar 1} (nu/->kebab-keys {:foo-bar 1})))
    (is (= {:foo-bar 1} (nu/->kebab-keys {:fooBar 1})))
    (is (= {:foo-bar 1} (nu/->kebab-keys {:foo_bar 1})))))

(deftest ->snake-keys-test
  (testing "Functional correctness"
    (is (= nil (nu/->snake-keys nil)))
    (is (= {:foo :bar} (nu/->snake-keys {:foo :bar})))
    (is (= {:foo_bar 1} (nu/->snake-keys {:foo_bar 1})))
    (is (= {:foo_bar 1} (nu/->snake-keys {:fooBar 1})))
    (is (= {:foo_bar 1} (nu/->snake-keys {:foo-bar 1})))))

(deftest ->yes-no-test
  (testing "Functional correctness"
    (is (= "Yes" (nu/->yes-no true)))
    (is (= "Yes" (nu/->yes-no {:some "value"})))
    (is (= "Yes" (nu/->yes-no (:some {:some "value"}))))
    (is (= "No"  (nu/->yes-no false)))
    (is (= "No"  (nu/->yes-no (:none {:some "value"}))))
    (is (= "No"  (nu/->yes-no nil)))))

(deftest pluralize-test
  (testing "Functional correctness"
    (is (= "snake" (nu/pluralize "snake" 1)))
    (is (= "snake" (nu/pluralize "snake" 0)))
    (is (= "snakes" (nu/pluralize "snake" 2)))
    (is (= "peach" (nu/pluralize "peach" 1 "peaches")))
    (is (= "peaches" (nu/pluralize "peach" 10 "peaches")))))

(deftest only-test
  (testing "Functional correctness"
    (is (= 1 (nu/only [1])))
    #?(:clj (is (thrown? Exception (nu/only []))))
    #?(:clj (is (thrown? Exception (nu/only [1 2]))))))
