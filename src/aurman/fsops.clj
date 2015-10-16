(ns aurman.fsops
  (:gen-class)
  (:use aurman.api)
  (:require [me.raynes.fs :as fs]
            [me.raynes.fs.compression :as compr])
  (:use [clojure.java.shell :only [sh]]
        [clojure.java.io :only (as-file)]))

(def pkg-dir "/tmp")
(def runtime (. Runtime getRuntime))

(defn as-buffered-reader
  [stream]
  (java.io.BufferedReader. (java.io.InputStreamReader. stream)))

(defn save-file
  [filename bytes]
  (clojure.java.io/copy bytes (java.io.File. filename)))

(defn exec
  [cmd dir]
  (let [p (.exec (Runtime/getRuntime) cmd nil (as-file dir))
        in (.getInputStream p)
        err (.getErrorStream p)]
    (map as-buffered-reader [in err])))

(defn print-output
  [in err]
  (loop [line (. in readLine)
         eline (.readLine in)]
    (if-not (and (nil? line) (nil? eline))
      (do (println line)
          (println eline)
          (flush)
          (recur (. in readLine)
                 (.readLine err))))))

(defn exec-print
  [cmd dir]
  (apply print-output 
         (exec cmd dir)))


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
    (fs/delete target)))

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
      (exec-print "makepkg --noconfirm -s" target)
      #_(let [{error :err 
             out :out 
             exit :exit} (sh 
                          "makepkg" "--noconfirm" "-s" 
                          :dir target)]
        (if (= exit 1)
          (println error)
          (println out))))))











