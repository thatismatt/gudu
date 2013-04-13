# gudu

*"Generate URL, Degenerate URL"*

A bi-directional routing and URL generation library, where routes are configured as simple clojure data structures.

Routes are defined as a data structure that is used at runtime to route requests and construct URLs. As the routes are just a data structure they can be easily inspected by other functions to produce related data, e.g. navigation, breadcrumbs.

## Usage

    (require 'gudu)
    (require 'gudu.core)
    (require 'gudu.middleware)

    (def my-routes
      {:home gudu.core/root
       :blog ["blog" {:latest gudu.core/root
                      :post   [gudu.core/string-segment]}]})

    ;; URL Generation
    (def gu (gudu/gu my-routes))

    (gu :home)   ;; => "/"
    (gu :blog)   ;; => "/blog"
    (gu :blog :post "great-post") ;; => "/blog/great-post"

    ;; URL Degeneration
    (def du (gudu/du my-routes))

    (du "/")     ;; => (:home)
    (du "/blog") ;; => (:blog :latest)
    (du "/blog/another-post")     ;; => (:blog :post "another-post")

    ;; du is normally accessed indirectly via the gudu ring middleware
    (defn get-handler [routes] ... return handler function based on route ...)
    (def app
      (-> (gudu.middleware/router get-handler my-routes)
          (gudu.middleware/wrap-route my-routes)))

## Documentation

* [API Docs](http://thatismatt.github.io/gudu/doc/)

## Examples

See the [gudu-examples](https://github.com/thatismatt/gudu-examples) project, in particular take a look at [blog.clj](https://github.com/thatismatt/gudu-examples/blob/master/src/gudu_examples/blog.clj).

## License

Copyright © 2013 Matt Lee

Distributed under the Eclipse Public License, the same as Clojure.
