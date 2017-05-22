(ns senior-project-profiles-server.buyer-matrix
  (:refer-clojure :exclude [* - + / == < <= > >= not= = min max])
  (:require [clojure.core.matrix :refer :all]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

;(comment
;  (def parsed-csv
;    (with-open [in-file (io/reader "src/senior_project_profiles_server/buyermatrix.csv")]
;      (doall
;       (csv/read-csv in-file)))))

(def parsed-csv
  '(["" "Decisive" "Consensus" "Relationship" "Skeptical" "Analytical Al" "Innovator"]
    ["Openness" "50" "75" "75" "15" "50" "75"]
    ["conscientiousness" "15" "75" "50" "75" "75" "25"]
    ["Extraversion" "75" "50" "75" "25" "25" "75"]
    ["Agreeableness" "15" "75" "75" "25" "25" "50"]
    ["Neroticism" "75" "15" "50" "25" "25" "75"]))

(def buyer-matrix
  (transpose (map (fn [row] (map read-string row))
                  (rest (map rest parsed-csv)))))


(def buyer-props
  "The first value of every column except the first.
  These correspond to columns in the matrix and elements in the buyer props"
  (into [] (map first (rest parsed-csv))))


(def buyer-types
  "The first row without the first element.
  These correspond to rows in the matrix."
  (into [] (rest (first parsed-csv))))


(defn buyer-to-vec
  [buyer]
  (map (fn [prop] (get buyer prop)) buyer-props))


(defn index-to-buyer-type
  [x]
  (get buyer-types x))


(defn max-index
  [v]
  (first (apply max-key second (map-indexed vector v))))


(defn vec-to-col
  [x]
  (map (fn [xi] [xi]) x))


(defn dominant-index
  [matrix vector]
  (max-index (flatten (mmul matrix (vec-to-col vector)))))


(defn buyer-to-buyer-type
  [buyer]
  (index-to-buyer-type (dominant-index buyer-matrix (buyer-to-vec buyer))))
