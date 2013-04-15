(ns gudu.core
  (:require [clojure.string :as str])
  (:use gudu.utils
        gudu.segment))

(defn match-segments
  [segment pieces]
  (if-let [[pieces params] (match segment pieces)]
    (if-not (seq pieces) ;; no more URL pieces to match
      params)))

(defn process-segments
  [segment params]
  (let [[params pieces] (process segment params)]
    (if-not (seq params) ;; params have all been consumed
      pieces)))

(defn split-url
  "Create a collection of URL pieces from a URL."
  [url]
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(defn join-pieces
  "Create a URL from a collection of URL pieces."
  [pieces context]
  (str context (if-not (= \/ (last context)) "/") (str/join "/" pieces)))
