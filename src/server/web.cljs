(ns server.web
  (:require
    ["@tinyhttp/app" :as app]
    ["@tinyhttp/logger" :as logger]
    ["fs" :as fs]
    ["path" :as path]
    [cljs-bean.core :refer [bean]]))


(def app (app/App.))

(def document-root "public")

(defn send-file
  [res path]
  (.sendFile res path #js {:root document-root}))


(defn stream-file
  "This keeps FireFox happy"
  [res path]
  (let [content (->> path (str document-root) path/resolve fs/readFileSync)]
    (-> res
        (.type "text/plain")
        (.send content))))


(defn -main
  [_]
  (-> app
      (.use (logger/logger))

      ;; UI
      (.get "/" (fn [_req res] (send-file res "/index.html")))
      (.get "/:file" (fn [req res] (send-file res (.-path req))))
      (.get "/images/:image" (fn [req res] (send-file res (.-path req))))
      (.get "/cljs/:ns" (fn [req res] (stream-file res (.-path req))))


      (.listen 3007 (fn [] (js/console.log (str "Listening on http://localhost:" 3007))))))
