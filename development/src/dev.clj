(ns dev
  (:require #_[example.password-app.main :as main]
            [example.single-optin-app.main :as main]))



(defn -main []
  (main/-main))

;; clj -M:dev -m dev