(defproject gudu "0.2.0-SNAPSHOT"
  :description "\"Generate URL, Degenerate URL\" - A bi-directional routing and URL generation library."
  :url "http://thatismatt.github.com/gudu"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :plugins [[lein-codox "0.10.8"]]
  :test-selectors {:default (complement :ignore)
                   :all     (constantly true)}
  :codox {:exclude                   gudu.utils
          :src-dir-uri               "http://github.com/thatismatt/gudu/blob/master"
          :src-linenum-anchor-prefix "L"
          :output-dir                "site/doc"})
