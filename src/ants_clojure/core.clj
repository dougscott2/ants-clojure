(ns ants-clojure.core
  (:require [clojure.java.io :refer [resource]])
  (:import [javafx.application Application]
           [javafx.fxml FXMLLoader]
           [javafx.stage Stage]
           [javafx.scene Scene]
           [javafx.animation AnimationTimer])
  (:gen-class :extends javafx.application.Application))

(def width 800)
(def height 600)

(defn -start [app ^Stage stage]
  (let [root (FXMLLoader/load (resource "main.fxml"))
        scene (Scene. root width height)
        canvas (.lookup scene "#canvas")
        fps-label (.lookup scene "#fps")
        context (.getGraphicsContext2D canvas)
        timer (proxy [AnimationTimer] []
                (handle [now]
                  ))]
    (doto stage
      (.setTitle "Ants")
      (.setScene scene)
      (.show))
    (.start timer)))

(defn -main [& args]
  (Application/launch ants_clojure.core (into-array String args)))
;commit