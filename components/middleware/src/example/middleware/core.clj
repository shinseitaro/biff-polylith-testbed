(ns example.middleware.core
  (:require [com.biffweb :as biff]
            [muuntaja.middleware :as muuntaja]
            [ring.middleware.anti-forgery :as csrf]
            [ring.middleware.defaults :as rd]))


(defn wrap-redirect-signed-in
  "セッションIDを持っていればロケーションへリダイレクトするハンドラ"
  [handler]
  (fn [{:keys [session head-to]
        :as   ctx}]
    (if (some? (:uid session))
      {:status  303
       :headers {"location" (if head-to head-to "/app")}}
      (handler ctx))))

(defn wrap-signed-in
  "セッションIDを持っていなければサインインエラーを返すハンドラ"
  [handler]
  (fn [{:keys [session]
        :as   ctx}]
    (if (some? (:uid session))
      (handler ctx)
      {:status  303
       :headers {"location" "/siginin?error=not-signed-in"}})))

(defn wrap-debug
  "middlware をデバグするためのミドルウェアハンドラ"
  [handler]
  (fn [ctx]
    (let [response (handler ctx)]
      (println "リクエスト：")
      (biff/pprint ctx)
      (def ctx* ctx)
      (println "レスポンス：")
      (biff/pprint response)
      (def response* response)
      response)))

(defn wrap-site-defaults
  "ウェブページに表示するためのミドルウェア関数"
  [handler]
  (-> handler
      ;; wrap-debug
      biff/wrap-render-rum
      biff/wrap-anti-forgery-websockets
      csrf/wrap-anti-forgery
      biff/wrap-session
      muuntaja/wrap-params
      muuntaja/wrap-format
      (rd/wrap-defaults (-> rd/site-defaults
                            (assoc-in [:security :anti-forgery] false)
                            (assoc-in [:responses :absolute-redirects] true)
                            (assoc :session false)
                            (assoc :static false)))))

(defn wrap-api-defaults
  "誰でもアクセスできるOpen API用のミドルウェア関数"
  [handler]
  (-> handler
      muuntaja/wrap-params
      muuntaja/wrap-format
      (rd/wrap-defaults rd/api-defaults)))

(defn wrap-base-defaults [handler]
  (-> handler
      biff/wrap-https-scheme
      biff/wrap-resource
      biff/wrap-internal-error
      biff/wrap-ssl
      biff/wrap-log-requests))
