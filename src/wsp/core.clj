(ns wsp.core
  (:require [ring.adapter.jetty9 :as j9]
            [wsp.socket-handlers :as wsh]))

(defonce server! (atom nil))

(def ws-handler {:on-connect #'wsh/on-connect
                 :on-error   #'wsh/on-error
                 :on-close   #'wsh/on-close
                 :on-text    #'wsh/on-text
                 :on-bytes   #'wsh/on-bytes})

(defn handler* [request]
  (clojure.pprint/pprint request)
  {:status 200
   :body   (slurp "resources/public/index.html")})

(defn handler [request]
  (handler* request))

(defn run []
  (when-let [server @server!]
    (j9/stop-server server))
  (reset! server! (j9/run-jetty handler {:port       8001
                                         :join?      false
                                         :websockets {"/ws" ws-handler}})))

(comment
  (deref server!)
  (run))
