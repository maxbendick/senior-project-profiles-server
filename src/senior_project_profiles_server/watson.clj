(ns senior-project-profiles-server.watson
  (:require [clj-http.client :as client])
  (:require [clojure.data.json :as json]))

(def API_URL "https://gateway.watsonplatform.net/personality-insights/api/v2/profile")
(def USER "358d1379-52d4-4179-8ff9-b27c594b48bc")
(def PASSWORD "hRs4gERTeSDa")

(defn get-insights [text]
    "Return the JSON object from Watson's personality insights API based on the given text."
    (json/read-str 
        (:body (client/post API_URL
            {:basic-auth [USER PASSWORD]
            :headers {"content-type" "text/plain"}
            :body text
        }))))

(defn parse-insights [insights]
    "Given the map response from Watson, return a map with the big x`5 scores."
    (let [personality (get (into {} (get (into {} (filter #(= (get % "id") "personality") (get (get insights "tree") "children"))) "children")) "children")
          get-percentile (fn [trait jsonblob] (get (into {} (filter #(= (get % "id") trait) jsonblob)) "percentage"))]
        {"Openness" (get-percentile "Openness" personality)
        "conscientiousness" (get-percentile "Conscientiousness" personality)
        "Extraversion" (get-percentile "Extraversion" personality)
        "Agreeableness" (get-percentile "Agreeableness" personality)
        "Neroticism" (get-percentile "Neuroticism" personality)}))

(defn get-big5 [text]
    "Get a map of big5 values representing the given text."
    (parse-insights (get-insights text)))