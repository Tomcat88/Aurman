(defproject aurman "0.1.0"
  :description "A simple AUR package downloader"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.0.0"]
                 [me.raynes/fs "1.4.6"]
                 [ring/ring-codec "1.0.0"]]
  :main ^:skip-aot aurman.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
