(ns gudu.core
  (:require [clojure.string :as str])
  (:use [gudu.utils]))

;; ## Vocabulary
;;  "pieces" - The collection of strings created when a URL is split at the '/' character
;;      e.g. URL: /foo/bar/baz => pieces: ["foo" "bar" "baz"]
;;  "params" - A collection that can be used to create URL pieces given a routing configuration
;;  "segment" - A definition for the URL pieces that can be matched at a particular point in a route

(defprotocol Segment
  (match [segment pieces]
    "## Segment Matching - creating params (from URL pieces)
     Given a routing configuration (defined as a top level segment),
     create a params collection from the URL pieces.")
  (process [segment params]
    "## Segment Processing - constructing URL pieces (from params)
     Given a routing configuration (defined as a top level segment),
     create the URL pieces from a params collection."))

(defn match-segments
  [segment pieces]
  (if-let [[pieces params] (match segment pieces)]
    (if-not (seq pieces) ;; no more URL pieces to be matched
      params)))

(defn process-segments
  [segment params]
  (let [[params pieces] (process segment params)]
    (if-not (seq params) ;; params have all been consumed
      pieces)))

(defn match-static-segment [segment [piece & pieces]]
  (if (= (name segment) piece)
    [pieces []]))

(defn process-static-segment [segment params]
  [params [(name segment)]])

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
   (fn [segment pieces]
     (let [match (->> segment
                      (remap #(match % pieces))
                      (filter (fn [[k v]] ((comp not nil?) v)))
                      (sort-by (comp count last))
                      last)]
       (if-let [[pr [pcs prs]] match]
         [pcs (into [pr] prs)])))
   :process
   (fn [segment [param & params :as all]]
     (if-let [next-segment (segment param)]
       (process next-segment params)))})

(extend clojure.lang.IPersistentVector
  Segment
  {:match
   (fn [segments pieces]
     (let [[rest-pieces params]
           (reduce
            (fn [[pcs agg] segment]
              (let [[pcs2 prs] (match segment pcs)]
                [pcs2 (conj agg prs)]))
            [pieces []]
            segments)]
       (if-not (some nil? params)
         [rest-pieces (apply concat params)])))
   :process
   (fn [segments params]
     (let [[rest-params pieces]
           (reduce
            (fn [[prs agg] segment]
              (let [[prs2 pcs] (process segment prs)]
                [prs2 (conj agg pcs)]))
            [params []]
            segments)]
       [rest-params (apply concat pieces)]))})

(def int-segment
  "A segment that matches integers."
  (reify Segment
    (match [_ [piece & pieces]]
      (if piece
        (if-let [int-str (re-find #"^[0-9]+$" piece)]
          (let [int-val (Integer/valueOf int-str)]
            [pieces [int-val]]))))
    (process [_ [param & params]]
      ;; TODO - verify param is actually an integer
      [params [param]])))

(def string-segment
  "A segment that matches any string."
  (reify Segment
    (match [_ [piece & pieces]]
      (if piece
        [pieces [piece]]))
    (process [_ [param & params]]
      [params [param]])))

(def root
  (reify Segment
    (match [segment pieces]
      (if (empty? pieces) []))
    (process [segment params]
      (if (empty? params) []))))

(defn split-url [url]
  "Create a collection of URL pieces from a URL."
  (filter (comp not empty?) ;; ignore leading & multiple slashes
          (str/split url #"/")))

(defn join-pieces [pieces context]
  "Create a URL from a collection of URL pieces."
  (str context (if-not (= \/ (last context)) "/") (str/join "/" pieces)))
