(ns senior-project-profiles-server.orb
  (:require [clj-http.client :as client]))

; Interface with the Orb-intelligence API to get company information.
; API Docs: http://orb-intelligence.com/api/

(def API_ENDPOINT "http://api2.orb-intelligence.com/2/search/companies?api_key=cd748904-268b-4fa0-92b7-a331b6914bc7")


(defn get-orb-company-by-name [name]
  "Return the Orb JSON response for the given company name"
  (let [resp (:body (client/get (str API_ENDPOINT "&name=" name) {:as :json}))]
    (if (and (= (:code resp) "OK")
            (>= (:result_count resp) 1))
        resp
        nil)))


(defn get-orb-company-by-website [website]
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
              (first entries))))))


(defn get-workplace-info [company]
  "Return a map containing info about the given company from the Orb API."
  (into {} (get-relevant-entry (into {} (get-orb-company-by-name company)))))


(defn get-pretty-address [address]
  "Returns a nicely formatted string representation of the address map."
  ; TODO: include address2, check for nulls, and support international addresses
  (str "</br>&emsp;" (:address1 address)
      "</br>&emsp;" (:city address) ", " (:state address) ", " (:zip address)))


(defn get-social-accounts [orb-info]
  "Returns a list of social media and links for the given orb-info."
  ; assumes all social media info falls under a "*_account" key in the orb-info JSON
  (let [socials (filter #(clojure.string/ends-with? (str (key %)) "_account") orb-info)]
    (into {} (filter #(not (nil? (:url (val %)))) socials))))


(defn clean-link [title]
  "Removes preceding colon and everything including and after '_account'."
  ; e.g. (clean-link ":linkedin_account") => "linkedin"
  (subs (str title) 1 (clojure.string/index-of title "_account")))


(defn company-card [company]
  "Creates a new card (map containing title and content) for company info."
  ; TODO: 
  ;  - Shorten description (or make expandable)
  ;  - Capitalize social media links
  ;  - Maybe include some of the most prominent 'categories' since industry is sometimes pretty vague    
  ;  - include general contact info (phone, email, fax)
  (let [orb-info (get-workplace-info company)]
    {:title "Company" :content (str "**Description:**\n" (:description orb-info)
                                 "\n\n**Industry:** " (:industry orb-info)
                                 "\n\n**Revenue Range:** " (:revenue_range orb-info)
                                 "\n\n**Employees:** " (:employees orb-info)
                                 "\n\n**Technologies:**\n" (clojure.string/join "\n" (map #(str "* " (:name %)) (:technologies orb-info)))
                                 "\n\n**Year Founded:** " (:year_founded orb-info)
                                 "\n\n**Address:** " (get-pretty-address (:address orb-info))
                                 "\n\n**Website:** [" (:website orb-info) "](" (:website orb-info) ")"
                                 "\n\n**Social Media Accounts:**\n " (clojure.string/join "\n" (map #(str "* [" (clean-link (key %)) "](" (:url (val %)) ")") (get-social-accounts orb-info)))
                              )}))