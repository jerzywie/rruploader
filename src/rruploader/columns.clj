(ns rruploader.columns
  (:require [rruploader.parsers :as p]
            [clojure.string :refer [split upper-case trim]]))

(defn- keyword-list
  "Given a start and end ASCII char number
   generate a list of keywords prepended with
   an optional identifier"
  ([start-range end-range identifier]
   (when (> start-range end-range) (throw (Exception. "Column ranges must increase.")))
   (map #(->> % char (str identifier) keyword) (range start-range (inc end-range))))
  ([start-range end-range]
   (keyword-list start-range end-range nil)))

(defn- singlerange->colmap
  "Converts a single column range into a map
   suitable for docjure/select-columns.

   E.g. 'A-c' gives
   {:A :colA :B :colB :C :colC}"
  [column-list]
  (let [column-ranges (-> column-list upper-case (split #"-"))
        charnum-ranges (map int (reduce str column-ranges))
        start (first charnum-ranges)
        end (last charnum-ranges)
        key-list (keyword-list start end)
        val-list (keyword-list start end "col")]
    (zipmap key-list val-list)))

(defn ranges->colmap
  "Converts a list of columns ranges into a map
   suitable for docjure/select-columns.

   Uses the same conventions for the column-list as 'cut'.

   E.g. 'A-c,f,k-L' gives
   {:A :colA :B :colB :C :colC :F :colF :K :colK :L :colL}"
  [column-list]
  (when column-list
    (let [ranges (-> column-list upper-case (split #","))]
      (apply merge (map singlerange->colmap ranges)))))

(defn parsername->parserfn
  [name]
  (cond
    (= name "HHMM") p/hhmm-parser
    (= name "DMY") p/dmy-parser
    (= name "DESC") p/description-parser
    :else p/none-parser))

(defn column-parser
  "Processes a single column parser spec into a map
   of column to parser"
  [col-parser]
  (let [raw-pair (-> col-parser trim (split #":"))]
    {(keyword (first raw-pair)) (parsername->parserfn (second raw-pair))}))

(defn parsers->parsermap
  "Converts a parsers list into a map of columns to parsers
   A parsers list looks like C:HHMM,D:NONE,G:DESC
   Any columns without parsers are assigned to the default none-parser."
  [parsers colmap]
  (let [parse-pairs (-> parsers upper-case (split #","))
        parser-map (apply merge (map column-parser parse-pairs))
        all-keys (vals colmap)
        all-vals (map (fn [k] (get parser-map k p/none-parser)) (keys colmap))]
    (zipmap all-keys all-vals)))

