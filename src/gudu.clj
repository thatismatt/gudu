(ns gudu
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn- segment-url [url]
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(defn- get-segments [routes [id & ids]]
  (let [[strings maps] (split-with string? (routes id))
        route          (first maps)
        segments-rest  (if (nil? route) [] (get-segments route ids))]
    (concat strings segments-rest)))

(defn gu
  "Generate URL"
  [routes]
  (fn [& ids]
    (let [segments (get-segments routes ids)]
      (str "/" (str/join "/" segments)))))

(defn du
  "Degenerate URL"
  [routes]
  (let [urls (set/map-invert routes)]
    (fn [url] [(urls (segment-url url))])))
