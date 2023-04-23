(ns gudu.core
  (:require [clojure.string :as str]
            [gudu.segment :as gds]))

(defn match-segments
  [segment pieces]
  (when-let [[pieces params] (gds/match segment pieces)]
    (when-not (seq pieces) ;; no more URL pieces to match
      params)))

(defn process-segments
  [segment params]
  (let [[params pieces] (gds/process segment params)]
    (when-not (seq params) ;; params have all been consumed
      pieces)))

(defn split-url
  "Create a collection of URL pieces from a URL."
  [url]
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(defn join-pieces
  "Create a URL from a collection of URL pieces."
  [pieces context]
  (str context (when-not (= \/ (last context)) "/") (str/join "/" pieces)))

(defn gu
  "Generate URL"
  [routes & {:keys [context] :or {context "/"}}]
  (fn [& params] (join-pieces (process-segments routes params) context)))

(defn du
  "Degenerate URL"
  [routes]
  (fn [url] (match-segments routes (split-url url))))
