(ns example.app.core
  (:require [com.biffweb :as biff]
            [rum.core :as rum]
            [xtdb.api :as xt]
            [ring.adapter.jetty9 :as jetty]
            [cheshire.core :as cheshire]

            [example.app.form :as form]

            [example.ui.interface :as ui]
            [example.db.interface :as db]))



(defn set-foo [{:keys [session params biff.app/set-foo]
                :as   ctx}]
  (biff/submit-tx ctx (set-foo (:uid session) (:foo params)))
  {:status  303
   :headers {"location" "/app"}})

(defn set-bar [{:keys [session params biff.app/set-bar]
                :as   ctx}]
  (biff/submit-tx ctx (set-bar (:uid session) (:bar params)))
  (biff/render (form/bar-form (:bar params))))


(defn message [{:msg/keys [text sent-at]}]
  [:.mt-3 {:_ "init send newMessage to #message-header"}
   [:.text-gray-600 (biff/format-date sent-at "yyyy/MM/dd HH:mm:ss")]
   [:div text]])

(defn notify-clients [{:keys [app/chat-clients]} tx]
  (doseq [[op & args] (::xt/tx-ops tx)
          :when       (= op ::xt/put)
          :let        [[doc] args]
          :when       (contains? doc :msg/text)
          :let        [html (rum/render-static-markup
                             [:div#messages {:hx-swap-oob "afterbegin"}
                              (message doc)])]
          ws          @chat-clients]
    (jetty/send! ws html)))

(defn send-message [{:keys [session]
                     :as   ctx} {:keys [text]}]
  (let [{:keys [text]} (cheshire/parse-string text true)]
    (biff/submit-tx ctx (db/set-message (:uid session) text))))

(defn chat [{:keys [biff/db]}]
  (let [messages (db/query-message-in-10min db)]

    [:div {:hx-ext     "ws"
           :ws-connect "/app/chat"}
     (form/chat-form)
     [:.h-6]
     [:div#message-header
      {:_ "on newMessage put 'Messages sent in the past 10 minutes:' into me"}
      (if (empty? messages)
        "メッセージはありません."
        "過去10分のメッセージ:")]
     [:div#messages
      (map message (sort-by :msg/sent-at #(compare %2 %1) messages))]]))

(defn app [{:keys [session biff/db]
            :as   ctx}]
  (let [{:user/keys [user foo bar]} (xt/entity db (:uid session))]
    (ui/page
     {}
     [:div "Signed in as " user ". "
      (form/signout-form)
      "."]
     [:.h-6]
     (form/foo-form foo)
     [:.h-6]
     (form/bar-form bar)
     [:.h-6]
     (chat ctx))))


(defn ws-handler [{:keys [app/chat-clients]
                   :as   ctx}]
  {:status  101
   :headers {"upgrade"    "websocket"
             "connection" "upgrade"}
   :ws      {:on-connect (fn [ws]
                           (swap! chat-clients conj ws))
             :on-text    (fn [ws text-message]
                           (send-message ctx {:ws   ws
                                              :text text-message}))
             :on-close   (fn [ws status-code reason]
                           (swap! chat-clients disj ws))}})


(def about-page
  (ui/page
   {:base/title (str "About ")}
   [:p "This app was made with "
    [:a.link {:href "https://biffweb.com"} "Biff"] "."]))

(defn echo [{:keys [params]}]
  {:status  200
   :headers {"content-type" "application/json"}
   :body    params})

(def module
  {:static     {"/about/" about-page}
   :api-routes [["/api/echo" {:post echo}]]
   :on-tx      notify-clients})


(defn wrap-options [handler options]
  (fn [ctx]
    (handler (merge options ctx))))