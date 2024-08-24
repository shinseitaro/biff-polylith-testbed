(ns example.app.form
  (:require [com.biffweb :as biff]))


(defn foo-form [foo]
  (biff/form
   {:action "/app/set-foo"}
   [:label.block {:for "foo"} "Foo: "
    [:span.font-mono (pr-str foo)]]
   [:.h-1]
   [:.flex
    [:input.w-full#foo {:type  "text"
                        :name  "foo"
                        :value foo}]
    [:.w-3]
    [:button.btn {:type "submit"} "Update"]]
   [:.h-1]
   [:.text-sm.text-gray-600
    "古いフォームを使って値を更新する例"]))

(defn bar-form [value]
  (biff/form
   {:hx-post "/app/set-bar"
    :hx-swap "outerHTML"}
   [:label.block {:for "bar"} "Bar: "
    [:span.font-mono (pr-str value)]]
   [:.h-1]
   [:.flex
    [:input.w-full#bar {:type  "text"
                        :name  "bar"
                        :value value}]
    [:.w-3]
    [:button.btn {:type "submit"} "Update"]]
   [:.h-1]
   [:.text-sm.text-gray-600
    "HTMX による値の更新"]))

(defn chat-form []
  [:form.mb-0 {:ws-send true
               :_       "on submit set value of #message to ''"}
   [:label.block {:for "message"} "Write a message"]
   [:.h-1]
   [:textarea.w-full#message {:name "text"}]
   [:.h-1]
   [:.text-sm.text-gray-600
    "Sign in with an incognito window to have a conversation with yourself."]
   [:.h-2]
   [:div [:button.btn {:type "submit"} "Send message"]]])

(defn signout-form []
  (biff/form
   {:action "/auth/signout"
    :class  "inline"}
   [:button.text-blue-500.hover:text-blue-800 {:type "submit"}
    "Sign out"]))