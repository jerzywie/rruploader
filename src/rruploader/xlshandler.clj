(ns rruploader.xlshandler
  (:use [dk.ative.docjure.spreadsheet] :reload-all)
  (:require [rruploader
             [columns :as col]
             [parsers :as par]]
            [hiccup
             [core :as h]
             [page :as p]]))

(defn printnpasson-first
  "-> debug"
  [arg msg]
  (println "\n=== debug " msg "====================")
  (println arg)
  arg)

(defn printnpasson-last
  "->> debug"
  [msg arg]
  (println "\n=== debug " msg "====================")
  (println arg)
  arg)

(defn load-spreadsheet
  "Loads the first sheet in a workbook"
  [file]
  (let [wb (load-workbook file)
        sheet (select-sheet (fn [x] true) wb)]
    sheet))

(defn get-table-rows
  ([columns line sheet]
   (get-table-rows columns line line sheet))
  ([columns first-line last-line sheet]
   (let [drop-lines (dec first-line)
         take-lines (- last-line first-line -1)]
     (->> sheet
          (select-columns columns)
          (drop drop-lines)
          (take take-lines)))))

(defn format-date
  [date]
  (try
    (.format (java.text.SimpleDateFormat. "EEE dd MMM") date)
    (catch Exception e (do (println "caught ex") date))))

(defn do-formatting
  [val]
  (let [tval (type val)]
    (cond
      (= java.util.Date tval) (format-date val)
      :else val)))

(defn class-list
  [& classes]
  (let [class-string (->> classes (interpose " ") (apply str) (clojure.string/trim))]
    {:class class-string}))

(defn emit-html-table-header
  [line]
  (let [svals (vals (into (sorted-map) line))]
      [:tr
       (for [val svals]
         [:th val])]))

(defn emit-html-table-rows-generic
  [parsermap table]
  (for [line table]
    (do (prn "line:" line)
     [:tr
      (for [[key val] line]
        (let [parsefn (key parsermap identity)]
            (do
              (prn "k:" key " v:" val)
              [:td  (parsefn val)])))])))

(defn xls->htmltable
  [{:keys [columns styles first-line last-line header-line parsers]} file]
  (let [colmap (col/ranges->colmap columns)
        parsermap (col/parsers->parsermap parsers colmap)
        hdrfn (comp emit-html-table-header (partial get-table-rows colmap header-line))
        bodyfn (comp (partial emit-html-table-rows-generic parsermap) (partial get-table-rows colmap first-line last-line))
        txfn (juxt hdrfn bodyfn)]
    (prn "parsermap:" parsermap)
    (when (some #{:nilkey} (set (keys parsermap)))
      (throw (Exception. "All parsers must refer to columns specified in the 'columns' parameter.")))
    (->> file
         load-spreadsheet
         txfn
         (apply conj)
         h/html)))
