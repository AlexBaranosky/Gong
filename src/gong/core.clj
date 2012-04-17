(ns gong.core

;  (:use freetts)
  (:import [java.applet Applet]
           [java.io File]
           [java.net URL]
           [com.sun.speech.freetts VoiceManager]))

(defn play-wav [file-name]
  (when-not (.exists (File. file-name))
    (throw (Exception. "couldn't find wav file")))

  (let [play-url (fn [url-string]
                   (.play (Applet/newAudioClip (URL. url-string))))
        absolute-name (.getAbsolutePath (File. file-name))
        url-string (str "file:/" absolute-name)]
    (println url-string)
    (play-url url-string)))

(defn- speak [msg]
;  (doto (.getVoice (VoiceManager/getInstance) "kevin16")
;    (.startBatch)
;    (.speak msg)
;    (.endBatch))
  (println (str "Speaking: " msg "...")))

(def ^:private sec-per-min 60)
(def ^:private sec-per-hour 360)
(def ^:private millis-per-sec 1000)

(defn- parse-secs-from
  "seconds from string such as \"00:05:00\" ;=> (300 seconds)"
  [string]
  (let [[_ hours mins secs] (re-matches #"^\d\d:\d\d:\d\d$" string)]
    (when-not hours
      (throw (Exception. (format "Duration, %s, was not formatted correctly." string))))
    (let [[hours mins secs] (map #(Integer/parseInt %) [hours mins secs])]
      (+ secs
        (* mins sec-per-min)
        (* hours sec-per-hour)))))


(defn- exec-practice-routine
  "practice-routine-sections looks like:
   [ {:duration \"00:01:00\" :message \"do something\"} {...} ...]"
  [practice-routine-sections]
  (doseq [{:keys [duration message]} practice-routine-sections]
    
    (speak message)
;    (Thread/sleep (* millis-per-sec (parse-secs-from duration)))
    (play-wav "/resources/blong2.wav")
    ))

(defn -main []
  (-> "routine.cljdata"
    slurp
    read-string
    exec-practice-routine))