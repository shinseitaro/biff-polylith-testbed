(ns example.db.interface
  (:require [example.db.core :as core]))



(def module core/module)
(defn new-user-tx [user] (core/new-user-tx user))
(defn set-foo [id foo] (core/set-foo id foo))
(defn set-bar [id bar] (core/set-bar id bar))
(defn set-message [id text] (core/set-message id text))

(defn get-user-id [db user] (core/get-user-id db user))
(defn query-message-in-10min [db] (core/query-message-in-10min db))

