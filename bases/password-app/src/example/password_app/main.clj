(ns example.password-app.main
  (:require [com.biffweb :as biff]
            [malli.core :as malc]
            [malli.registry :as malr]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :as tn-repl]
            [nrepl.cmdline :as nrepl-cmd]

            [example.app.interface :as app]
            [example.middleware.interface :as mid]
            [example.ui.interface :as ui]

            [example.home.interface :as home]
            [example.home.interface.password.interface :as home-password]

            [example.db.interface :as db]
            [example.db.interface.password.interface :as db-password]
            [example.auth.interface.password.interface :as auth])
  (:gen-class))

(def modules
  [app/module
   db/module
   db-password/module])

(def home-options
  #:biff.home{:signup-form home-password/signup-form
              :signin-form home-password/signin-form})

(def app-options
  #:biff.app{:set-foo db/set-foo
             :set-bar db/set-bar})


(def application-routes
  [["" {:middleware [mid/wrap-redirect-signed-in
                     [home/wrap-options home-options]]}

    ["/" {:get home/home-page}]
    ["/signin" {:get home/signin-page}]]

   ["/app" {:middleware [mid/wrap-signed-in
                         [app/wrap-options app-options]]}
    [""         {:get app/app}]
    ["/set-foo" {:post app/set-foo}]
    ["/set-bar" {:post app/set-bar}]
    ["/chat" {:get app/ws-handler}]]

   ["/auth" {:middleware [[auth/wrap-options {}]]}
    ["/register" {:post auth/register-handler}]
    ["/signin"   {:post auth/signin-handler}]
    ["/signout"  {:post auth/signout}]]])

(def routes [["" {:middleware [mid/wrap-site-defaults]}
              application-routes]
             ["" {:middleware [mid/wrap-api-defaults]}
              (keep :api-routes modules)]])

(def handler (-> (biff/reitit-handler {:routes routes})
                 mid/wrap-base-defaults))

(def static-pages (apply biff/safe-merge (map :static modules)))

(defn generate-assets! [ctx]
  (biff/export-rum static-pages "target/resources/public")
  (biff/delete-old-files {:dir  "target/resources/public"
                          :exts [".html"]}))

(defn on-save [ctx]
  (biff/add-libs)
  (biff/eval-files! ctx)
  (generate-assets! ctx)
  #_(test/run-all-tests #"example.*-test"))

(def malli-opts
  {:registry (malr/composite-registry
              malc/default-registry
              (apply biff/safe-merge (keep :schema modules)))})

(def initial-system
  {:biff/modules             #'modules
   :biff/handler             #'handler
   :biff/malli-opts          #'malli-opts
   :biff.beholder/on-save    #'on-save
   :biff.middleware/on-error #'ui/on-error
   :biff.xtdb/tx-fns         biff/tx-fns
   :app/chat-clients         (atom #{})
   :app-name                 "username ＆ password アプリケーション"})


(defonce system (atom {}))


(def components
  [biff/use-aero-config
   biff/use-xtdb
   biff/use-queues
   biff/use-xtdb-tx-listener
   biff/use-htmx-refresh
   biff/use-jetty
   biff/use-chime
   biff/use-beholder])



(defn start []
  (let [new-system (reduce (fn [system component]
                             (log/info "starting:" (str component))
                             (component system))
                           initial-system
                           components)]
    (reset! system new-system)
    (generate-assets! new-system)
    (log/info "System started.")
    (log/info "Go to" (:biff/base-url new-system))
    new-system))

(defn -main []
  (let [{:keys [biff.nrepl/args]} (start)]
    (apply nrepl-cmd/-main args)))

(defn refresh []
  (doseq [f (:biff/stop @system)]
    (log/info "stopping:" (str f))
    (f))
  (tn-repl/refresh :after `start)
  :done)
