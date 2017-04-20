(ns senior-project-profiles-server.markdown-processor
  (:require [instaparse.core :as insta]))

(def parser
  (insta/parser
    "<profile>  = sexp+
     <sexp>     = space (space lparen operation rparen space) space
     <lparen> = <'('>
     <rparen> = <')'>
     <space>  = <#'[ ]*'> | '\n'
     <ssexp>  = space sexp space

     name = <'name'> string
     twitter = <'twitter'> string
     position = <'position'> string
     company = <'company'> string
     card = <'card'> string markdown

     <operation> = name | twitter | position | company | card

     <markdown> = space <'{'> #'[^}]+' <'}'> space

     <string> = space <'\"'> #'[^\"]+' <'\"'> space

     "))

;<#'[ ]*'> | <#'\\s+'> #'\\s+'

(defn get-one-arg [ast key]
  (->> ast
      (filter #(= (first %) key))
      (first)
      (second)))

(defn cards-from-ast [ast]
  (->> ast
       (filter #(= (first %) :card))
       (map (fn [e] {:title   (get e 1)
                  :content (get e 2)}))))

(defn profile-from-ast [ast]
  (let [get (fn [k] (get-one-arg ast k))]
    {:name     (get :name)
     :twitter  (get :twitter)
     :position (get :position)
     :company  (get :company)
     :cards    (cards-from-ast ast)}))

(defn compile-xmarkdown [s]
  (let [p (parser s)]
    (if (insta/failure? p)
      {:left p}
      {:right (profile-from-ast p)})))
