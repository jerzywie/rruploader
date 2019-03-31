(ns rruploader.parsers)

(defn none-parser
  [v]
  v)


(defn dmy-parser
  [date]
  (try
    (.format (java.text.SimpleDateFormat. "EEE dd MMM") date)
    (catch Exception e (do (println "caught ex") date))))

(defn hhmm-parser
  [date]
  (try
    (.format (java.text.SimpleDateFormat. "HH:mm") date)
    (catch Exception e (do (println "caught ex") date))))
