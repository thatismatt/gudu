(ns gudu
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn- segment-url [url]
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(declare process-string-segment process-map-segment)

(defn- process-segments
  ([routes params]
     (process-segments routes params []))
  ([[route & _ :as routes] params segments]
     (if (empty? routes) ;; check anything else?
       segments
       (apply (cond
               (string? route) process-string-segment
               (map?    route) process-map-segment)
              [routes params segments]))))

(defn- process-string-segment [[route & routes] params segments]
  (process-segments routes params (conj segments route)))

(defn- process-map-segment [[route & routes] [param & params] segments]
  (process-segments (concat (route param) routes) params segments))

(defn gu
  "Generate URL"
  [routes]
  (fn [& params]
    (let [segments (process-segments [routes] params)]
      (str "/" (str/join "/" segments)))))

(defn du
  "Degenerate URL"
  [routes]
  (let [urls (set/map-invert routes)]
    (fn [url] [(urls (segment-url url))])))
