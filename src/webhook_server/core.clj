(ns webhook-server.core
  (:use webhook-server.server)
  (:gen-class))

(defn -main
  "Start the running server"
  [& args]
  (start-server)
  (println "Server accepting connections..."))
