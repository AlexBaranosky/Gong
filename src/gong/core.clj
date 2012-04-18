(ns gong.core
  (:import [java.applet Applet]
           [java.io File]
           [java.net URL]
           [com.sun.speech.freetts VoiceManager]
           [javazoom.jl.player Player]))

(defn play-wav [file-name]
  (when-not (.exists (File. file-name))
    (throw (Exception. (str "Couldn't find wav file: " file-name))))

  (let [absolute-name (.getAbsolutePath (File. file-name))
        bis (java.io.BufferedInputStream. (java.io.FileInputStream. absolute-name))]
    (println (str "Playing: " absolute-name))
    (doto (Player. bis)
      (.play)
      (.close))))

(defn- speak [msg]
;  (doto (.getVoice (VoiceManager/getInstance) "kevin16")
;    (.startBatch)
;    (.speak msg)
;    (.endBatch))
  (println (str "Speaking: " msg)))

(def ^:private secs-per-min 60)
(def ^:private secs-per-hour 3600)
(def ^:private millis-per-sec 1000)

(defn- parse-secs-from
  "seconds from string such as \"00:05:00\" ;=> (300 seconds)"
  [string]
  (let [[_ hours mins secs] (re-matches #"^(\d\d):(\d\d):(\d\d)$" string)]
    (when-not hours
      (throw (Exception. (format "Duration, %s, was not formatted correctly." string))))
    (let [[hours mins secs] (map #(Integer/parseInt %) [hours mins secs])]
      (+ secs
        (* mins secs-per-min)
        (* hours secs-per-hour)))))

(defn- exec-practice-routine
  "practice-routine-sections looks like:
   [ {:duration \"00:01:00\" :message \"do something\"} {...} ...]"
  [practice-routine-sections]
  (doseq [{:keys [duration message]} practice-routine-sections]
    
    (speak message)
    (println (str "Duration: " (parse-secs-from duration) " secs"))
    (Thread/sleep (* millis-per-sec (parse-secs-from duration)))
    (play-wav "blong2.wav")
    (println "Done.\n")))

(defn -main []
  (-> "routine.cljdata"
       slurp
       read-string
       exec-practice-routine))