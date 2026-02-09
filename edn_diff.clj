#!/usr/bin/env bb

(ns edn-diff
  (:require [babashka.process :refer [shell]]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [lambdaisland.deep-diff2 :as diff2]
            [lambdaisland.deep-diff2.printer :as printer]
            [shell-smith.core :as smith]))

(def usage "
edn-diff

Usage:
  edn-diff [-aerb=<branch>] <edn-file>
  edn-diff -h

Options:
  -b --branch=<branch>  Branch used to compare [default: master]
  -a --added-only       Show only added compared to branch
  -r --removed-only     Show only removed compared to branch
  -e --edn-output       Format output in edn for scripting
  -h --help             Show help
")

(def config (smith/config usage))

(defn read-edn-from-branch [branch file-path]
  (-> (shell {:out :string} "git" "show" (str branch ":" file-path))
      :out
      edn/read-string))

(defn -main [& _args]
  (let [{:keys [branch edn-file]} (smith/config usage)
        current-branch (str/trim (:out (shell {:out :string} "git" "branch" "--show-current")))
        current-data (read-edn-from-branch current-branch edn-file)
        other-data (read-edn-from-branch branch edn-file)
        diff-result (diff2/diff current-data other-data)]
    (printer/print diff-result)))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
