(ns example.ui.interface
  (:require [example.ui.core :as core]))


(defn page [ctx & body] (core/page ctx body))
(defn on-error [ctx] (core/on-error ctx))