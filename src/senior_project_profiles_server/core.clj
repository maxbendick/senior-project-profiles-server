(ns senior-project-profiles-server.core
  (:require [clojure.string :as str]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [google-apps-clj.google-drive :refer :all]
            [google-apps-clj.credentials :as gauth]
            [clj-http.client :as client]
            [senior-project-profiles-server.markdown-processor :refer [compile-xmarkdown]]))

(use 'ring.middleware.content-type)

(def DRIVE_FOLDER "0B3o1bAVuv7uLa09weC1GYVlLVGs")
(def NOT_FOUND_404 {:status 404 :headers {"Content-Type" "text/html; charset=utf-8"} :body "Not found"})


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

  (GET "/profile/:name" [name] ; where name is the filename as it appears in Drive
    (as-> (get-files) input
      (first (filter #(= (:title %) name) input))
      (if input
        {:body (compile-xmarkdown (get-body (client/get (:export-text input))))} ; can't use {:as :clojure} because it cuts off part of the body
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


  (app-routes {:request-method :get :uri "/profile/JimBobGoogle"})

  "To load in the repl"
   (use 'senior-project-profiles-server.core :reload)

)
