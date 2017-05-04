(ns senior-project-profiles-server.orb
  (:require [clj-http.client :as client]))

; Interface with the Orb-intelligence API to get company information.
; API Docs: http://orb-intelligence.com/api/

(def API_ENDPOINT "http://api2.orb-intelligence.com/2/search/companies?api_key=cd748904-268b-4fa0-92b7-a331b6914bc7")

(defn get-company-by-name [name]
  "Return the Orb JSON response for the given company name"
  (let [resp (:body (client/get (str API_ENDPOINT "&name=" name) {:as :json}))]
    (if (and (= (:code resp) "OK")
            (>= (:result_count resp) 1))
        resp
        nil)))

(defn get-company-by-website [website]
  "Return the Orb JSON response for the given company website, where website is like 'company.com'"
  (let [resp (:body (client/get (str API_ENDPOINT "&webdomain=" website) {:as :json}))]
    (if (and (= (:code resp) "OK")
            (>= (:result_count resp) 1))
        resp
        nil)))

(defn get-relevant-entry [resp]
  "Return the most relevant entry from an Orb JSON response."
  ; Currently just filters out non-null descriptions.
  ; If there is not one entry with a non-null description, returns first entry
  (let [entries (:result_set resp)]
    (let [entries_desc (filter #(not (nil? (:description %))) entries)]
      (if (= (count entries_desc) 0)
          (first entries)
          (if (= (count entries_desc) 1)
              entries_desc
              entries)))))