(ns example.auth-single-optin.interface
  (:require [example.auth-single-optin.core :as core]
            [example.auth-single-optin.db :as db]
            [example.auth-single-optin.home :as home]))


(def module db/module)

(defn signup-form [ctx] (home/signup-form ctx))
(defn signin-form [ctx] (home/signin-form ctx))


(defn wrap-options [handler options] (core/wrap-options handler options))

(defn send-link-handler [ctx] (core/send-link-handler ctx))
(defn verify-link-handler [ctx] (core/verify-link-handler ctx))
(defn send-code-handler [ctx] (core/send-code-handler ctx))
(defn verify-code-handler [ctx] (core/verify-code-handler ctx))
(defn signout [ctx] (core/signout ctx))
(defn email-valid? [ctx email] (core/email-valid? ctx email))

(defn link-sent [ctx] (home/link-sent ctx))
(defn verify-email-page [ctx] (home/verify-email-page ctx))
(defn enter-code-page [ctx] (home/enter-code-page ctx))
