(ns example.home.interface.password.interface
  (:require [example.home.core.password.core :as core]))

(defn signup-form [ctx] (core/signup-form ctx))
(defn signin-form [ctx] (core/signin-form ctx))
