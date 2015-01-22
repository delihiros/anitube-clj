(ns anitube-clj.api
  (:require [clj-http.client :as client])
  (:use [net.cgrand.enlive-html])
  (:import [java.net URLEncoder]))

(def anitube "http://www.anitube.se")

(defrecord Video
  [video-thumb video-title video-url category-name category-url])

(defn url->html-resource
  [url]
  (-> url client/get :body
      java.io.StringReader.
      html-resource))

(defn top-page
  []
  (url->html-resource anitube))

(defn search-page
  [text]
  (url->html-resource (str anitube "/search/?search_id=" (URLEncoder/encode text "utf-8"))))

(defn recent-page
  []
  (url->html-resource (str anitube "/videos/basic/mr")))

(defn parse-config
  [config-js-url]
  (let [config-js
        (-> config-js-url client/get :body)]
    {:video-url (->> config-js
                     (re-seq #"\"(.+\.mp4)\"") ;; flvもあるっぽい
                     (map second)
                     (zipmap [:sd :hd]))
     :video-thumb (->> config-js
                       (re-seq #"image: \"(.+)\",")
                       first second)}))

(defn parse-video-page
  "
  (parse-video-page (url->html-resource  \"http://www.anitube.se/video/81259/Saenai-Heroine-no-Sodatekata-01\"))
  "
  [video-resource]
  (merge {:video-title
          (-> video-resource
              (select [:h1.mainBoxHeader]) first :content first)
          :category-name
          (-> video-resource
              (select [:.viewVideoTags :a]) last :content first)
          :category-url
          (-> video-resource
              (select [:.viewVideoTags :a]) last :attrs :href)}
         (-> video-resource
             (select [:li#videoPlayer :script]) last :attrs :src parse-config)))

(defn parse-thumb
  [thumb-resource]
  (-> thumb-resource
      (select [:.videoThumb :a])
      first :attrs :href
      url->html-resource
      parse-video-page))

(defn parse-list-page
  "
  (parse-list-page (recent-page))
  "
  [list-page]
  (lazy-seq (as-> list-page $
              (select $ [:.mainList])
              (map parse-thumb $))))

(comment
  (-> "http://www.anitube.se/search/?search_id=wixoss"
      url->html-resource
      parse-list-page
      first))
