(ns wsp.socket-handlers
  (:require [ring.adapter.jetty9 :as j9])
  (:import (java.util UUID)))

(defonce connected-clients (atom {}))

(defrecord Client [ws id])

(defn on-connect [ws]
  (let [client (->Client ws (UUID/randomUUID))]
    (swap! connected-clients assoc ws client)
    (j9/send! ws (str "you are - " (:id client)))
    (prn ::on-connect ws)))

(defn on-error [ws e]
  ;; on-close is also called
  (prn ::err e))

(defn on-close [ws status-code reason]
  (swap! connected-clients dissoc ws)
  (prn ::clos status-code reason))

(defn on-text [ws text-message]
  (let [{:keys [ws id] :as thing} (get @connected-clients ws)]
    (prn ws)
    (prn (= (str id) (str text-message))))
  (try
    (j9/send! ws (str text-message))
    (catch Throwable err
      (prn ::Err err))))

(defn on-bytes [ws bytes offset len]
  (prn ::ob (slurp bytes) offset len))

(comment
  (deref connected-clients)
  (doseq [[ws {:keys [ws id]}] @connected-clients]
    (on-text ws (System/currentTimeMillis))))
