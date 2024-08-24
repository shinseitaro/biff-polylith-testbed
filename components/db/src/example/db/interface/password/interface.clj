(ns example.db.interface.password.interface
  (:require [example.db.core.password.core :as core]))

(def module core/module)
(defn get-authorized-user-doc [db username] (core/get-authorized-user-doc db username))
(defn new-authorized-user-tx [username password] (core/new-authorized-user-tx username password))
