(ns gudu.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [gudu.core :as gd]
            [gudu.segment :as gds]))

(deftest test-static-routesgds
  (let [static-routes {:home   gds/root
                       :blog   "blog"
                       :sub    ["a" "b"]
                       :subsub ["a" "b" "c"]
                       :kw     :w
                       :subkw  [:x :y :z]
                       :mixed  [:e "f" :g]}
        my-gu         (gd/gu static-routes)
        my-du         (gd/du static-routes)]
    (testing "gu"
      (is (= (my-gu :home)   "/"))
      (is (= (my-gu :blog)   "/blog"))
      (is (= (my-gu :sub)    "/a/b"))
      (is (= (my-gu :subsub) "/a/b/c"))
      (is (= (my-gu :kw)     "/w"))
      (is (= (my-gu :subkw)  "/x/y/z"))
      (is (= (my-gu :mixed)  "/e/f/g")))
    (testing "du"
      (is (= (my-du "/")      [:home]))
      (is (= (my-du "/blog")  [:blog]))
      (is (= (my-du "/a/b")   [:sub]))
      (is (= (my-du "/a/b/c") [:subsub]))
      (is (= (my-du "/w")     [:kw]))
      (is (= (my-du "/x/y/z") [:subkw]))
      (is (= (my-du "/e/f/g") [:mixed])))
    (testing "missing"
      (is (nil? (my-du "/missing")))
      (is (nil? (my-du "/blog/missing"))))
    (testing "trailing slash"
      (is (= (my-du "/blog/") [:blog]))
      (is (= (my-du "/a/b/")  [:sub])))))

(deftest test-sub-routes
  (let [blog-routes {:current gds/root
                     :archive "archive"}
        sub-routes  {:home    gds/root
                     :blog    ["blog" blog-routes]}
        my-gu (gd/gu sub-routes)
        my-du (gd/du sub-routes)]
    (testing "gu"
      (is (= (my-gu :blog)          "/blog"))
      (is (= (my-gu :blog :current) "/blog"))
      (is (= (my-gu :blog :archive) "/blog/archive")))
    (testing "du"
      (is (= (my-du "/")             [:home]))
      (is (= (my-du "/blog")         [:blog :current]))
      (is (= (my-du "/blog/archive") [:blog :archive])))))

(deftest test-int-segments
  (let [int-routes {:a gds/int
                    :b {:d "d"
                        :e [gds/int "e"]
                        :f ["f" gds/int]}}
        my-gu (gd/gu int-routes)
        my-du (gd/du int-routes)]
    (testing "gu"
      (is (= (my-gu :a 1)    "/1"))
      (is (= (my-gu :b :d)   "/d"))
      (is (= (my-gu :b :e 1) "/1/e"))
      (is (= (my-gu :b :f 1) "/f/1")))
    (testing "du"
      (is (= (my-du "/1")   [:a 1]))
      (is (= (my-du "/d")   [:b :d]))
      (is (= (my-du "/1/e") [:b :e 1]))
      (is (= (my-du "/f/1") [:b :f 1])))
    (testing "missing"
      (is (nil? (my-du "/not-int")))
      (is (nil? (my-du "/not-int/e")))
      (is (nil? (my-du "/f/not-int"))))))

(deftest test-string-segments
  (let [string-routes {:r gds/root
                       :a gds/string
                       :b {:e [gds/string "e"]
                           :f ["f" gds/string]}}
        my-gu (gd/gu string-routes)
        my-du (gd/du string-routes)]
    (testing "gu"
      (is (= (my-gu :r)           "/"))
      (is (= (my-gu :a "matt")    "/matt"))
      (is (= (my-gu :b :e "matt") "/matt/e"))
      (is (= (my-gu :b :f "matt") "/f/matt")))
    (testing "du"
      (is (= (my-du "/")       [:r]))
      (is (= (my-du "/matt")   [:a "matt"]))
      (is (= (my-du "/matt/e") [:b :e "matt"]))
      (is (= (my-du "/f/matt") [:b :f "matt"])))))
