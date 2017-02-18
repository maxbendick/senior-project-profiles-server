(ns senior-project-profiles-server.core
  (:require [clojure.string :as str]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]))

(use 'ring.middleware.content-type)


(let [id (atom 0)]
  (defn add-id [a]
    "Adds an :id to a map"
    (assoc a :id (swap! id inc))))

(defn make-person
  "Constructs a person structure"
  [name twitter]
  (add-id {:name name :twitter-handle twitter}))


(def people [(make-person "max" "maxbendick")
             (make-person "logan" "loganwilliams")
             (make-person "nick" "nicksinai")
             (make-person "jose" "josecabrera")
             (make-person "krissy" "krissywitous")
             (make-person "david" "davidcornella")])

(defn find-person
  "Finds the person with a matching :id in people"
  [id]
  (first (filter #(= (:id %) id) people)))


(defroutes app-routes
  (GET "/" [] "Hello World")

  (GET "/profile/:id" [id]
       (let [person (find-person (read-string id))]
         (if person
           {:body person}
           {:status 404 :body "Not found"})))

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
