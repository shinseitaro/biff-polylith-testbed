(ns example.email.interface.mailersend.interface
  (:require [example.email.core.mailersend.core :as core]))


(defn send-email [ctx opts] (core/send-email ctx opts)) 
