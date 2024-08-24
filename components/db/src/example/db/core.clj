(ns example.db.core
  (:require [com.biffweb :as biff]))


(def schema
  {;; user docs to use in app after login or signup
   :user/id :uuid
   :user    [:map {:closed true}
             [:xt/id                     :user/id]
             [:user/user                 :string]
             [:user/joined-at            inst?]
             [:user/foo {:optional true} :string]
             [:user/bar {:optional true} :string]]

   ;; msg doc for streaming message. 
   :msg/id  :uuid
   :msg     [:map {:closed true}
             [:xt/id       :msg/id]
             [:msg/user    :user/id]
             [:msg/text    :string]
             [:msg/sent-at inst?]]})

(def module
  {:schema schema})

(defn new-user-tx
  "login / signup 後にユーザをユーザドキュメントに追加するドキュメント
   引数 user は single-optin であれば email、password であれば username"
  [user]
  [{:db/doc-type    :user
    :db.op/upsert   {:user/user user}
    :user/joined-at :db/now}])

(defn set-foo [id foo]
  [{:db/op       :update
    :db/doc-type :user
    :xt/id       id
    :user/foo    foo}])

(defn set-bar [id bar]
  [{:db/op       :update
    :db/doc-type :user
    :xt/id       id
    :user/bar    bar}])

(defn set-message [id text]
  [{:db/doc-type :msg
    :msg/user    id
    :msg/text    text
    :msg/sent-at :db/now}])

(defn get-user-id [db user]
  (biff/lookup-id db :user/user user))

(defn query-message-in-10min [db]
  (biff/q db
          '{:find  (pull msg [*])
            :in    [t0]
            :where [[msg :msg/sent-at t]
                    [(<= t0 t)]]}
          (biff/add-seconds (java.util.Date.) (* -60 10))))

