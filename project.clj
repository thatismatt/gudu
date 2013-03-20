(defproject gudu "0.1.0-SNAPSHOT"
  :description "Generate URL, Degenerate URL"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]]
  :test-selectors {:default (complement :ignore)
                   :all (constantly true)})
