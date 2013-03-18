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
