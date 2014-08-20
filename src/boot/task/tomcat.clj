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

;;; private ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create [base-dir war-file port join?]
  (let [join  #(if join? (do (.await (.getServer %)) %) %)
        start #(doto %
                (.setBaseDir (.getAbsolutePath base-dir))
                (.setPort port)
                (.addWebapp "" (.getAbsolutePath war-file))
                (.start) )]
    (.mkdirs (io/file base-dir "webapps"))
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
  (let [artifact-id (second (extract-ids (c/get-env :project)))
        version     (c/get-env :version) ]
    (c/with-post-wrap
      (let [war-file (first (c/by-ext ["war"] (c/src-files)))
            base-dir #(c/mkoutdir! ::base-dir) ]
        (swap! server #(do (if % (destroy %)) (create (base-dir) war-file port join?))) ))))
