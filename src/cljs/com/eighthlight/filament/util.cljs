(ns com.eighthlight.filament.util
  (:require-macros [hiccups.core :as h])
  (:require [clojure.string :as string]
            [clojure.walk :as walk]
            [domina :as dom]
            [domina.css :as css]
            [domina.events :as event]
            [hiccups.runtime]))

(defn ->options
  "Takes keyword argument and converts them to a map.  If the args are prefixed with a map, the rest of the
  args are merged in."
  [options]
  (if (map? (first options))
    (merge (first options) (apply hash-map (rest options)))
    (apply hash-map options)))


(defn path-with-query-params [path params]
  (str path
    (when-let [query-params (seq (walk/stringify-keys params))]
      (str "?"
        (string/join "&" (map (fn [segment]
                                (string/join "=" (map #(js/encodeURIComponent %)
                                                   segment)))
                           query-params))))))

(defn ->json [data]
  (.stringify js/JSON (clj->js data)))

(defn current-time-millis []
  (.getTime (js/Date.)))

; CLJS ONLY BELOW

(def ENTER 13)
(def ESC 27)
(def SPACE 32)
(def UP_ARROW 38)
(def DOWN_ARROW 40)
(defn ENTER? [e] (= ENTER (.-keyCode (event/raw-event e))))
(defn ESC? [e] (= ESC (.-keyCode (event/raw-event e))))
(defn SPACE? [e] (= SPACE (.-keyCode (event/raw-event e))))
(defn UP_ARROW? [e] (= UP_ARROW (.-keyCode (event/raw-event e))))
(defn DOWN_ARROW? [e] (= DOWN_ARROW (.-keyCode (event/raw-event e))))

(def not-blank? (complement string/blank?))

(defn errors->messages [errors]
  (mapcat
    (fn [[key messages] error]
      (map #(str (name key) " " %) messages))
    errors))

(def default-error "There was a problem.")

(defn flash-error [text]
  (let [message (if (string/blank? text)
                  default-error
                  text)]
    (dom/set-html! (css/sel "#flash-container")
      (h/html [:div.flash [:h2.error message]]))))

(defn clear-flash []
  (dom/set-html! (css/sel "#flash-container") ""))

(defn remove-loading-placeholder [name]
  (dom/detach! (dom/by-id (str name "-loading-placeholder"))))

(defn override-click! [nodes action]
  (event/listen! nodes
    :click (fn [e]
             (event/prevent-default e)
             (action e))))

(defn element-id [element]
  (dom/attr element :id))
