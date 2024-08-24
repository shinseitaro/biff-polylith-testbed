(ns example.auth.core.password.core
  (:require [com.biffweb :as biff]
            [buddy.hashers :as hashers]

            [example.db.interface :as db]
            [example.db.interface.password.interface :as db-password]))


(defn validate-username? [username]
  ;; 大文字小文字数字で３−１６文字
  (let [regex #"^[a-zA-Z0-9_-]{3,16}$"]
    (boolean (re-matches regex username))))

(defn validate-password? [password]
  ;; 大文字小文字数字と何かしらの記号を含んだ８文字以上
  (let [regex #"^(?=.*[a-zA-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).{8,}$"]
    (boolean (re-matches regex password))))

(comment
  (validate-password? "123QWEasd?")
  :rcf)

(defn user-exist? [db username]
  (some? (db/get-user-id db username)))

(defn create-account!
  "auth-user と user を作成する"
  [ctx username password]
  (let [tx (concat (db-password/new-authorized-user-tx username password)
                   (db/new-user-tx username))]
    (biff/submit-tx ctx tx)
    (biff/merge-context ctx)))

(defn validate-credentials? [db username password]
  (let [user-doc (db-password/get-authorized-user-doc db username)]
    (if user-doc
      (:valid  (hashers/verify password (:biff.auth.password/password user-doc)))
      false)))

(defn return-uid [db username session]
  (let [uid (db/get-user-id db username)]
    {:status  303
     :headers {"location" "/app"}
     :session (assoc session :uid uid)}))

(defn register-handler [{:keys [params biff/db session]
                         :as   ctx}]
  (let [{:keys [username password]} params]
    (cond
      (true? (user-exist? db username)) {:status 400
                                         :body   {:error "すでに存在する username です。"}}
      (false? (validate-username? username)) {:status 400
                                              :body   {:error "username は大文字小文字数字のいずれかで３−１６文字です。"}}
      (false? (validate-password? password)) {:status 400
                                              :body   {:error "password は大文字小文字数字と何かしらの記号を含んだ８文字以上です。"}}

      :else (let [{:keys [biff/db]} (create-account! ctx username password)]
              (return-uid db username session)))))

(defn signin-handler [{:keys [params biff/db session]
                       :as   ctx}]
  (let [{:keys [username password]} params
        valid-login?                (validate-credentials? db username password)]
    (if valid-login?
      (return-uid db username session) ;; session ID は UUID を使いまわす
      {:status 400
       :body   {:error "user or password invalid"}})))


(defn user-exist-handler [ctx]
  {:status 400
   :body   {:error "User already exists"}})

(defn signout [{:keys [session]}]
  {:status  303
   :headers {"location" "/"}
   :session (dissoc session :uid)})


(defn wrap-options [handler options]
  (fn [ctx]
    (handler (merge options ctx))))