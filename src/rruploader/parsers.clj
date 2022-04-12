(ns rruploader.parsers
  (:require [clojure.string :as string]))

(defn none-parser
  [v]
  v)


(defn dmy-parser
  [date]
  (try
    (.format (java.text.SimpleDateFormat. "EEE dd MMM") date)
    (catch Exception e (do (println "Error while attempting to parse'" date "' as HHMM")))))

(defn hhmm-parser
  [date]
  (try
    (.format (java.text.SimpleDateFormat. "HH:mm") date)
    (catch Exception e (do (println "Error while attempting to parse'" date "' as HHMM")))))

(defn- extract-between-delimiters
  "Extract text between the lh and rh delimiters.
   Use start of string and end of string respectively if
   delimiters aren't matched."
  [astr lh-delim rh-delim]
  (let [lhs-index (string/index-of astr lh-delim)
        rhs-index (string/last-index-of astr rh-delim)
        lhs (if lhs-index (inc lhs-index) 0)
        rhs (if rhs-index rhs-index (count astr))]
    (->> astr
         (take rhs)
         (drop lhs)
         (apply str))))

(defn- extract-before-delimiter
  "Extract any text before a specific delimiter."
  [astr delim]
  (let [index (string/index-of astr delim)
        num-before (if index index 0)]
    (->> astr
         (take num-before)
         (apply str))))

(defn- extract-kandv
  "Extract the key and value from a string
   in the format 'key(value)"
  [kv]
  (let [pa "("
        ren ")"]
    {(string/trim (extract-before-delimiter kv pa))
     (string/trim (extract-between-delimiters kv pa ren))}))

(defn description-parser
  "Parse a description line in a very peculiar format:
   <location> [<key-value list]
   where:
   <location>         the location
   <key-value list>   a list of <key-value pair> delimited with ; and surrounded with []
   <key-value pair>   in the format <key> (<value>)
   Return a map of the key-value pairs."
  [description]
  (let [kvs (extract-between-delimiters description "[" "]")
        result (->> (string/split kvs #";")
                    (map extract-kandv)
                    (apply merge))]
    result))
