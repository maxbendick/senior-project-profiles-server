(ns senior-project-profiles-server.buyer-matrix
  (:refer-clojure :exclude [* - + / == < <= > >= not= = min max])
  (:require [clojure.core.matrix :refer :all]))

(mmul

  (array [[2 2]
          [3 3]])

  (array [[4 4]
          [5 5]]))

(defn buyer-type
  [props]
  (mmul [] []))

(mmul
      [[10 02 02]
       [01 01 01]
       [01 01 01]]

      [[2]
       [1]
       [1]])
