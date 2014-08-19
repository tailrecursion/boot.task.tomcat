#!/usr/bin/env boot
#tailrecursion.boot.core/version "2.5.0"

(apply set-env! (read-string (slurp "config.edn")))

(require
  '[tailrecursion.boot.task        :refer [install]]
  '[tailrecursion.boot.task.notify :refer [hear]] )

(deftask develop []
  "rebuild and reinstall the library to ~/m2."
  (comp (watch) (hear) (install)) )
