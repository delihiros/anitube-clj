(ns anitube-clj.gui
  (:use [seesaw.core]
        [seesaw.dev])
  (:require [anitube-clj.api :as api])
  (:import [javafx.embed.swing JFXPanel]
           [javafx.scene Group Scene]
           [javafx.scene.media Media MediaPlayer MediaView]))

(native!)

(def sample-vid
  (first (api/parse-list-page (api/recent-page))))

(defn play-video
  [video]
  (let [root (Group.)
        scene (Scene. root)
        player (MediaPlayer. (Media. (-> video :video-url :sd)))]
    (.setAutoPlay player true)
    (.. scene getRoot
        getChildren
        (add (MediaView. player)))
    (.setScene (JFXPanel.) scene)))

(defn video-thumb
  [video]
  (label :text (:video-title video)
         :icon (:video-thumb video)
         :listen [:mouse-clicked (fn [_] (play-video video))]))


(-> (frame :title "anitube-clj"
           :content
           (video-thumb sample-vid))
    pack!
    show!)
