(ns gudu.test
  (:use clojure.test
        gudu))

(deftest test-static-routes
  (let [static-routes {:home   []
                       :blog   ["blog"]
                       :sub    ["a" "b"]
                       :subsub ["a" "b" "c"]}
        my-gu (gu static-routes)
        my-du (du static-routes)]
    (testing "gu"
      (is (= (my-gu :home)   "/"))
      (is (= (my-gu :blog)   "/blog"))
      (is (= (my-gu :sub)    "/a/b"))
      (is (= (my-gu :subsub) "/a/b/c")))
    (testing "du"
      (is (= (my-du "/")      :home))
      (is (= (my-du "/blog")  :blog))
      (is (= (my-du "/a/b")   :sub))
      (is (= (my-du "/a/b/c") :subsub)))))

;; TODO
;;  - trailing slash
