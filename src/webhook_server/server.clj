(ns webhook-server.server
  (:require [clojure.tools.logging :as log])
  (:use [compojure.route :only [files not-found]]
        [compojure.core :only [defroutes GET POST context]]
        [clojure.data.json :as json]
        [ring.middleware.defaults :only [api-defaults site-defaults wrap-defaults]]
        org.httpkit.server))

(defonce server (atom nil))

(defonce last-post (atom nil))

(defn hook-event [req]
  (let [data (json/read (-> req clojure.java.io/reader))]
    (reset! last-post data)
    (log/info (str "hook: " (json/write-str data)))
    {:status 200 :heders {"Content-Type" "application/json"} :body (json/write-str data)}
    ))

(defn show-last-webhook [_]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (json/write-str @last-post)})

(defroutes all-routes
  (GET "/" [] show-last-webhook)
  (POST "/" {body :body} (hook-event body)))

(defn site [handler]
  (wrap-defaults handler api-defaults))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (reset! server (run-server (site #'all-routes) {:port 9100})))
