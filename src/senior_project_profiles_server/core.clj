(ns senior-project-profiles-server.core
  (:require [clojure.string :as str]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [google-apps-clj.google-drive :refer :all]
            [google-apps-clj.credentials :as gauth]
            [clj-http.client :as client]))

(use 'ring.middleware.content-type)

(def DRIVE_FOLDER "0B3o1bAVuv7uLa09weC1GYVlLVGs")
(def NOT_FOUND_404 {:status 404 :headers {"Content-Type" "text/html; charset=utf-8"} :body "Not found"})


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


(defn get-files []
  "Returns a list of {:title :id :export-text} for each file in the Drive folder"
  (let [scopes [com.google.api.services.drive.DriveScopes/DRIVE]
        creds (gauth/default-credential scopes)
        resp (google-apps-clj.google-drive/list-files! creds DRIVE_FOLDER {:pageSize 1000})
        get-wanted-info (fn [info] {:title (:title info), :id (:id info), :export-text (:text/plain (:export-links info))})]
    (map get-wanted-info resp)))

(defn get-body [resp]
  "Returns the body of the given http response as a string"
  (as-> resp input
    (str input)
    (re-find #":body (.*?), :|:body (.*?)}" input)
    (remove nil? input)
    (nth input 1)
    (clojure.string/replace input #"\"" "")))


(defroutes app-routes
  (GET "/" [] "Welcome to Vertible!")

  (GET "/person/:id" [id]
       (let [person (find-person (read-string id))]
         (if person
           {:body person}
           NOT_FOUND_404)))

  (GET "/profile/:name" [name] ; where name is the filename as it appears in Drive
    (as-> (get-files) input
      (first (filter #(= (:title %) name) input))
      (if input
        {:body (get-body (client/get (:export-text input)))} ; can't use {:as :clojure} because it cuts off part of the body
        NOT_FOUND_404)))

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
