(ns ants-clojure.core
  (:require [clojure.java.io :refer [resource]])
  (:import [javafx.application Application]
           [javafx.fxml FXMLLoader]
           [javafx.stage Stage]
           [javafx.scene Scene]
           [javafx.animation AnimationTimer]
           (javafx.scene.paint Color))
  (:gen-class :extends javafx.application.Application))

(def width 800)
(def height 600)
(def ant-count 100)
(def ants (atom nil))
(def last-timestamp (atom 0))

(defn create-ants []
  (for [i (range 0 ant-count)]
    {:x     (rand-int width)
     :y     (rand-int height)
     :color Color/BLACK
    :size 10}))

(defn random-step []
  (- (* 2 (rand)) 1))


(defn move-ant [ant]
  #_(Thread/sleep 1)
  (assoc ant :x (+ (random-step) (:x ant))
             :y (+ (random-step) (:y ant))))

(defn draw-ants [context]
  (.clearRect context 0 0 width height)
  (doseq [ant (deref ants)]
    (.setFill context (:color ant))
    (.fillOval context (:x ant) (:y ant) (:size ant) (:size ant))))


(defn fps [now]
  (let [diff (- now (deref last-timestamp))
        diff-seconds (/ diff 1000000000)]
    (int (/ 1 diff-seconds))))

(defn aggravate-ant [ant]
  (let [aggro-ants (filter (fn [anthony]
                              (and (<= (Math/abs (- (:x ant) (:x anthony))) 10)
                                   (<= (Math/abs (- (:y ant) (:y anthony))) 10)))
                            (deref ants))]
    (if (= 1 (count aggro-ants))
      (assoc ant :color Color/BLACK)
      (assoc ant :color Color/GREEN)
      )))
(defn hulk-ant [ant]
  (let [hulk-ants (filter (fn [anthony]
                            (and (<= (Math/abs (- (:x ant) (:x anthony))) 10)
                                 (<= (Math/abs (- (:y ant) (:y anthony))) 10)))
                          (deref ants))]
    (if (= 1 (count hulk-ants))
     (assoc ant :size 10)
     (assoc ant :size 20))))

(defn -start [app ^Stage stage]
  (let [root (FXMLLoader/load (resource "main.fxml"))
        scene (Scene. root width height)
        canvas (.lookup scene "#canvas")
        fps-label (.lookup scene "#fps")
        context (.getGraphicsContext2D canvas)
        timer (proxy [AnimationTimer] []
                (handle [now]
                  (.setText fps-label (str (fps now)))
                  (reset! last-timestamp now)
                  (reset! ants (doall (pmap hulk-ant (pmap aggravate-ant (pmap move-ant (deref ants)))))) ;doall makes the lazy sequence work
                  (draw-ants context)))]
    (reset! ants (create-ants))
    (doto stage
      (.setTitle "Ants")
      (.setScene scene)
      (.show))
    (.start timer)))

(defn -main [& args]
  (Application/launch ants_clojure.core (into-array String args)))
