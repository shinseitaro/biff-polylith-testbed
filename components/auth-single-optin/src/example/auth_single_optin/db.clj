(ns example.auth-single-optin.db)

;; schema / tx-docs / queries 


(def schema
  {:biff.auth.code/id :uuid
   :biff.auth/code    [:map {:closed true}
                       [:xt/id :biff.auth.code/id]
                       [:biff.auth.code/email :string]
                       [:biff.auth.code/code :string]
                       [:biff.auth.code/created-at inst?]
                       [:biff.auth.code/failed-attempts integer?]]})

(def module
  {:schema schema})


(defn set-code-first [email code]
  [{:db/doc-type                    :biff.auth/code
    :db.op/upsert                   {:biff.auth.code/email email}
    :biff.auth.code/code            code
    :biff.auth.code/created-at      :db/now
    :biff.auth.code/failed-attempts 0}])

(defn set-code-again [code]
  [{:db/doc-type                    :biff.auth/code
    :db/op                          :update
    :xt/id                          (:xt/id code)
    :biff.auth.code/failed-attempts [:db/add 1]}])
