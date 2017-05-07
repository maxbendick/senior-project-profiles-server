(ns senior-project-profiles-server.twitter
  (:use [twitter.oauth]
        [twitter.callbacks]
        [twitter.callbacks.handlers]
        [twitter.api.restful])
  (:import [twitter.callbacks.protocols SyncSingleCallback])
  (:require [clj-http.client :as client]))

(def my-creds (make-oauth-creds *app-consumer-key*
                                *app-consumer-secret*
                                *user-access-token*
                                *user-access-token-secret*))

; https://github.com/adamwynne/twitter-api

(defn get-tweets [handle]
    "Return the tweets from the username 'handle'."
    handle)
