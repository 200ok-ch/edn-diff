#!/usr/bin/env bb

(ns edn-diff
  (:require [babashka.process :refer [shell]]
            [clojure.data :as data]
            [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.pprint :as pprint]
            [lambdaisland.deep-diff2 :as diff2]
            [shell-smith.core :as smith]))

(def usage "
edn-diff

Usage:
  edn-diff [-a|-r] [-e] [-b=<branch>] <edn-file>
  edn-diff -h

Options:
  -b --branch=<branch>  Branch used to compare [default: master]
  -a --added-only       Show only added compared to branch
  -r --removed-only     Show only removed compared to branch
  -e --edn-output       Format output in edn for scripting
  -h --help             Show help
")

(def config (smith/config usage :name "end_diff"))

(defn read-edn-from-branch [branch file-path]
  (-> (shell {:out :string} "git" "show" (str branch ":" file-path))
      :out
      edn/read-string))

(defn -main [& _args]
  (let [{:keys [branch edn-file added-only removed-only edn-output]} (smith/config usage)
        current-branch (str/trim (:out (shell {:out :string} "git" "branch" "--show-current")))
        current-data (read-edn-from-branch current-branch edn-file)
        other-data (read-edn-from-branch branch edn-file)]
    (cond
      added-only
      (let [diff (first (data/diff current-data other-data))]
        (if edn-output
          (prn diff)
          (pprint/pprint diff)))

      removed-only
      (let [diff (second (data/diff current-data other-data))]
        (if edn-output
          (prn diff)
          (pprint/pprint diff)))

      :else
      (if edn-output
        (prn (data/diff current-data other-data))
        (diff2/pretty-print (diff2/diff current-data other-data))))))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
