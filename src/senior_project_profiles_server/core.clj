(ns senior-project-profiles-server.core
  (:require [clojure.string :as str]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [resource-response content-type redirect]]
            [clj-http.client :as client]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [senior-project-profiles-server.markdown-processor :refer [compile-xmarkdown]]
            [senior-project-profiles-server.orb :refer [get-workplace-info]]
            [senior-project-profiles-server.googleapi :refer [get-drive-files get-gdoc-body]]
            [senior-project-profiles-server.orb :refer [company-card]]
            [senior-project-profiles-server.twitter :refer [get-tweet-blob]]
            [senior-project-profiles-server.watson :refer [get-big5]]))

(use 'ring.middleware.content-type)

(def NOT_FOUND_404 {:status 404}) ; :headers {"Content-Type" "text/html; charset=utf-8"} :body "Not found"})


(defn add-card [response card]
  "Adds a new card to the response (creates the cards list if it doens't exist)."
  ; position of new card isn't guaranteed b/c of 'conj'
  (let [new-cards-list (into [] (conj (:cards response) card))]
    (assoc response :cards new-cards-list)))


(defn twitter-to-big5 [handle]
  "Given a twitter handle, returns a map of the big 5 trait scores for that tweeter."
  (get-big5 (get-tweet-blob handle)))


(defroutes app-routes
  (GET "/" [] "Welcome to Vertible!")

  (GET "/gprofile/:name" [name] ; where name is the filename as it appears in Drive
    (as-> (get-drive-files) input
      (first (filter #(= (:title %) name) input))
      (if input
        {:body (as-> input x
                   (:export-text x)
                   (client/get x)
                   (get-gdoc-body x)
                   (subs x 2 (- (count x) 1))
                   (compile-xmarkdown x))}; can't use {:as :clojure} because it cuts off part of the body
        NOT_FOUND_404)))

  (GET "/profile/" {params :params}
    ; TODO: 
    ;  - do analysis and return the results for the profile
    ;  - handle the case when no params are given
    {:body {:right (add-card params (company-card (:company params)))}})

  (GET "/app/*" [] (io/resource "public/index.html"))

  (route/not-found "Not found"))


(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-content-type)
      (wrap-defaults site-defaults)))



(comment
  "Here's an example request test"
  (=
    (app-routes {:request-method :get :uri "/"})

    {:status 200, :headers {"Content-Type" "text/html; charset=utf-8"}, :body "Hello World"})


  (app-routes {:request-method :get :uri "/gprofile/TemplateFormat"})
  (app-routes {:request-method :get :uri "/profile?name=Bob Smith&company=Google&twitter=potus"})

  "To load in the repl"
   (use 'senior-project-profiles-server.core :reload)

)
