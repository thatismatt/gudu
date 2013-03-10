(ns gudu
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn- segment-url [url]
  (filter (comp not empty?) (str/split url #"/")))

(defn gu
  "Generate URL"
  [routes]
  (fn [id]
    (let [segments (id routes)]
      (str "/" (str/join "/" segments)))))

(defn du
  "Degenerate URL"
  [routes]
  (let [urls (set/map-invert routes)]
    (fn [url] (urls (segment-url url)))))
