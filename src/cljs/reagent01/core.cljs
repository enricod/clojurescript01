(ns reagent01.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]))

;; -------------------------
;; Views



(defn input-element
  "An input element which updates its value and on focus parameters on change, blur, and focus"
  [id name type value in-focus]
  [:input {:id id
           :name name
           :class "form-control"
           :type type
           :required ""
           :value @value
           :on-change #(reset! value (-> % .-target .-value))
           ;; Below we change the state of in-focus
           :on-focus #(swap! in-focus not)
           :on-blur #(swap! in-focus not)}])



(defn input-and-prompt
  "Creates an input box and a prompt box that appears above the input when the input comes into focus."
  [label-value input-name input-type input-element-arg prompt-element]
  (let [input-focus (atom false)]
    (fn []
      [:div
       [:label label-value]
       (if @input-focus prompt-element [:div])
       [input-element input-name input-name input-type input-element-arg input-focus]])))



(defn prompt-message
  "A prompt that will animate to help the user with a given input"
  [message]
  [:div {:class "my-messages"}
   [:div {:class "prompt message-animation"} [:p message]]])


;;specific function
(defn email-prompt
  []
  (prompt-message "What's your email address?"))


(defn email-form
  [email-address-atom]
  (input-and-prompt "email"
                    "email"
                    "email"
                    email-address-atom
                    [prompt-message "What's your email?"]))



(defn home-page []
  (let [email-address (atom "a@b.it")]
    (fn []
      [:div
      [:div {:class "signup-wrapper"}
       [:h2 "Welcome to TestChimp"]
       [:form
        [email-form email-address]]]
        [:div [:a {:href "/about"} "go to the about page"]]
       ])))


(defn about-page []
  [:div [:h2 "About qaria"]
   [:div [:a {:href "/"} "go to the home page"]]])


(defn current-page []
  [:div [(session/get :current-page)]]   )


;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
