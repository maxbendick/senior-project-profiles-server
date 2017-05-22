(defproject senior-project-profiles-server "0.1.0-SNAPSHOT"
  :description "Vertible"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.6.1"]
                 [javax.servlet/servlet-api "2.5"]
                 [ring/ring-defaults "0.2.3"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.5.2"]
                 [google-apps-clj "0.6.1"]
                 [clj-http "3.4.1"]
                 [twitter-api "0.7.9"]
                 [net.mikera/core.matrix "0.59.0"]
                 [org.clojure/data.csv "0.1.3"]]
  
  :plugins [[lein-ring "0.12.0"]
            [lein-beanstalk "0.2.7"]]

  :ring {:handler senior-project-profiles-server.core/app})
