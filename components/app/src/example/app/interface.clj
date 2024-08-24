(ns example.app.interface
  (:require [example.app.core :as core]))


(def module core/module)
(defn app [ctx] (core/app ctx))
(defn set-foo [ctx] (core/set-foo ctx))
(defn set-bar [ctx] (core/set-bar ctx))
(defn ws-handler [ctx] (core/ws-handler ctx))
(defn wrap-options [handler options] (core/wrap-options handler options))
