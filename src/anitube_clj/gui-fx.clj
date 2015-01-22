(ns anitube-clj.gui-fake
  (:import [javafx.scene SceneBuilder]
           [javafx.scene.control ButtonBuilder]
           [javafx.scene.layout VBoxBuilder]
           [javafx.scene.web WebView]
           [javafx.scene.media Media MediaPlayer MediaView]
           [javafx.stage StageBuilder])
  (:require [anitube-clj.api :as api]))




;; https://coderwall.com/p/4yjy1a/getting-started-with-javafx-in-clojure
(defonce force-toolkit-init (javafx.embed.swing.JFXPanel.))

(defn run-later*
  [f]
  (javafx.application.Platform/runLater f))

(defmacro run-later
  [& body]
  `(run-later* (fn [] ~@body)))

(defn run-now*
  [f]
  (let [result (promise)]
    (run-later
      (deliver result (try (f) (catch Throwable e e))))
    @result))

(defmacro run-now
  [& body]
  `(run-now* (fn [] ~@body)))

(defn event-handler*
  [f]
  (reify javafx.event.EventHandler
    (handle [this e] (f e))))

(defmacro event-handler [arg & body]
  `(event-handler* (fn ~arg ~@body)))

(def stage (atom nil))

(defn play-video
  [video]
  (let [webview (WebView.)]
    (.. webview getEngine
        (load "http://lax06.vid.anitu.be/bAkdutTee2jBxayiFWfDeg/1421430146/79492_hd.mp4"))
    webview))

(run-now (reset! stage
                 (.. StageBuilder create
                     (title "hi!")
                     (scene (.. SceneBuilder create
                                (height 480) (width 640)
                                (root (play-video nil)) ;(second (api/parse-list-page (api/top-page))))])
                                build))
                     build)))

(run-now (.show @stage))
