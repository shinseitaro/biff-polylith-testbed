(ns example.single-optin-app.main
  (:require [com.biffweb :as biff]
            [malli.core :as malc]
            [malli.registry :as malr]
            [clojure.tools.logging :as log]
            [clojure.tools.namespace.repl :as tn-repl]
            [nrepl.cmdline :as nrepl-cmd]

            [example.app.interface :as app]
            [example.middleware.interface :as mid]
            [example.ui.interface :as ui]
            [example.email.interface.mailersend.interface :as email]

            [example.home.interface :as home]
            [example.db.interface :as db]

            [example.auth-single-optin.interface :as auth])

  (:gen-class))

;; オプション
(def default-app-options
  #:biff.app{:set-foo db/set-foo
             :set-bar db/set-bar})

(def default-auth-options
  #:biff.auth{:app-path          "/app"
              :invalid-link-path "/signin?error=invalid-link"
              :check-state       true
              :new-user-tx       db/new-user-tx
              :get-user-id       db/get-user-id
              :single-opt-in     false
              :email-validator   auth/email-valid?})

(def home-options
  #:biff.home{:signup-form auth/signup-form
              :signin-form auth/signin-form})


(def app-routes
  [["" {:middleware [mid/wrap-redirect-signed-in
                     [home/wrap-options home-options]]}
    ["/" {:get home/home-page}]
    ["/signin" {:get home/signin-page}]
    ["/link-sent" {:get auth/link-sent}]
    ["/verify-link" {:get auth/verify-email-page}]
    ["/verify-code" {:get auth/enter-code-page}]]

   ["/app" {:middleware [[app/wrap-options default-app-options]
                         mid/wrap-signed-in]}
    ["" {:get app/app}]
    ["/set-foo" {:post app/set-foo}]
    ["/set-bar" {:post app/set-bar}]
    ["/chat" {:get app/ws-handler}]]

   ["/auth" {:middleware [[auth/wrap-options default-auth-options]]}
    ["/send-link" {:post auth/send-link-handler}]
    ["/verify-link/:token" {:get auth/verify-link-handler}]
    ["/verify-link" {:post auth/verify-link-handler}]
    ["/send-code" {:post auth/send-code-handler}]
    ["/verify-code" {:post auth/verify-code-handler}]
    ["/signout" {:post auth/signout}]]])

(def routes [["" {:middleware [mid/wrap-site-defaults]}
              app-routes]])

(def modules
  [app/module
   db/module
   auth/module])

(def handler
  (-> (biff/reitit-handler {:routes routes})
      mid/wrap-base-defaults))

(defn on-save [ctx]
  (biff/add-libs)
  (biff/eval-files! ctx))

(def malli-opts
  {:registry (malr/composite-registry
              malc/default-registry
              (apply biff/safe-merge (keep :schema modules)))})


(def initial-system
  {:biff/modules             #'modules
   :biff/send-email          #'email/send-email
   :biff/handler             #'handler
   :biff/malli-opts          #'malli-opts
   :biff.beholder/on-save    #'on-save
   :biff.middleware/on-error #'ui/on-error
   :biff.xtdb/tx-fns         biff/tx-fns
   :app/chat-clients         (atom #{})
   :app-name                 "single optin アプリケーション"})


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
    ;; (generate-assets! new-system)
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
