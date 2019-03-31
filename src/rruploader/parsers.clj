(ns rruploader.parsers)

(defn hhmm-parser-x
  [v]
  (let [dtime (try (Double/parseDouble v) (catch Exception e Double/NaN))]
    (when-not (Double/isNaN dtime)
      (let [rawhh (* dtime 24)
            hh (int rawhh)
            mm (-> rawhh (mod hh) (* 60) int)]
        (format "%02d:%02d" hh mm)))))

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
