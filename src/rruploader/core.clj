(ns rruploader.core
  (require [rruploader.xlshandler :as xh]
           [clojure.tools.cli :refer [parse-opts]])
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
   ["-p" "--parsers COLUMNPARSESPEC" "List of columns to parse in a specific way (col:parser. parsers available HHMM, TEXT. e.g. C:HHMM parses column C as a time in HH:MM format)"
    :default ":NONE"]
   ["-o" "--output OUTPUTTYPE" "Type of output: HTML or ICAL"
    :default "HTML"]])

(defn cli-help
  [errors options-summary]
  (when errors (println errors))
  (println "Usage: rruploader <options> spreadsheet-file-name")
  (println " where options are:")
  (println options-summary))

(defn -main
  "Read Excel file with timetable details.
   Emit HTML table."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (try
      (cond
        (< (count arguments) 1) (cli-help "[spreadsheet-file-name must be supplied]" summary)
        errors (cli-help errors summary)
        (nil? (:columns options)) (cli-help "--columns must be used to specify the columns to process" summary)
        :else (println (xh/xls->htmltable options (first arguments))))
      (catch Exception e (cli-help (str "[" (.getMessage e) "]") summary)))))
