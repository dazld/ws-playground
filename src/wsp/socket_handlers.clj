(ns wsp.socket-handlers
  (:require [ring.adapter.jetty9 :as j9]))

(def connected-clients (atom #{}))

(defn on-connect [ws]
  (swap! connected-clients conj ws)
  (prn ::on-connect ws))

(defn on-error [ws e]
  (prn ::err e))

(defn on-close [ws status-code reason]
  (swap! connected-clients disj ws)
  (prn ::clos status-code reason))

(defn on-text [ws text-message]
  (prn ::ot text-message)
  (try
    (j9/send! ws (str text-message))
    (catch Throwable err
      (prn ::Err err))))

(defn on-bytes [ws bytes offset len]
  (prn ::ob (slurp bytes) offset len))

(comment
  (doseq [ws @connected-clients]
    (prn (on-text ws (System/currentTimeMillis)))))
