(ns learnoffline.core
  (:require [reagent.core :as reagent]
            [goog.dom :as gdom]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce app-state
  (reagent/atom {:online true
                 :data1 "data1"
                 :data2 "data2"
                 :data3 "data3"}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Util function

(defn submithandler []
  (let [online (:online @app-state)
        data1 (:data1 @app-state)
        data2 (:data2 @app-state)
        data3 (:data3 @app-state)]
    (.log js/console "submit handler!!!") 
    (if online
      (do 
        (.log js/console "online")
        (.log js/console "Data has been saved online. (But not in this demo)"))
      (do
        (.log js/console "offline")
        (.setItem (.-sessionStorage js/window) (str :data1) (str data1))
        (.setItem (.-sessionStorage js/window) (str :data2) (str data2))
        (.setItem (.-sessionStorage js/window) (str :data3) (str data3))
        (.log js/console "Data has been saved offline.")))))
      
(defn loaddata []
  (let [online (:online @app-state)]
    (if online
      (.log js/console "Currently online: \n data could be loaded from server")
      (let [data1 (.getItem (.-sessionStorage js/window) (str :data1))
            data2 (.getItem (.-sessionStorage js/window) (str :data2))
            data3 (.getItem (.-sessionStorage js/window) (str :data3))]
        (swap! app-state assoc :data1 data1)
        (swap! app-state assoc :data2 data2)
        (swap! app-state assoc :data3 data3)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Components

(defn status []
  (if (:online @app-state)
    [:p#status "Online"]
    [:p#status {:class "offline"} "Offline"]))

(defn maindata []
  (let [data1 (:data1 @app-state)
        data2 (:data2 @app-state)
        data3 (:data3 @app-state)]
    [:div 
     [:label "Data item 1: "
      [:input#data1 {:type "text" 
                     :name "data1" 
                     :value data1
                     :onChange (fn [_]
                                 (let [v (.-value (gdom/getElement "data1"))]
                                   (swap! app-state assoc :data1 v)))}]]
     [:label "Data item 2: "
      [:input#data2 {:type "text" 
                     :name "data2" 
                     :value data2
                     :onChange (fn [_]
                                 (let [v (.-value (gdom/getElement "data2"))]
                                   (swap! app-state assoc :data2 v)))}]]
     [:label "Data item 3: "
      [:input#data3 {:type "text" 
                     :name "data3" 
                     :value data3
                     :onChange (fn [_]
                                 (let [v (.-value (gdom/getElement "data3"))]
                                   (swap! app-state assoc :data3 v)))}]]]))
(defn submit []
  [:button {:type "button"
            :onClick #(submithandler)}
   "Save Data"])    

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page

(defn page [ratom]
  [:div
   [status]
   [:h1 "Offline test page"]
   [:form#mainform {:action "index.html" :method "post"}
    [:fieldset
      [:legend "Udpate data"]
      [maindata]
      [submit]]]
   [:p "Use File > Work Offline in Firefox to switch online/offline modes."]
   [:p 
    [:a {:href "index.html"} "Refresh the page"] " in offline mode to reload data from store."]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn handleOnOff []
  (let [status (aget js/navigator "onLine")]
    (swap! app-state assoc :online status)
    (.log js/console (str "status: " status))))  

(defn addHandlerForOnOff []
  (.addEventListener js/window "online" #(handleOnOff))
  (.addEventListener js/window "offline" #(handleOnOff)))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn reload []
  (reagent/render [page app-state]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (.log js/console "main")
  (dev-setup)
  (addHandlerForOnOff)
  (reload)
  (handleOnOff)
  (loaddata))
