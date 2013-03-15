# gudu

"Generate URL, Degenerate URL"

A routing and URL generation library that tries to obey the notion of code as data.

Rather than routing based on macros or function that are impenetrable at runtime,
a data structure is defined that describes the routes. This can then be inspected at
runtime to route and request and construct URLs.

## Usage

    (use gudu)

    (def my-routes ...)

    (def my-gu (gu my-routes))
    (def my-du (du my-routes))

    (my-gu :blog)   ;; => "/blog"
    (my-du "/blog") ;; => :blog

## License

Copyright © 2013 Matt Lee

Distributed under the Eclipse Public License, the same as Clojure.
