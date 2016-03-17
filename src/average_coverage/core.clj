(ns average-coverage.core
  (:require [clojure-csv.core :as csv]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [schema.core :as s]
            [schema.coerce :as coerce]
            [clojure.java.io :as io]))


(comment (use 'clojure.java.io)
(with-open [wrtr (writer "test.txt")]
  (.write wrtr (str (-main)))))

(set! *warn-on-reflection* true)
(set! *print-length* 4000)

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
         (map #(coercion dataSchema %)))))

(defn group-by-gene [data] 
  (group-by #(select-keys % [:gene]) data))

(defn -main [filename-str]
  (let [grouped-data (->> filename-str
                          (load-csv)
                          (map #(select-keys % [:gene :percentage_coverage]))
                          (group-by-gene))
        genes (->> (map first grouped-data)
                   (map :gene))
        percentage (->> grouped-data
                     (map #(second %)) 
                     (map #(vec (map :percentage_coverage %))))
        remove-higher (map (fn [x] (map #(if (>= 100.0 %)
                                %) x)) percentage)
        filtered (map (fn [x] (vec (filter number? (vec x)))) remove-higher)
        mean-coverage (map #(double (/ (reduce + %)
                                      (count %))) filtered)
        output (zipmap genes mean-coverage)]
    (with-open [file-out (io/writer "average_coverage.txt")]
      (spit file-out output))))


