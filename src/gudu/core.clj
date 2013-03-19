(ns gudu.core
  (:require [clojure.string :as str])
  (:use [gudu.utils]))

;; ## Vocabulary
;;  "pieces" - The collection of strings created when a URL is split at the '/' character
;;      e.g. URL: /foo/bar/baz => pieces: ["foo" "bar" "baz"]
;;  "params" - A collection that can be used to create URL pieces given a routing configuration
;;  "segment" - A definition for the URL pieces that can be matched at a particular point in a route
;;      There are different types of segments, e.g. string and map.

;; ## Segment Processing - constructing URL pieces (from params)
;; Given a routing configuration (defined as a collection of segments),
;; create the URL pieces from a params collection.

(defn static-segment? [segment] (or (string? segment) (keyword? segment)))

(declare process-static-segment process-map-segment)

(defn process-segments
  ([segments params]
     (process-segments segments params []))
  ([[segment & _ :as segments] params pieces]
     (if (empty? segments) ;; check anything else?
       pieces
       (apply (cond
               (static-segment? segment) process-static-segment
               (map?            segment) process-map-segment)
              [segments params pieces]))))

(defn process-static-segment [[segment & segments] params pieces]
  (process-segments segments params (conj pieces (name segment))))

(defn process-map-segment [[segment & _] [param & params] pieces]
  (process-segments (segment param) params pieces))

;; ## Segment Matching - creating params (from URL pieces)
;; Given a routing configuration (defined as a collection of segments),
;; create a params collection from the URL pieces.

(declare match-static-segment match-map-segment)

(defn match-segments
  ([segments pieces]
     (match-segments segments pieces []))
  ([[segment & _ :as segments] pieces params]
     (if (empty? segments) ;; no more routes to match against
       (if (empty? pieces) ;; no more URL pieces to be matched
         params            ;; so, we've found a match
         nil)              ;; unmatched URL pieces, this isn't the route you're looking for
       (apply (cond
               (static-segment? segment) match-static-segment
               (map?            segment) match-map-segment
               (throw (Exception. "Invalid route segment")) nil)
              [segments pieces params]))))

(defn match-static-segment [[segment & segments] [piece & pieces] params]
  (if (= (name segment) piece)
    (match-segments segments pieces params)
    nil))

(defn match-map-segment [[segment & _] pieces params]
  ;; NOTE: segments after a map segment are ignore
  (let [match (->> segment
                   (remap #(match-segments % pieces params))
                   (filter (fn [[k v]] ((comp not nil?) v)))
                   first)]
    (and match (apply cons match))))

(defn split-url [url]
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(defn join-pieces [pieces]
  (str "/" (str/join "/" pieces)))
