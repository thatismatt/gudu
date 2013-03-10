(ns gudu.test
  (:use clojure.test
        gudu))

(deftest one-static-url
  (let [routes-1 {:home ["home"]
                  :sub  ["a" "b"]}
        my-gu (gu routes-1)
        my-du (du routes-1)]
    (testing "gu"
      (is (= (my-gu :home) "/home"))
      (is (= (my-gu :sub) "/a/b")))
    (testing "du"
      (is (= (my-du "/home") :home))
      (is (= (my-du "/a/b")  :sub)))))

;; TODO
;;  - trailing slash
;;  - root
