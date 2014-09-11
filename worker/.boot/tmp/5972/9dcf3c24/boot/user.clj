(ns boot.user
  (:require [boot.task.built-in :refer :all]
            [boot.repl :refer :all]
            [boot.cli :refer :all]
            [boot.core :refer :all]
            [boot.util :refer :all :exclude [pp]]))

(clojure.core/comment "start boot script")

(set-env!
  :src-paths
  #{"src"}
  :tgt-path
  "tgt"
  :dependencies
  '[[org.apache.tomcat.embed/tomcat-embed-jasper "8.0.8"]
    [org.apache.tomcat.embed/tomcat-embed-logging-juli "8.0.8"]
    [org.apache.tomcat.embed/tomcat-embed-core "8.0.8"]])

(task-options!
  watch
  [:quiet false]
  speak
  [:theme "ordinance"]
  pom
  [:project
   'tailrecursion/boot.worker.tomcat
   :version
   "0.1.0-SNAPSHOT"
   :description
   "Boot worker to create a standalone Tomcat server."
   :url
   "http://github.com/tailrecursion/boot.task.tomcat"
   :scm
   {:url "https://github.com/tailrecursion/boot.task.tomcat"}
   :license
   {:name "Eclipse Public License",
    :url "http://www.eclipse.org/legal/epl-v10.html"}])

(deftask
  develop
  "Build a war and install it to ~/.m2 when the source changes."
  []
  (comp (watch) (speak) (install)))

(clojure.core/comment "end boot script")

(clojure.core/let [boot?__1890__auto__ true]
  (clojure.core/if-not boot?__1890__auto__
    (clojure.core/when-let [main__1891__auto__ (clojure.core/resolve
                                                 'boot.user/-main)]
      (main__1891__auto__ "install"))
    (boot.core/boot "install")))
