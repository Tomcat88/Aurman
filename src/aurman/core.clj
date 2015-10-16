(ns aurman.core
  (:gen-class)
  (:use aurman.api)
  (:use aurman.fsops))


(defn results->ip-map 
  [results]
  (reduce (fn [ret pkg]
            (assoc ret (:PackageBaseID pkg) pkg))
          {}
          results))

(defn pretty-print-search
  [results]
  (let [error? (= "error" (:type results))
        res (:results results)]
    (if-let [err (or (and error? res) (and (empty? res) "No package found!"))]
      (println err)
      (do (loop [[{name :Name
                desc :Description
                id :PackageBaseID} & more] res]
         (println id ":" name (str "(" desc ")"))
         (if more (recur more)))
          res))))



(defn aur-search
  [query]
  (pretty-print-search (get-search query)))

(defn aur-install
  [idquery]
  (if (number? (read-string idquery))
    (println (get-info idquery))
    (let [results (-> idquery aur-search results->ip-map)]
      (if (not-empty results)
        (let [id  (do (print "Enter the Package ID to install : ")
                      (flush)
                      (read-string (read-line)))]
          (if (contains? results id)
            (install (get results id))
            (println "Package ID not found!"))))
       )))

(defn -main
  "search query -> shows info for package that match the query
install id -> install the package with that package id
install query -> search for packages that match query and lets you decide which package to install 
  "
  ([] (-> -main
          var
          meta
          :doc
          println))
  ([arg] ; Default op is search if omitted
   (-main "search" arg))
  ([op arg & args]
   (condp = op
     "search" (aur-search arg)
     "install" (aur-install arg))))



















