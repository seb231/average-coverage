(ns average-coverage.core
  (:require [clojure-csv.core :as csv]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [schema.core :as s]
            [schema.coerce :as coerce]))

(set! *print-length* 10)
(set! *warn-on-reflection* true)

(def dataSchema
  {:percentage_coverage Double
   :length s/Num
   s/Keyword s/Any})

(defn coercion [schema data]
  (let [coerce-data-fn (coerce/coercer schema coerce/string-coercion-matcher)]
    (coerce-data-fn data)))

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
    (->> (map #(walk/keywordize-keys (zipmap headers %1)) parsed-data)
         (map #(coercion dataSchema %))
         )))

(defn write-csv
  [seq-of-maps filename-string]
  (let [headers (map name (-> seq-of-maps (first) keys))
        data-to-csv   (cons headers (map #(map str (vals %)) seq-of-maps))
        csv    (csv/write-csv data-to-csv)]
    (spit filename-string csv)))

(defn group-by-gene [data] 
  (group-by #(select-keys % [:gene]) data))

(defn mean [xs]
  (/ (reduce + xs)
     (count xs)))

(defn sum-over-grouped-map [keys grouped-data]
  (map (fn [[key value]]
         (merge key (mean (map #(keys %) value)))) grouped-data))

(defn -main []
  (->> (load-csv "data/pulldown.coverage.csv")
       (group-by-gene)
       (map #(map (fn [seb] (map (fn [here] (:percentage_coverage here)) seb)) %))
       ;;(map #(mean (map (fn [[key value]] (:percentage_coverage value) %))))
       ))
