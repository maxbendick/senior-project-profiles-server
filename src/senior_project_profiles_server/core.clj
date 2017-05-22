(ns senior-project-profiles-server.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [clojure.java.io :as io]
            [senior-project-profiles-server.orb :refer [company-card]]
            [senior-project-profiles-server.twitter :refer [get-tweet-blob]]
            [senior-project-profiles-server.watson :refer [get-big5]]
            [senior-project-profiles-server.buyer-matrix :refer [buyer-to-buyer-type]]))

(use 'ring.middleware.content-type)

(def NOT_FOUND_404 {:status 404})

(defn add-card [response card]
  "Adds a new card to the response (creates the cards list if it doens't exist)."
  ; position of new card isn't guaranteed b/c of 'conj'
  (let [new-cards-list (into [] (conj (:cards response) card))]
    (assoc response :cards new-cards-list)))

(defn add-cards [response cards]
  "Appends the cards to the response."
  (reduce add-card response cards))

(defn text-to-buyer-type [text]
  "Given a string, returns the buyer type of the person who wrote it."
  (buyer-to-buyer-type (get-big5 text)))


(defn twitter-to-big5 [handle]
  "Given a twitter handle, returns a map of the big 5 trait scores for that tweeter."
  (get-big5 (get-tweet-blob handle)))


(defroutes app-routes
  (GET "/" [] "Welcome to Vertible!")

  (GET "/profile/" {params :params}
    ; TODO:
    ;  - handle the case when no params are given
    ;  - handle the case when a nonexistent/non-public twitter handle is given
    {:body {:right (add-cards params [{:title "Buyer Type" :content (buyer-to-buyer-type (twitter-to-big5 (:twitter params)))}
                                      (company-card (:company params))])}})

  (GET "/test-twitter/:handle" [handle]
    {:body {:buyer-type (buyer-to-buyer-type (twitter-to-big5 handle))
            :ocean-scores (twitter-to-big5 handle)
            :twitter-handle handle}})

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

  (app-routes {:request-method :get
        :uri "/profile"})
  
  "To load in the repl"
   (use 'senior-project-profiles-server.core :reload)

)
