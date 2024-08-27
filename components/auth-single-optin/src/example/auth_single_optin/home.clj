(ns example.auth-single-optin.home
  (:require [com.biffweb :as biff]
            [example.ui.interface :as ui]))


(defn signup-form [{:keys [recaptcha/site-key params app-name]}]
  (biff/form
   {:action "/auth/send-link"
    :id     "signup"
    :hidden {:on-error "/"}}
   (biff/recaptcha-callback "submitSignup" "signup")
   [:h2.text-2xl.font-bold (str "Sign up for " app-name)]
   [:.h-3]
   [:.flex
    [:input#email {:name         "email"
                   :type         "email"
                   :autocomplete "email"
                   :placeholder  "Enter your email address"}]
    [:.w-3]
    [:button.btn.g-recaptcha
     (merge (when site-key
              {:data-sitekey  site-key
               :data-callback "submitSignup"})
            {:type "submit"})
     "Sign up"]]
   (when-some [error (:error params)]
     [:<>
      [:.h-1]
      [:.text-sm.text-red-600
       (case error
         "recaptcha" (str "You failed the recaptcha test. Try again, "
                          "and make sure you aren't blocking scripts from Google.")
         "invalid-email" "Invalid email. Try again with a different address."
         "send-failed" (str "We weren't able to send an email to that address. "
                            "If the problem persists, try another address.")
         "There was an error.")]])
   [:.h-1]
   [:.text-sm "Already have an account? " [:a.link {:href "/signin"} "Sign in"] "."]
   [:.h-3]
   biff/recaptcha-disclosure))


(defn signin-form [{:keys [recaptcha/site-key params app-name]}]
  (biff/form
   {:action "/auth/send-code"
    :id     "signin"
    :hidden {:on-error "/signin"}}
   (biff/recaptcha-callback "submitSignin" "signin")
   [:h2.text-2xl.font-bold "Sign in to " app-name]
   [:.h-3]
   [:.flex
    [:input#email {:name         "email"
                   :type         "email"
                   :autocomplete "email"
                   :placeholder  "Enter your email address"}]
    [:.w-3]
    [:button.btn.g-recaptcha
     (merge (when site-key
              {:data-sitekey  site-key
               :data-callback "submitSignin"})
            {:type "submit"})
     "Sign in"]]
   (when-some [error (:error params)]
     [:<>
      [:.h-1]
      [:.text-sm.text-red-600
       (case error
         "recaptcha" (str "You failed the recaptcha test. Try again, "
                          "and make sure you aren't blocking scripts from Google.")
         "invalid-email" "Invalid email. Try again with a different address."
         "send-failed" (str "We weren't able to send an email to that address. "
                            "If the problem persists, try another address.")
         "invalid-link" "Invalid or expired link. Sign in to get a new link."
         "not-signed-in" "You must be signed in to view that page."
         "There was an error.")]])
   [:.h-1]
   [:.text-sm "Don't have an account yet? " [:a.link {:href "/"} "Sign up"] "."]
   [:.h-3]
   biff/recaptcha-disclosure))


(defn link-sent [{:keys [params]
                  :as   ctx}]
  (ui/page
   ctx
   [:h2.text-xl.font-bold "Check your inbox"]
   [:p "We've sent a sign-in link to " [:span.font-bold (:email params)] "."]))

(defn verify-email-form [params]
  (biff/form
   {:action "/auth/verify-link"
    :hidden {:token (:token params)}}
   [:div [:label {:for "email"}
          "サインアップしたブラウザとは違うブラウザで認証リンクを開いたようです。念の為メールアドレスをもう一度入れてください"]]
   [:.h-3]
   [:.flex
    [:input#email {:name        "email"
                   :type        "email"
                   :placeholder "Enter your email address"}]
    [:.w-3]
    [:button.btn {:type "submit"}
     "Sign in"]]))

(defn verify-email-page [{:keys [params app-name]
                          :as   ctx}]
  (ui/page
   ctx
   [:h2.text-2xl.font-bold (str "Sign up for " app-name)]
   [:.h-3]
   (verify-email-form params)
   (when-some [error (:error params)]
     [:.h-1]
     [:.text-sm.text-red-600
      (case error
        "incorrect-email" "Incorrect email address. Try again."
        "There was an error.")])))

(defn code-form [site-key params]
  (biff/form
   {:action "/auth/verify-code"
    :id     "code-form"
    :hidden {:email (:email params)}}
   (biff/recaptcha-callback "submitCode" "code-form")
   [:div [:label {:for "code"} "こちらのEmailにお送りした6桁のコードを入力してください ："
          [:span.font-bold (:email params)]]]
   [:.h-1]
   [:.flex
    [:input#code {:name "code"
                  :type "text"}]
    [:.w-3]
    [:button.btn.g-recaptcha
     (merge (when site-key
              {:data-sitekey  site-key
               :data-callback "submitCode"})
            {:type "submit"})
     "Sign in"]]))

(defn send-another-code-button [site-key params]
  (biff/form
   {:action "/auth/send-code"
    :id     "signin"
    :hidden {:email    (:email params)
             :on-error "/signin"}}
   (biff/recaptcha-callback "submitSignin" "signin")
   [:button.link.g-recaptcha
    (merge (when site-key
             {:data-sitekey  site-key
              :data-callback "submitSignin"})
           {:type "submit"})
    "別のコードを送信する"]))


(defn enter-code-page [{:keys [recaptcha/site-key params]
                        :as   ctx}]
  (ui/page
   (assoc ctx ::ui/recaptcha true)
   (code-form site-key params)
   (when-some [error (:error params)]
     [:.h-1]
     [:.text-sm.text-red-600
      (case error
        "invalid-code" "Invalid code."
        "There was an error.")])
   [:.h-3]
   (send-another-code-button site-key params)))