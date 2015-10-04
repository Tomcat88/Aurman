(ns aurman.core
  (:gen-class)
  (:use aurman.api))


(defn pretty-print-search
  [results]
  (let [error? (= "error" (:type results))
        res (:results results)]
    (if error?
      (println res)
      (loop [[{name :Name
               desc :Description
               id :PackageBaseID} & more] res]
        (println id ":" name (str "(" desc ")"))
        (if more (recur more))))))

(defn aur-search
  [query]
  (pretty-print-search (get-search query)))

(defn aur-install
  [idquery]
  (if (number? (read-string idquery))
    (println (get-info idquery))
    (println (get-search idquery))))

(defn -main
  "
  search query -> shows info for package that match the query
  install id -> install the package with that package id
  install query -> search for packages that match query and lets you decide which package to install 
  "
  ([arg] ; Default op is search if omitted
   (-main "search" arg))
  ([op arg & args]
   (condp = op
     "search" (aur-search arg)
     "install" (aur-install arg))))





















