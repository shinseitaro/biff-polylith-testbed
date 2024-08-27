(ns example.auth-password.home
  (:require [com.biffweb :as biff]))


(defn signup-form [{:keys [recaptcha/site-key app-name]}]
  (biff/form
   {:action "/auth/register"
    :id     "signup"
    :hidden {:on-error "/"}}
   (biff/recaptcha-callback "submitSignup" "signup")
   [:h2.text-2xl.font-bold (str "Sign up for " app-name)]
   [:.h-3]
   [:.flex.flex-col
    [:input#username
     {:name        "username"
      :type        "text"
      :placeholder "ユーザー名を入れてください"}]
    [:.w-3]
    [:input#password
     {:name        "password"
      :type        "password"
      :placeholder "パスワードを入れてください"}]
    [:button.btn.g-recaptcha
     (merge (when site-key
              {:data-sitekey  site-key
               :data-callback "submitSignup"})
            {:type "submit"})
     "Sign up"]]

   [:.h-1]
   [:.text-sm "Already have an account? " [:a.link {:href "/signin"} "Sign in"] "."]
   [:.h-3]))

(defn signin-form [{:keys [recaptcha/site-key app-name]}]
  (biff/form
   {:action "/auth/signin"
    :id     "signin"
    :hidden {:on-error "/signin"}}
   (biff/recaptcha-callback "submitSignin" "signin")
   [:h2.text-2xl.font-bold "Sign in to " app-name]
   [:.h-3]
   [:.flex.flex-col
    [:input#username
     {:name        "username"
      :type        "text"
      :placeholder "ユーザー名を入れてください"}]
    [:.w-3]
    [:input#password
     {:name        "password"
      :type        "password"
      :placeholder "パスワードを入れてください"}]
    [:button.btn.g-recaptcha
     (merge (when site-key
              {:data-sitekey  site-key
               :data-callback "submitSignin"})
            {:type "submit"})
     "Sign in"]]

   [:.h-1]
   [:.text-sm "Don't have an account yet? " [:a.link {:href "/"} "Sign up"] "."]
   [:.h-3]))
   