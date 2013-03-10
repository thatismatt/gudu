(comment

  (ns gudu.test.example
    (:require gudu
              ;; ring
              ))

  (def my-routes
    {:home    ["home"]
     :invoice ["invoice" gudu/int-seg] ;; string-seg, regex-seg, etc...
     :blog    ["blog" {:latest  [] ;; /blog
                       :archive ["archive" gudu/int-seg gudu/int-seg]}]}) ;; /blog/archive/2013/01

  (def my-gu
    (gudu/gu my-routes))

  (defn get-invoice [id]
    '(invoice-page id (my-gu :home)))

  (defn home
    '(home-page (my-gu invoice 1)))

  (def handler
    (-> (du my-routes)
        'middleware)))
