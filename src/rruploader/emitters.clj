(ns rruploader.emitters
  [:require [clojure.data.json :as json]
            [hiccup.core :as h]])

(defn emit-html-table-header
  [line-maps]
  (let [svals (keys (first line-maps))]
    (h/html [:tr
             (for [val svals]
               [:th val])])))

(defn emit-html-table-rows-generic
  [line-maps]
  (h/html (for [line line-maps]
            [:tr
             (for [[key val] line]
               [:td val])])))


(defmulti emit :format)

(defmethod emit :html [_ data]
  (let [jxfn (juxt emit-html-table-header emit-html-table-rows-generic)]
    (->> data
         jxfn
         (reduce str))))

(defmethod emit :json [_ data]
  (json/write-str data))
