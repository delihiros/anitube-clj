(ns anitube-clj.gui
  (:require [fx-clj.core :as fx]
            [anitube-clj.api :as api]
            [clj-http.client :as client])
  (:import [javafx.scene.media Media MediaPlayer MediaView]))

(def sample-vid
  (when-not (.exists (clojure.java.io/file "sample.mp4"))
    (let [yatter (-> "yoru no yatter man"
                     api/search-page
                     api/parse-list-page
                     first)
          yatter-stream (client/get (-> yatter :video-url :sd) {:as :stream})
          buffer-size (* 1024 10)]
      (with-open [input (:body yatter-stream)
                  output (clojure.java.io/output-stream "sample.mp4")]
        (let [buffer (make-array Byte/TYPE buffer-size)]
          (loop []
            (let [size (.read input buffer)]
              (when (pos? size)
                (.write output buffer 0 size)
                (recur))))))))
    {:video-url
     {:sd
      (str (.toURI (clojure.java.io/file "sample.mp4")))}})


(defn play-vid []
  (let [media  (-> sample-vid :video-url :sd Media.)
        player (MediaPlayer. media)]
    (.play player)
    (doto (fx/h-box (MediaView. player))
      ;    ;    (.setPreserveRatio view true)
      ;    ;    (.setSmooth view true)
      ;    box))
      (.setMinHeight (.. player getMedia getHeight))
      (.setMinWidth  (.. player getMedia getWidth))
      (.setMaxHeight (.. player getMedia getHeight))
      (.setMinWidth  (.. player getMedia getHeight)))))

(defn -main [& args]
  (fx/sandbox #'play-vid))
