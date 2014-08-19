;;;-------------------------------------------------------------------------------------------------
;;; Copyright Alan Dipert, Micha Niskin, & jumblerg. All rights reserved. The use and distribution
;;; terms for this software are covered by the Eclipse Public License 1.0 (http://www.eclipse.org/
;;; legal/epl-v10.html). By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.  You must not remove this notice, or any other, from this software.
;;;-------------------------------------------------------------------------------------------------

(ns boot.task.tomcat
  (:require
    [clojure.java.io         :as io]
    [tailrecursion.boot.core :as c] )
  (:import
    [org.apache.catalina.startup Tomcat] ))

;;; state ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def server (atom nil))

;;; utilities ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn extract-ids [sym]
  (let [[group artifact] ((juxt namespace name) sym)]
    [(or group artifact) artifact] ))

(defn filename [aid vers ext] (str (if aid (str aid "-" vers) "project") "." ext)) ;; should use boot tmpdir

;;; private ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create [app-file port join?]
  (let [srv-path (c/mktmpdir! ::webapp)
        join     #(if join? (do (.await (.getServer %)) %) %)
        start    #(doto %
                    (.setBaseDir (.getAbsolutePath srv-path))
                    (.setPort port)
                    (.addWebapp "" (.getAbsolutePath app-file))
                    (.start) ) ]
    (.mkdirs (io/file srv-path "webapps"))
    (-> (Tomcat.) start join) ))

(defn destroy [^Tomcat server]
  (doto server .stop .destroy) )

;;; public ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(c/deftask tomcat
  "Boot task to create a standalone Tomcat server.

  The `:port` option specifies which port the server should listen on (default
  8000). The `:join?` option specifies whether Jetty should run in the foreground
  (default false)."
  [& {:keys [port join?] :or {port 8000 join? false}}]
  (println  "Starting Tomcat server...")
  (let [aid  (second (extract-ids (c/get-env :project)))
        file (io/file (c/get-env :dst-path) (filename aid (c/get-env :version) "war")) ]
    (c/with-post-wrap
      (swap! server #(do (if % (destroy %)) (create file port join?)))  )))
