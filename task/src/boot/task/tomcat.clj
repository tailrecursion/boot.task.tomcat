;;;-------------------------------------------------------------------------------------------------
;;; Copyright Alan Dipert, Micha Niskin, & jumblerg. All rights reserved. The use and distribution
;;; terms for this software are covered by the Eclipse Public License 1.0 (http://www.eclipse.org/
;;; legal/epl-v10.html). By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.  You must not remove this notice, or any other, from this software.
;;;-------------------------------------------------------------------------------------------------

(ns boot.task.tomcat
  (:require
    [clojure.java.io :as io]
    [boot.core       :as core]
    [boot.pod        :as pod] ))

(core/deftask serve
  "Boot task to create Tomcat server."

  [f file PATH #{str} "The path to the war file(s)."
   p port PORT int    "The port the server will listen on, incremented for each additional war."]

  (let [pod  (pod/make-pod (assoc (core/get-env)
              :dependencies '[[tailrecursion/boot.worker.tomcat "0.1.1-SNAPSHOT"]]))
        port (atom (or port 8000)) ]
    (core/with-pre-wrap
      (doseq [war  (if file (map io/file file) (core/by-ext ["war"] (core/tgt-files)))
              :let [dir (core/mktmpdir! (keyword (str *ns*) (str "base-dir." @port))) ]]
        (pod/call-in pod
          `(boot.worker.tomcat/create ~(.getPath dir) ~(.getAbsolutePath war) ~(- (swap! port inc) 1) ))))))
