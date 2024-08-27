(ns example.home.interface.single-optin.interface
  (:require [example.home.core.single-optin.core :as core]))

(defn signup-form [ctx] (core/signup-form ctx))
(defn signin-form [ctx] (core/signin-form ctx))

(defn link-sent [ctx] (core/link-sent ctx))
(defn verify-email-page [ctx] (core/verify-email-page ctx))
(defn enter-code-page [ctx] (core/enter-code-page ctx))