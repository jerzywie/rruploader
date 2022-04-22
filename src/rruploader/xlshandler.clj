(ns rruploader.xlshandler
  (:use [dk.ative.docjure.spreadsheet] :reload-all)
  (:require [rruploader
             [columns :as col]
             [parsers :as par]
             [emitters :as em]]
            [clojure.string :as string]))

(defn printnpasson-first
  "-> debug"
  [arg msg]
  (println "\n=== debug " msg "====================")
  (prn arg)
  arg)

(defn printnpasson-last
  "->> debug"
  [msg arg]
  (println "\n=== debug " msg "====================")
  (prn arg)
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

(defn apply-parsers
  [header-map parser-map extra-cols-list line-map-list]
  (for [line-map line-map-list]
    (for [[key val] line-map]
      (let [outkey (key header-map)
            parsefn (key parser-map par/none-parser)]
        (parsefn outkey val extra-cols-list)))))

(defn class-list
  [& classes]
  (let [class-string (->> classes (interpose " ") (apply str) (string/trim))]
    {:class class-string}))

(defn xls->map
  [{:keys [columns styles first-line last-line header-line parsers extra-cols output]} file]
  (let [colmap (col/ranges->colmap columns)
        extra-cols-list (string/split extra-cols #",")
        parsermap (col/parsers->parsermap parsers colmap)
        sheet (load-spreadsheet file)
        header-vals (first (get-table-rows colmap header-line sheet))
        emit-format (-> output string/lower-case keyword)]
    (when (some #{:nilkey} (set (keys parsermap)))
      (throw (Exception. "All parsers must refer to columns specified in the 'columns' parameter.")))
    (->> sheet
         (get-table-rows colmap first-line last-line)
         (apply-parsers header-vals parsermap extra-cols-list)
         (map #(apply merge %))
         (em/emit {:format emit-format}))))
