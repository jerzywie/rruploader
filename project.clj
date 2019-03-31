(defproject rruploader "0.1.0"
  :description "Simple converter from spreadsheet table to html"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [dk.ative/docjure "1.8.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [hiccup "1.0.4"]
                 [garden "0.1.0-beta6"]]
  :main ^:skip-aot rruploader.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
