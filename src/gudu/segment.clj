(ns gudu.segment
  (:refer-clojure :exclude [int])
  (:use gudu.utils))

;; ## Vocabulary
;;  "pieces"  - The collection of strings created when a URL is split at the '/' character
;;         e.g. URL: /foo/bar/baz => pieces: ["foo" "bar" "baz"]
;;  "params"  - A collection that can be used to create URL pieces given a routing configuration

(defprotocol Segment
  "A segment defines the way to map between pieces and params.
   A routing configuration is defined as segments."
  (match [segment pieces]
    "# Segment Matching - creating params (from URL pieces)

     Given a routing configuration (defined as a top level segment),
     create a params collection from the URL pieces.")
  (process [segment params]
    "# Segment Processing - constructing URL pieces (from params)

     Given a routing configuration (defined as a top level segment),
     create the URL pieces from a params collection."))

(defn- match-static-segment [segment [piece & pieces]]
  (if (= (name segment) piece)
    [pieces []]))

(defn- process-static-segment [segment params]
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

(def int
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

(def string
  "A segment that matches any string."
  (reify Segment
    (match [_ [piece & pieces]]
      (if piece
        [pieces [piece]]))
    (process [_ [param & params]]
      [params [param]])))

(def root
  "A segment that matches the root URL."
  (reify Segment
    (match [segment pieces]
      (if (empty? pieces) []))
    (process [segment params]
      (if (empty? params) []))))
