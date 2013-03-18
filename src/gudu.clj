(ns gudu
  (:require [gudu.core :as c]))

(defn gu
  "Generate URL"
  [routes]
  (fn [& params] (c/join-pieces (c/process-segments [routes] params))))

(defn du
  "Degenerate URL"
  [routes]
  (fn [url] (c/match-segments [routes] (c/split-url url))))
