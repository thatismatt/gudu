(ns gudu.segment
  (:refer-clojure :exclude [int]))

;; ## Vocabulary
;;  "pieces"  - The collection of strings created when a URL is split on the '/' character
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
  (when (= (name segment) piece)
    [pieces []]))

(defn- process-static-segment [segment params]
  [params [(name segment)]])

(defn map-match [segment pieces]
  (let [match (->> (update-vals segment #(match % pieces))
                   (filter (fn [[_ v]] ((comp not nil?) v)))
                   (sort-by (comp count last))
                   last)]
    (when-let [[pr [pcs prs]] match]
      [pcs (into [pr] prs)])))

(defn map-process [segment [param & params]]
  (when-let [next-segment (segment param)]
    (process next-segment params)))

(defn vector-match [segments pieces]
  (let [[rest-pieces params]
        (reduce
         (fn [[pcs agg] segment]
           (let [[pcs2 prs] (match segment pcs)]
             [pcs2 (conj agg prs)]))
         [pieces []]
         segments)]
    (when-not (some nil? params)
      [rest-pieces (apply concat params)])))

(defn vector-process [segments params]
  (let [[rest-params pieces]
        (reduce
         (fn [[prs agg] segment]
           (let [[prs2 pcs] (process segment prs)]
             [prs2 (conj agg pcs)]))
         [params []]
         segments)]
    [rest-params (apply concat pieces)]))

(extend-protocol Segment

  #?(:clj  java.lang.String
     :cljs string)
  (match   [segment pieces] (match-static-segment   segment pieces))
  (process [segment params] (process-static-segment segment params))

  #?(:clj  clojure.lang.Keyword
     :cljs cljs.core.Keyword)
  (match   [segment pieces] (match-static-segment   segment pieces))
  (process [segment params] (process-static-segment segment params))

  #?@(:clj
      (clojure.lang.IPersistentMap
       (match   [segment pieces] (map-match   segment pieces))
       (process [segment params] (map-process segment params))))

  #?@(:clj
      (clojure.lang.IPersistentVector
       (match   [segments pieces] (vector-match   segments pieces))
       (process [segments params] (vector-process segments params))))

  #?@(:cljs
      (default
       (match [segment pieces]
              (cond
                (satisfies? IMap segment)    (map-match    segment pieces)
                (satisfies? IVector segment) (vector-match segment pieces)
                :else (throw (str "Segment/match not implemented for " (type segment)))))
       (process [segment params]
                (cond
                  (satisfies? IMap segment)    (map-process    segment params)
                  (satisfies? IVector segment) (vector-process segment params)
                  :else (throw (str "Segment/process not implemented for " (type segment))))))))

(def int
  "A segment that matches integers."
  (reify Segment
    (match [_ [piece & pieces]]
      (when piece
        (when-let [int-str (re-find #"^[0-9]+$" piece)]
          (let [int-val (parse-long int-str)]
            [pieces [int-val]]))))
    (process [_ [param & params]]
      ;; TODO - verify param is actually an integer
      [params [param]])))

(def string
  "A segment that matches any string."
  (reify Segment
    (match [_ [piece & pieces]]
      (when piece
        [pieces [piece]]))
    (process [_ [param & params]]
      [params [param]])))

(def root
  "A segment that matches the root URL."
  (reify Segment
    (match [_segment pieces]
      (when (empty? pieces) []))
    (process [_segment params]
      (when (empty? params) []))))
