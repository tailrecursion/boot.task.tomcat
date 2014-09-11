;;;-------------------------------------------------------------------------------------------------
;;; Copyright Alan Dipert, Micha Niskin, & jumblerg. All rights reserved. The use and distribution
;;; terms for this software are covered by the Eclipse Public License 1.0 (http://www.eclipse.org/
;;; legal/epl-v10.html). By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.  You must not remove this notice, or any other, from this software.
;;;-------------------------------------------------------------------------------------------------

(ns boot.task.tomcat
  (:require
    [boot.core       :as core]
    [boot.pod        :as pod] ))

;; (boot.task.tomcat/tomcat base-dir war-file port join?)

(core/deftask serve
  "Boot task to create Tomcat server."

  [p port PORT int  "The port the server should listen on. Default 8000."]

  (let [pod (pod/make-pod (assoc (core/get-env) :dependencies '[[tailrecursion/boot.tomcat "0.1.0-SNAPSHOT"]]))
        dir (core/mktmpdir! ::base-dir) ]
    (core/with-post-wrap
      (when-let [war (->> (core/src-files) (core/by-ext ["war"]) first)]
        (println :vars dir war port)
        (pod/call-in pod
          `(boot.worker.tomcat/hello) )))))
