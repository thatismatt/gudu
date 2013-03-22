(defproject gudu "0.1.0-SNAPSHOT"
  :description "Generate URL, Degenerate URL"
  :url "http://thatismatt.github.com/gudu"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :test-selectors {:default (complement :ignore)
                   :all (constantly true)})
