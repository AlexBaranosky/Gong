(ns gong.core
  (:require [speech-synthesis.say :refer [say]])
  (:import [java.io BufferedInputStream File FileInputStream]
           [javazoom.jl.player Player]))

(defn- play-wav [file-name]
  (when-not (.exists (File. file-name))
    (throw (Exception. (str "Couldn't find wav file: " file-name))))

  (let [absolute-name (.getAbsolutePath (File. file-name))
        input-stream (BufferedInputStream. (FileInputStream. absolute-name))]
    (doto (Player. input-stream)
      (.play)
      (.close))))

(def ^:private secs-per-min 60)
(def ^:private secs-per-hour 3600)
(def ^:private millis-per-sec 1000)

(defn- parse-millis-from
  "seconds from string such as \"00:05:00\" ;=> (300 seconds)"
  [string]
  (let [[_ hours mins secs] (re-matches #"^(\d\d):(\d\d):(\d\d)$" string)]
    (when-not hours
      (throw (Exception. (format "Duration, %s, was not formatted correctly." string))))
    (let [[hours mins secs] (map #(Integer/parseInt %) [hours mins secs])]
      (* millis-per-sec (+ secs
                           (* mins secs-per-min)
                           (* hours secs-per-hour))))))

(defn- exec-practice-routine
  "practice-routine-sections looks like:
   [ {:duration \"00:01:00\" :message \"do something\"} {...} ...]"
  [practice-routine-sections]

  (println "** Routine Summary **")
  (doseq [{:keys [duration message]} practice-routine-sections] 
    (println (str duration " " message)))

  (doseq [{:keys [duration message]} practice-routine-sections]
    (say message)
    (Thread/sleep (parse-millis-from duration))
    (play-wav "blong2.wav")))

(defn -main []
  (-> "routine.cljdata"
       slurp
       read-string
       exec-practice-routine))