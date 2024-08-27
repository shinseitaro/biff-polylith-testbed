(ns example.home.interface
  (:require [example.home.core :as core]))

(defn home-page [ctx] (core/home-page ctx))
(defn signin-page [ctx] (core/signin-page ctx))
(defn wrap-options [handler options] (core/wrap-options handler options))
