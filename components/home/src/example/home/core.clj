(ns example.home.core
  (:require [com.biffweb :as biff]
            [example.ui.interface :as ui]))


(defn home-page [{:keys [biff.home/signup-form]
                  :as   ctx}]
  (ui/page
   (assoc ctx ::ui/recaptcha true)
   (signup-form ctx)))

(defn signin-page [{:keys [biff.home/signin-form]
                    :as   ctx}]
  (ui/page
   (assoc ctx ::ui/recaptcha true)
   (signin-form ctx)))


(defn wrap-options [handler options]
  (fn [ctx]
    (handler (merge options ctx))))
