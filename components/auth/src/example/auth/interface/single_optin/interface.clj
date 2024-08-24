(ns example.auth.interface.single-optin.interface
  (:require [example.auth.core.single-optin.core :as core]))

(defn wrap-options [handler options] (core/wrap-options handler options))

(defn send-link-handler [ctx] (core/send-link-handler ctx))
(defn verify-link-handler [ctx] (core/verify-link-handler ctx))
(defn send-code-handler [ctx] (core/send-code-handler ctx))
(defn verify-code-handler [ctx] (core/verify-code-handler ctx))
(defn signout [ctx] (core/signout ctx))
(defn email-valid? [ctx email] (core/email-valid? ctx email))