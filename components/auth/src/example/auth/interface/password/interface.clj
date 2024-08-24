(ns example.auth.interface.password.interface
  (:require [example.auth.core.password.core :as core]))

(defn wrap-options [handler options] (core/wrap-options handler options))

(defn register-handler [ctx] (core/register-handler ctx))
(defn signin-handler [ctx] (core/signin-handler ctx))
(defn signout [ctx] (core/signout ctx))