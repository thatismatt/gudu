(ns gudu.test
  (:use clojure.test
        gudu))

(deftest test-static-routes
  (let [static-routes {:home   []
                       :blog   ["blog"]
                       :sub    ["a" "b"]
                       :subsub ["a" "b" "c"]
                       :sym    [:w]
                       :subsym [:x :y :z]
                       :mixed  [:e "f" :g]}
        my-gu (gu static-routes)
        my-du (du static-routes)]
    (testing "gu"
      (is (= (my-gu :home)   "/"))
      (is (= (my-gu :blog)   "/blog"))
      (is (= (my-gu :sub)    "/a/b"))
      (is (= (my-gu :subsub) "/a/b/c"))
      (is (= (my-gu :sym)    "/w"))
      (is (= (my-gu :subsym) "/x/y/z"))
      (is (= (my-gu :mixed)  "/e/f/g")))
    (testing "du"
      (is (= (my-du "/")      [:home]))
      (is (= (my-du "/blog")  [:blog]))
      (is (= (my-du "/a/b")   [:sub]))
      (is (= (my-du "/a/b/c") [:subsub]))
      (is (= (my-du "/w")     [:sym]))
      (is (= (my-du "/x/y/z") [:subsym]))
      (is (= (my-du "/e/f/g") [:mixed])))
    (testing "missing"
      (is (nil? (my-du "/missing")))
      (is (nil? (my-du "/blog/missing"))))
    (testing "trailing slash"
      (is (= (my-du "/blog/") [:blog]))
      (is (= (my-du "/a/b/")  [:sub])))))

(deftest test-sub-routes
  (let [blog-routes {:current []
                     :archive ["archive"]}
        sub-routes  {:home    []
                     :blog    ["blog" blog-routes]}
        my-gu (gu sub-routes)
        my-du (du sub-routes)]
    (testing "gu"
      (is (= (my-gu :blog)          "/blog"))
      (is (= (my-gu :blog :current) "/blog"))
      (is (= (my-gu :blog :archive) "/blog/archive")))
    (testing "du"
      (is (= (my-du "/")             [:home]))
      (is (= (my-du "/blog")         [:blog :current]))
      (is (= (my-du "/blog/archive") [:blog :archive])))))

(deftest test-int-segments
  (let [int-routes {:a [int-segment]
                    :b [{:c [int-segment]
                         :d ["d"]
                         :e [int-segment "e"]
                         :f ["f" int-segment]}]}
        my-gu (gu int-routes)
        my-du (du int-routes)]
    (testing "gu"
      (is (= (my-gu :a 1)    "/1"))
      (is (= (my-gu :b :c 1) "/1"))
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
