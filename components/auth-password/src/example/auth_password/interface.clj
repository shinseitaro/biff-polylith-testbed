(ns example.auth-password.interface
  (:require [example.auth-password.core :as core]
            [example.auth-password.db :as db]
            [example.auth-password.home :as home]))

(def module db/module)


(defn signup-form [ctx] (home/signup-form ctx))
(defn signin-form [ctx] (home/signin-form ctx))

(defn wrap-options [handler options] (core/wrap-options handler options))

(defn register-handler [ctx] (core/register-handler ctx))
(defn signin-handler [ctx] (core/signin-handler ctx))
(defn signout [ctx] (core/signout ctx))