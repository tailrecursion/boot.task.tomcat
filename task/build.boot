;;;-------------------------------------------------------------------------------------------------
;;; Copyright Alan Dipert, Micha Niskin, & jumblerg. All rights reserved. The use and distribution
;;; terms for this software are covered by the Eclipse Public License 1.0 (http://www.eclipse.org/
;;; legal/epl-v10.html). By using this software in any fashion, you are agreeing to be bound by the
;;; terms of this license.  You must not remove this notice, or any other, from this software.
;;;-------------------------------------------------------------------------------------------------

(set-env!
  :src-paths    #{"src"}
  :tgt-path     "tgt" )

(task-options!
  watch   [:quiet       false]
  speak   [:theme       "ordinance"]
  pom     [:project     'tailrecursion/boot.task.tomcat
           :version      "0.1.0-SNAPSHOT"
           :description  "Boot task to create a standalone Tomcat server."
           :url          "http://github.com/tailrecursion/boot.task.tomcat"
           :scm         {:url  "https://github.com/tailrecursion/boot.task.tomcat"}
           :license     {:name "Eclipse Public License"
                         :url  "http://www.eclipse.org/legal/epl-v10.html"} ])

(deftask develop
  "Build a war and install it to ~/.m2 when the source changes."
  []
  (comp (watch) (speak) (install)) )
