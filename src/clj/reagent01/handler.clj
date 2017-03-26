(ns reagent01.handler
  (:require
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [compojure.core :refer [GET defroutes routes]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [reagent01.middleware :refer [wrap-middleware]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
            [config.core :refer [env]])  )




(def mount-target
  [:div#app
      [:h3 "ClojureScript has not been compiled!"]
      [:p "please run "
       [:b "lein figwheel"]
       " in order to start the compiler"]])


(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])


(defn loading-page []
  (html5
    (head)
    [:body {:class "body-container"}
     mount-target
     (include-js "/js/app.js")]))



(defn stazioni []
    (let [name  "John Doe"]
     {:status 200
       :body {:name name
       :desc (str "The name you sent to me was " name)}}))


(defroutes apiroutes
  (GET "/api/stazioni" [] (stazioni ))  )

(defroutes approutes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))

  (resources "/")
  (not-found "Not Found"))

(def app
    (routes
        (wrap-json-response apiroutes api-defaults  )
        (wrap-defaults approutes site-defaults)))


