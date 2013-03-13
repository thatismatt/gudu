(ns gudu
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn- remap [f m]
  "Remap the values of m with f"
  (into {} (for [[k v] m] [k (f v)])))

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

(declare match-string-segment match-map-segment)

(defn- match-segments
  ([routes segments]
     (match-segments routes segments []))
  ([[route & _ :as routes] segments params]
     (if (empty? routes)     ;; no more routes to match against
       (if (empty? segments) ;; no more segments to be matched
         params              ;; so, we've found a match
         nil)                ;; unmatched segments left, this wasn't the route you were looking for
       (apply (cond
               (string? route) match-string-segment
               (map?    route) match-map-segment)
              [routes segments params]))))

(defn- match-string-segment [[route & routes] [segment & segments] params]
  (if (= route segment)
    (match-segments routes segments params)
    nil))

(defn- match-map-segment [[route & routes] segments params]
  ;; NOTE: currently routes is ignore, this implicitly
  ;; means that any routes after a map are ignored
  (->> route
       (remap #(match-segments % segments params))
       (filter (fn [[k v]] ((comp not nil?) v)))
       first
       (apply cons)))

(defn du
  "Degenerate URL"
  [routes]
  (fn [url] (match-segments [routes] (segment-url url))))
