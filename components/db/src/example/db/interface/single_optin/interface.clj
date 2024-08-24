(ns example.db.interface.single-optin.interface
  (:require [example.db.core.single-optin.core :as core]))

(def module core/module)
(defn set-code-first [email code] (core/set-code-first email code))
(defn set-code-again [code] (core/set-code-again code))

