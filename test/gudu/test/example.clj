(comment

  (ns gudu.test.example
    (:require gudu
              ;; ring
              ))

  (def my-routes
    {:home    ["home"]
     :invoice ["invoice" gudu/int-seg]}) ;; string-seg, regex-seg, etc...

  (def my-gu
    (gudu/gu my-routes))

  (defn get-invoice [id]
    '(invoice-page id (my-gu :home)))

  (defn home
    '(home-page (my-gu invoice 1)))

  (def handler
    (-> (du my-routes)
        'middleware)))
