(ns senior-project-profiles-server.buyer-matrix
  (:refer-clojure :exclude [* - + / == < <= > >= not= = min max])
  (:require [clojure.core.matrix :refer :all]))

(def buyer-matrix [[1 0 1]
                   [1 1 0]
                   [0 1 1]])

;These correspond to columns in the matrix and elements in the buyer props
(def buyer-props ["openness"
                  "conscien"
                  "extraver"])

;These correspond to rows in the matrix
(def buyer-types ["decisive"
                  "consensu"
                  "relation"])

(def buyer {"openness" 1
            "conscien" 1
            "extraver" 0.1})


(defn buyer-to-vec
  [buyer]
  (map (fn [prop] (get buyer prop)) buyer-props))

(defn index-to-buyer-type
  [x]
  (get buyer-types x))

(defn buyer-to-buyer-type
  [buyer]
  (index-to-buyer-type (dominant-index buyer-matrix (buyer-to-vec buyer))))

(defn vec-to-col
  [x]
  (map (fn [xi] [xi]) x))

(defn max-index
  [v]
  (first (apply max-key second (map-indexed vector v))))

(defn dominant-index
  [matrix vector]
  (max-index (flatten (mmul matrix (vec-to-col vector)))))

(buyer-to-buyer-type {"openness" 1
                      "conscien" 1
                      "extraver" 0.1})

;(let [openness-ind 0
;      conscien-ind 1
;      extraver-ind 2;

;      decisive-vec [1 0 1]
;      consensu-vec [1 1 0]
;      relation-vec [0 1 1];

;      buyer-mat [decisive-vec
;                 consensu-vec
;                 relation-vec]

;      buyer-vec (buyer-to-vec buyer)];[1 1 0.1]]

;  (index-to-buyer-type (dominant-index buyer-mat buyer-vec)))

;(let [openness (get buyer "openness")
;        conscien (get buyer "conscien")
;        extraver (get buyer "extraver")]

;[openness conscien extraver]))
