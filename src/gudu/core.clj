(ns gudu.core
  (:require [clojure.string :as str])
  (:use [gudu.utils]))

;; ## Vocabulary
;;  "pieces" - The collection of strings created when a URL is split at the '/' character
;;      e.g. URL: /foo/bar/baz => pieces: ["foo" "bar" "baz"]
;;  "params" - A collection that can be used to create URL pieces given a routing configuration
;;  "segment" - A definition for the URL pieces that can be matched at a particular point in a route
;;      There are different types of segments, e.g. string and map.

(defprotocol Segment
  (match [segment segments pieces params]
    "## Segment Matching - creating params (from URL pieces)
     Given a routing configuration (defined as a collection of segments),
     create a params collection from the URL pieces.")
  (process [segment segments params pieces]
    "## Segment Processing - constructing URL pieces (from params)
     Given a routing configuration (defined as a collection of segments),
     create the URL pieces from a params collection."))
;; NOTES: Segment instances are responsible for recursing through the
;; rest of the pieces/params. match/process should return the params/pieces
;; created, and nil if there was no match.

(defn match-segments
  ([segments pieces]
     (match-segments segments pieces []))
  ([[segment & segments :as all-segments] pieces params]
     (if (empty? all-segments) ;; no more routes to match against
       (if (empty? pieces)     ;; no more URL pieces to be matched
         params                ;; so, we've found a match
         nil)                  ;; unmatched URL pieces, this isn't the route you're looking for
       (match segment segments pieces params))))

(defn process-segments
  ([segments params]
     (process-segments segments params []))
  ([[segment & segments :as all-segments] params pieces]
     (if (empty? all-segments) ;; check anything else?
       pieces
       (process segment segments params pieces))))

(defn match-static-segment [segment segments [piece & pieces] params]
  (if (= (name segment) piece)
    (match-segments segments pieces params)
    nil))

(defn process-static-segment [segment segments params pieces]
  (process-segments segments params (conj pieces (name segment))))

(extend java.lang.String
  Segment
  {:match match-static-segment
   :process process-static-segment})

(extend clojure.lang.Keyword
  Segment
  {:match match-static-segment
   :process process-static-segment})

(extend clojure.lang.IPersistentMap
  Segment
  {:match
   (fn [segment _ pieces params]
     ;; NOTE: segments after a map segment are ignored
     (let [matches (->> segment
                        (remap #(match-segments % pieces params))
                        (filter (fn [[k v]] ((comp not nil?) v)))
                        first)]
       (and matches (apply cons matches))))
   :process
   (fn [segment _ [param & params] pieces]
     (process-segments (segment param) params pieces))})

(def int-segment
  "A segment that matches integers."
  (reify Segment
    (match [_ segments [piece & pieces] params]
      (if-let [int-str (re-find #"^[0-9]+$" piece)]
        (let [int-val (Integer/valueOf int-str)]
          (match-segments segments pieces (conj params int-val)))))
    (process [_ segments [param & params] pieces]
      ;; TODO - verify param is actually an integer
      (process-segments segments params (conj pieces param)))))

(def string-segment
  "A segment that matches any string."
  (reify Segment
    (match [_ segments [piece & pieces] params]
      (match-segments segments pieces (conj params piece)))
    (process [_ segments [param & params] pieces]
      ;; TODO - verify param is actually a string
      (process-segments segments params (conj pieces param)))))

(defn split-url [url]
  "Create a collection of URL pieces from a URL."
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(defn join-pieces [pieces context]
  "Create a URL from a collection of URL pieces."
  (str context (if-not (= \/ (last context)) "/") (str/join "/" pieces)))
