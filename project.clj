(defproject nnichols "0.4.0"
            :description "A bunch of functions and definitions I'm sick of copy/pasting"
            :url "https://github.com/nnichols/nnichols"
            :license {:name "MIT"
                      :url "https://opensource.org/licenses/MIT"}
            :dependencies [[camel-snake-kebab "0.4.1"]
                           [org.clojure/clojure "1.10.1"]
                           [org.clojure/clojurescript "1.10.597" :scope "provided"]]

            :plugins [[lein-cljsbuild "1.1.7"]]

            :profiles {:uberjar {:aot :all}
                       :dev {:dependencies [[doo "0.1.11"]]
                             :plugins      [[lein-doo "0.1.10"]]}}

            :aliases {"test-build" ["do" "clean" ["cljsbuild" "once" "test"] ["doo" "once"] ["test"]]}

            :cljsbuild {:builds
                        [{:id "test"
                          :source-paths ["src" "test"]
                          :compiler {:main "nnichols.runner"
                                     :output-to "target/test/app.js"
                                     :output-dir "target/test/js/compiled/out"
                                     :optimizations :none
                                     :parallel-build true}}]}

            :doo {:build "test"
                  :alias {:default [:chrome-headless-no-sandbox]}
                  :paths {:karma "./node_modules/karma/bin/karma"}
                  :karma {:launchers {:chrome-headless-no-sandbox {:plugin "karma-chrome-launcher"
                                                                   :name   "ChromeHeadlessNoSandbox"}}
                          :config    {"captureTimeout"             210000
                                      "browserDisconnectTolerance" 3
                                      "browserDisconnectTimeout"   210000
                                      "browserNoActivityTimeout"   210000
                                      "customLaunchers"            {"ChromeHeadlessNoSandbox"
                                                                    {"base"  "ChromeHeadless"
                                                                     "flags" ["--no-sandbox" "--disable-dev-shm-usage"]}}}}}

            :min-lein-version "2.5.3")
