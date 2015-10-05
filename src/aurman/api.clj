(ns aurman.api
  (:gen-class)
  (:require [clojure.data.json :as json])
  (:require [clj-http.client :as http])
  (:require [ring.util.codec :as codec]))

(def aur-host "https://aur.archlinux.org")

(def endpoint (str aur-host "/rpc.php?"))

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

(defn get-pkg
  [pkg]
  (let [{status :status
         body :body
         headers :headers} (http/get 
                            (->> pkg :URLPath (str aur-host)) 
                            {:as :stream})
         [_ filename] (re-find #"filename=(.*)" (get headers "Content-Disposition"))]
    (if (= status 200)
      [filename body]))



















  )
