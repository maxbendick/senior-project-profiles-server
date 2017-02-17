(ns senior-project-profiles-server.core
  (:require [clojure.string :as str]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]))

(use 'ring.middleware.content-type)


(defmacro make-person
  "Constructs a person structure"
  [name twitter]
  {:name name
   :twitter-handle twitter})

(def people [(make-person "max" "maxbendick")
             (make-person "logan" "loganwilliams")
             (make-person "nick" "")
             (make-person "jose" "")
             (make-person "krissy" "")
             (make-person "david" "")])


(defroutes app-routes
  (GET "/" [] "Hello World")

  (GET "/profile/:id" [id]
       {:body (get people (read-string id))})

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


  "To load in the repl"
   (use 'senior-project-profiles-server.core :reload)

)
