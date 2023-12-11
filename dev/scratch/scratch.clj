(ns scratch.scratch)

(require '[gudu.core :as gd]
         '[gudu.segment :as gds]
         '[gudu.middleware :as gdm])

(def my-routes
  {:home gds/root
   :blog ["blog" {:latest gds/root
                  :post   [gds/string]}]})

;; URL Generation
(def gu (gd/gu my-routes))

(gu :home)                    ;; => "/"
(gu :blog)                    ;; => "/blog"
(gu :blog :post "great-post") ;; => "/blog/great-post"

;; URL Degeneration
(def du (gd/du my-routes))

(du "/")                  ;; => [:home]
(du "/blog")              ;; => [:blog :latest]
(du "/blog/another-post") ;; => [:blog :post "another-post"]

;; du is normally accessed indirectly via the gudu ring middleware
(defn get-handler [route]
  ;; ... return handler function based on route ...
  (case (first route)
    :home (fn [req] {:handler :home :uri (:uri req) :route route})
    :blog (fn [req] {:handler :blog :uri (:uri req) :route route})))
(def app
  (-> (gdm/router get-handler (fn [_req] {:handler :404}))
      (gdm/wrap-route my-routes)))

(app {:uri "/"})       ;; {:handler :home, :uri "/", :route [:home]}
(app {:uri "/blog"})   ;; {:handler :blog, :uri "/blog", :route [:blog :latest]}
(app {:uri "/blog/3"}) ;; {:handler :blog, :uri "/blog/post-3", :route [:blog :post "3"]}
(app {:uri "/404"})    ;; {:handler :404}
