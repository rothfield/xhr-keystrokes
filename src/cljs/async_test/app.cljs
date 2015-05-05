(ns async-test.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [goog.dom :as dom]
            [goog.Uri] 
            [goog.net.XhrIo]
            [goog.net.Jsonp]
            [goog.events :as events]
            [cljs.core.async :refer [close! timeout put! chan <!]])
  )

(def wiki-url "http://en.wikipedia.org/w/api.php?action=opensearch&format=json&search="
  )

(defn jsonp [uri]
  (let [out (chan)
        req (goog.net.Jsonp. (goog/Uri. uri))]
    (.send req nil (fn [res] (put! out res)))
    out))

(defn listen [el type]
  (let [out (chan 1)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

(def seconds 1000)

(defn user-query []
  (.-value (dom/getElement "query")))

(defn render-query [results]
  (str
    "<ul>"
    (apply str
           (for [result results]
             (str "<li>" result "</li>")))
    "</ul>"))

(defn init []
  (let [ results-view (dom/getElement "results")
        last-value (atom "") 
        keypresses (listen (dom/getElement "query") "keypress")]
    (go (while true
          (<! keypresses)
          (let [cur-value (user-query)]
            (when (not= cur-value @last-value) 
              (reset! last-value cur-value)
              (let [
                    results (<! (jsonp (str  wiki-url cur-value)))
                    ]
                (set! (.-innerHTML results-view) (render-query results))
                )
              (<! (timeout (* 6 seconds))) 
              ))
          ))))

