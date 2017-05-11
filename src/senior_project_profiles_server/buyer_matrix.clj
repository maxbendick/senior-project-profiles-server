(ns senior-project-profiles-server.buyer-matrix
  (:refer-clojure :exclude [* - + / == < <= > >= not= = min max])
  (:require [clojure.core.matrix :refer :all]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(def parsed-csv
  (with-open [in-file (io/reader "src/senior_project_profiles_server/buyermatrix.csv")]
    (doall
      (csv/read-csv in-file))))


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


(defn dominant-index
  [matrix vector]
  (max-index (flatten (mmul matrix (vec-to-col vector)))))


(defn buyer-to-buyer-type
  [buyer]
  (index-to-buyer-type (dominant-index buyer-matrix (buyer-to-vec buyer))))


(defn vec-to-col
  [x]
  (map (fn [xi] [xi]) x))


(defn max-index
  [v]
  (first (apply max-key second (map-indexed vector v))))