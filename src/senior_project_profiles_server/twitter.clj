(ns senior-project-profiles-server.twitter
   (:use [twitter.oauth]
         [twitter.callbacks]
         [twitter.callbacks.handlers]
         [twitter.api.restful])

  (:import [twitter.callbacks.protocols SyncSingleCallback])

  (:require ;[twitter.oauth :refer [make-oauth-creds]]
            ;[twitter.api.restful :refer [get-tweets]]
            [clojure.string :as str]))

  ;(:require [clj-http.client :as client]))

; https://github.com/adamwynne/twitter-api

(def CONSUMER_KEY "qUY8nhHgn1ZadBKiIlge36fDR")
(def CONSUMER_SECRET "sFiCzyAkP8VvQgRwBt9Qh8saj2IlKldTi103SgJRPSvDe8y01R")
(def ACCESS_TOKEN "830950575189610496-Ha2m186OtAs25KXZpAHpEoeXrkt4h5L")
(def ACCESS_TOKEN_SECRET "gipc6GI2jjGUEMKNNypqwBpCvvGuged4ULVFusnepUaBH")
(def creds (make-oauth-creds CONSUMER_KEY
                             CONSUMER_SECRET
                             ACCESS_TOKEN
                             ACCESS_TOKEN_SECRET))

(defn get-tweets [handle]
  "Return the most recent tweets (up to 200) from the username 'handle'."
  (statuses-user-timeline :oauth-creds creds :params {:screen-name handle :count 200}))

(defn get-tweet-blob [handle]
  "Returns a single string of all the most recent tweets for username 'handle'."
  (str/join " " (map #(:text %) (:body (get-tweets handle)))))
