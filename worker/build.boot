;;;-------------------------------------------------------------------------------------------------
;;; Copyright Alan Dipert, Micha Niskin, & jumblerg. All rights reserved. The use and distribution
;;; terms for this software are covered by the Eclipse Public License 1.0 (http://www.eclipse.org/
;;; legal/epl-v10.html). By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.  You must not remove this notice, or any other, from this software.
;;;-------------------------------------------------------------------------------------------------

(require '[clojure.java.io :as io])

(set-env!
  :src-paths    #{"src"}
  :tgt-path     "tgt"
  :dependencies '[[org.apache.tomcat.embed/tomcat-embed-jasper       "8.0.8"]
                  [org.apache.tomcat.embed/tomcat-embed-logging-juli "8.0.8"]
                  [org.apache.tomcat.embed/tomcat-embed-core         "8.0.8"]]
  :repositories #(into % [["legisense" {:url        "s3p://legisense.repository/snapshot"
                                        :username   (System/getenv "AWS_ACCESS_KEY_ID")
                                        :passphrase (System/getenv "AWS_SECRET_KEY") }]]))

(task-options!
  watch   [:quiet       false]
  speak   [:theme       "ordinance"]
  pom     [:project     'tailrecursion/boot.worker.tomcat
           :version     "0.0.1-SNAPSHOT"
           :description "Boot worker to create a standalone Tomcat server."
           :url         "http://github.com/tailrecursion/boot.task.tomcat"
           :scm         {:url  "https://github.com/tailrecursion/boot.task.tomcat"}
           :license     {:name "Eclipse Public License"
                         :url  "http://www.eclipse.org/legal/epl-v10.html"} ]
  ;push  [:file        "tgt/boot.worker.tomcat-0.1.0-SNAPSHOT.jar"
  ;       :repo        "legisense"] 

           )

;;; tasks ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftask build
  "Build the jar distribution and install it locally."
  []
  (comp (speak) (add-src) (pom) (jar) (install)) )
