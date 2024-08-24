(ns example.home.core.single-optin.core
  (:require [com.biffweb :as biff]))

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