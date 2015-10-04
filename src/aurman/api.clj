(ns aurman.api
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [clj-http.client :as http])
  (:require [ring.util.codec :as codec]))

(def aur-host "https://aur.archlinux.org/")

(def endpoint (str aur-host "rpc.php?"))

(def info-endpoint (str endpoint "type=info"))
(def search-endpoint (str endpoint "type=search"))

(defn add-args-param
  [endpoint args]
  (str endpoint "&arg=" args))

(defn get-json
  [url]
  (json/read-str (:body (http/get url)) :key-fn keyword))

(defn get-generic
  [endpoint args]
  (get-json (add-args-param endpoint args)))

(defn get-info
  [id]
  (get-generic info-endpoint id))

(defn get-search
  [query]
  (get-generic search-endpoint query))



















