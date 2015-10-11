(ns aurman.core
  (:gen-class)
  (:use aurman.api)
  (:require [me.raynes.fs :as fs]
            [me.raynes.fs.compression :as compr])
  (:use [clojure.java.shell :only [sh with-sh-dir]]))

(def pkg-dir "/tmp")

(defn save-file
  [filename bytes]
  (clojure.java.io/copy bytes (java.io.File. filename)))

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

(defn extract
  [filename filepath outdir]
  (let [target (str outdir
                    "/"
                    (clojure.string/replace filename #"\.gz$" ""))
        makepkg-dir (clojure.string/replace target #"\.tar$" "")]
    (println "Extracting " filepath "into" target)
    (compr/gunzip filepath target)
    (println "Extracting " target "into" outdir)
    (compr/untar target outdir)
    (println "Deleting " target)
    (fs/delete target)
   
))

(defn install
  [pkg]
  (do
    ;(println pkg)
    (let [[f bytes] (get-pkg pkg)
           filepath (str pkg-dir "/" f)
           target (clojure.string/replace filepath #"\.tar\.gz$" "/")]
      (println filepath)
      (save-file filepath bytes)
      (extract f filepath pkg-dir)
      (let [{error :err 
             out :out 
             exit :exit} (sh 
                          "makepkg" "--noconfirm" "-s" 
                          :dir target)]
        (if (= exit 1)
          (println error)
          (println out))))))

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





















