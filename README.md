# gudu

"Generate URL, Degenerate URL"

A routing and URL generation library that tries to obey the notion of code as data.

Rather than routing based on macros or function that are impenetrable at runtime,
a data structure is defined that describes the routes. This can then be inspected at
runtime to route a request and construct URLs.

## Usage

    (require 'gudu)

    (def my-routes {:home []
                    :blog ["blog"])

    (def gu (gudu/gu my-routes))
    (def du (gudu/du my-routes))

    (gu :home)   ;; => "/"
    (du "/")     ;; => :home
    (gu :blog)   ;; => "/blog"
    (du "/blog") ;; => :blog

## Examples

See the [gudu-examples](https://github.com/thatismatt/gudu-examples) project, in particular take a look at [blog.clj](https://github.com/thatismatt/gudu-examples/blob/master/src/gudu_examples/blog.clj).

## License

Copyright © 2013 Matt Lee

Distributed under the Eclipse Public License, the same as Clojure.
