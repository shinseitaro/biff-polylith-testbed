(ns example.home.interface.single-optin.interface
  (:require [example.home.core.single-optin.core :as core]))

(defn signup-form [ctx] (core/signup-form ctx))
(defn signin-form [ctx] (core/signin-form ctx))
