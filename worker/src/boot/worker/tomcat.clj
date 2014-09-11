;;;-------------------------------------------------------------------------------------------------
;;; Copyright Alan Dipert, Micha Niskin, & jumblerg. All rights reserved. The use and distribution
;;; terms for this software are covered by the Eclipse Public License 1.0 (http://www.eclipse.org/
;;; legal/epl-v10.html). By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.  You must not remove this notice, or any other, from this software.
;;;-------------------------------------------------------------------------------------------------

(ns boot.worker.tomcat
  (:require
    [clojure.java.io :as io] )
  (:import
    [org.apache.catalina.startup Tomcat] ))

;;; state ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def server (atom nil))

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
  (when server
    (doto server .stop .destroy) ))

(defn tomcat [& args]
  (swap! server #(do (destroy %) (apply create args))) )

(defn hello []
  (println "hello world") )
