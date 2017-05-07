(ns senior-project-profiles-server.googleapi
  (:require [clj-http.client :as client]
            [google-apps-clj.google-drive :refer :all]
            [google-apps-clj.credentials :as gauth]))

(def DRIVE_FOLDER "0B3o1bAVuv7uLa09weC1GYVlLVGs")

(defn get-drive-files []
  "Returns a list of {:title :id :export-text} for each file in the Drive folder"
  (let [scopes [com.google.api.services.drive.DriveScopes/DRIVE]
        creds (gauth/default-credential scopes)
        resp (google-apps-clj.google-drive/list-files! creds DRIVE_FOLDER {:pageSize 1000})
        get-wanted-info (fn [info] {:title (:title info), :id (:id info), :export-text (:text/plain (:export-links info))})]
    (map get-wanted-info resp)))


(defn get-gdoc-body [resp]
  "Returns the body of the given http response as a string"
  (as-> resp input
    (str input)
    (re-find #":body (.*?), :|:body (.*?)}" input)
    (remove nil? input)
    (nth input 1)
    (clojure.string/replace input #"\\r\\n" "\n")
    (clojure.string/replace input #"\\\"" "\"")))
