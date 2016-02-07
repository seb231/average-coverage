(ns average-coverage.core
  (:require [clojure-csv.core :as csv]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(defn load-csv
  [filename]
  (let [normalised-data (-> (slurp filename)
                            (clojure.string/replace "\r\n" "\n")
                            (clojure.string/replace "\r" "\n"))
        parsed-csv (csv/parse-csv normalised-data :end-of-line nil)
        parsed-data (rest parsed-csv)
        headers (->> (first parsed-csv)
                     (map str/lower-case)
                     (map #(str/replace % #"\s" ".")))]
    (map #(walk/keywordize-keys (zipmap headers %1)) parsed-data)))

(defn write-csv
  [seq-of-maps filename-string]
  (let [headers (map name (-> seq-of-maps (first) keys))
        data-to-csv   (cons headers (map #(map str (vals %)) seq-of-maps))
        csv    (csv/write-csv data-to-csv)]
    (spit filename-string csv)))


