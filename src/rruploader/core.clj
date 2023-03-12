(ns rruploader.core
  (:require [rruploader.xlshandler :as xh]
            [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :refer [join]])
  (:gen-class))

(defn int-parse
  [n]
  (try (Integer/parseInt n) (catch Exception e Integer/MAX_VALUE)))

(def cli-options
  [;; First three strings describe a short-option, long-option with optional
   ;; example argument description, and a description. All three are optional
   ;; and positional.
   ["-h" "--header-line HEADER" "Line with table header"
    :parse-fn int-parse
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]
    :default nil]
   ["-f" "--first-line FIRST" "First line of table body"
    :parse-fn int-parse
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]
    :default 1]
   ["-l" "--last-line LAST" "Last line of table body"
    :parse-fn int-parse
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]
    :default 65536]
   ["-s" "--styles STYLES" "Line with css styles"
    :parse-fn int-parse
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]
    :default nil]
   ["-c" "--columns COLUMNSPEC" "List of columns ('cut'-like syntax: A-D,F,H)"
    :default nil]
   ["-p" "--parsers COLUMNPARSESPEC" "List of columns to parse in a specific way (col1:parser1,col2:parser2... See below for more information)."
    :default "*:NONE"]
   ["-x" "--extra-cols COLTITLES" "List of extra column titles to generate from description parser."
    :default nil]
   ["-o" "--output OUTPUTTYPE" "Type of output: HTML or JSON"
    :default "HTML"]])

(def parser-help (join "\n" [""
                             "A parsers list looks like C:HHMM,D:NONE,B:OtherParser..."
                             ""
                             "Available parsers:"
                             "HHMM : formats the time part of a date into HH:MM format (e.g. 0.75 = '18:00')."
                             "DMY  : formats  a date as <day-of the week> <day of the month> <month>."
                             "DESC : extracts all the key value pairs from the 'Description' field."
                             ""
                             "The default parser is 'NONE'. The default value is *:NONE which means 'for all columns, use NONE as the parser'."
                             ]))

(defn cli-help
  [errors options-summary extra-help]
  (when errors (println errors))
  (println "Usage: rruploader <options> spreadsheet-file-name")
  (println " where options are:")
  (println options-summary)
  (println extra-help))

(defn -main
  "Read Excel file with timetable details.
   Emit other data."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (try
      (cond
        (< (count arguments) 1) (cli-help "[spreadsheet-file-name must be supplied]" summary parser-help)
        errors (cli-help errors summary parser-help)
        (nil? (:columns options)) (cli-help "--columns must be used to specify the columns to process" summary)
        :else (println (xh/xls->map options (first arguments))))
      (catch Exception e (do (println (str "[" (.toString e) "] " ))
                             (.printStackTrace e))))))
