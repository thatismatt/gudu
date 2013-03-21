(ns gudu.middleware
  (:use [gudu]))

(defn wrap-route
  "Ring middleware that adds a :route key to the request."
  [handler routes]
  (let [my-du (du routes)]
    (fn [req]
      (let [url     (or (:path-info req)
                        (:uri req))
            route   (my-du url)
            new-req (assoc req :route route)]
        (handler new-req)))))
