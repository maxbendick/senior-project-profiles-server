(ns senior-project-profiles-server.orb
  (:require [clj-http.client :as client]))

; Interface with the Orb-intelligence API to get company information.
; API Docs: http://orb-intelligence.com/api/

(def API_ENDPOINT "http://api2.orb-intelligence.com/2/search/companies?api_key=cd748904-268b-4fa0-92b7-a331b6914bc7")

(defn get-company-by-name [name]
  "Return the Orb JSON response for the given company name"
  (client/get (str API_ENDPOINT "&name=" name) {:as :json}))

(defn get-company-by-website [website]
  "Return the Orb JSON response for the given company website, where website is like 'company.com'"
  (client/get (str API_ENDPOINT "&webdomain=" website) {:as :json}))