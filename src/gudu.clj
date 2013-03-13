(ns gudu
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn- segment-url [url]
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(declare process-string-segment process-map-segment)

(defn- process-segments
  ([routes args]
     (process-segments routes args []))
  ([[route & _ :as routes] args segments]
     (if (empty? routes) ;; check anything else?
       segments
       (apply (cond
               (string? route) process-string-segment
               (map?    route) process-map-segment)
              [routes args segments]))))

(defn- process-string-segment [[route & routes] args segments]
  (process-segments routes args (conj segments route)))

(defn- process-map-segment [[route & routes] [arg & args] segments]
  (process-segments (concat (route arg) routes) args segments))

(defn gu
  "Generate URL"
  [routes]
  (fn [& args]
    (let [segments (process-segments [routes] args)]
      (str "/" (str/join "/" segments)))))

(defn du
  "Degenerate URL"
  [routes]
  (let [urls (set/map-invert routes)]
    (fn [url] [(urls (segment-url url))])))
