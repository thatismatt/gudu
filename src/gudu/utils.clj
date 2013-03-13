(ns gudu.utils)

;; see http://stackoverflow.com/questions/1676891
(defn remap [f m]
  "Remap the values of m with f"
  (into {} (for [[k v] m] [k (f v)])))
