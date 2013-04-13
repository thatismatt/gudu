(defproject gudu "0.1.0-SNAPSHOT"
  :description "\"Generate URL, Degenerate URL\" - A bi-directional routing and URL generation library."
  :url "http://thatismatt.github.com/gudu"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :plugins [[codox "0.6.4"]]
  :test-selectors {:default (complement :ignore)
                   :all (constantly true)}
  :codox {:exclude gudu.utils
          :src-dir-uri "http://github.com/thatismatt/gudu/blob/master"
          :src-linenum-anchor-prefix "L"})
