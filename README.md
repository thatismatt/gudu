# gudu

*"Generate URL, Degenerate URL"*

A bi-directional routing and URL generation library, where routes are configured as simple Clojure data structures.

Routes are defined as a data structure that is used at runtime to route requests and construct URLs. As the routes are
just a data structure they can be easily inspected by other functions to produce related data, e.g. navigation,
breadcrumbs.

## Usage

```clojure
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
```

## Documentation

* [API Docs](http://thatismatt.github.io/gudu/doc/)

## Examples

See the [gudu-examples](https://github.com/thatismatt/gudu-examples) project, in particular take a look at
[blog.clj](https://github.com/thatismatt/gudu-examples/blob/master/src/gudu_examples/blog.clj).

## License

Copyright © 2013 Matt Lee

Distributed under the Eclipse Public License, the same as Clojure.
