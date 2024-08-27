(ns example.auth-password.db
  (:require [com.biffweb :as biff]
            [buddy.hashers :as hashers]))

;; schema / tx-docs / queries 

;; schema 
(def schema
  {:biff.auth.password/id :uuid
   :biff.auth/password    [:map {:close true}
                           [:xt/id :biff.auth.password/id]
                           [:biff.auth.password/username :string]
                           [:biff.auth.password/password :string]
                           [:biff.auth.password/created-at inst?]]})

(def module
  {:schema schema})


;; transaction doc and queries 

(defn get-authorized-user-doc [db username]
  (biff/lookup db :biff.auth.password/username username))

(defn new-authorized-user-tx [username password]
  [{:db/doc-type                   :biff.auth/password
    :db.op/upsert                  {:biff.auth.password/username username
                                    :biff.auth.password/password (hashers/derive password)}
    :biff.auth.password/created-at :db/now}])

