(ns gudu.middleware
  (:use gudu))

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

(defn router
  "Obtains the route from the :route entry of the request,
   picks a handler using handler-fn,
   defaults to default-handler (which normally generates a 404)"
  [handler-fn default-handler]
  (fn [req]
    (let [route   (:route req)
          handler (handler-fn route)]
      (or (and handler
               (handler req))
          (default-handler req)))))
