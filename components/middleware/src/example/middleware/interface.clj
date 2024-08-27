(ns example.middleware.interface
  (:require [example.middleware.core :as core]))


(defn wrap-redirect-signed-in [handler] (core/wrap-redirect-signed-in handler))

(defn wrap-signed-in [handler] (core/wrap-signed-in handler))

(defn wrap-debug [handler] (core/wrap-debug handler))

(defn wrap-site-defaults [handler] (core/wrap-site-defaults handler))

(defn wrap-api-defaults [handler] (core/wrap-api-defaults handler))

(defn wrap-base-defaults [handler] (core/wrap-base-defaults handler))
