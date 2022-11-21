(ns cljs-ui
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]
    [clojure.set :as set]))

(def dimension (r/atom 3))
(def backing-grid (r/atom nil))
(def winner (r/atom nil))
(def player (r/atom nil))
(def turn-count (r/atom 0))

(defn game-reset
  []
  (reset! backing-grid (make-array int @dimension @dimension))
  (reset! winner nil)
  (reset! player #{:X})
  (reset! turn-count 0))

(def diagonal-coordinates
  [(vec (map #(identity [% %]) (range @dimension)))
   (vec (map #(identity [%1 %2]) (range @dimension) (reverse (range @dimension))))])


(def players #{:X :O})

(defn winner-in-rows? [board player]
  (boolean (some (fn [row]
                   (every? (fn [c] (= c player)) row)) board)))

(defn transposed-board [board]
  (vec (apply map vector board)))

(defn winner-in-cols? [board player]
  (winner-in-rows? (transposed-board board) player))

(defn winner-in-diagonals? [board player]
  (boolean
    (some (fn [coords]
            (every? (fn [[row col]]
                      (= player (aget board row col)))
                    coords))
          diagonal-coordinates)))

(defn check-win
  []
  (let [player-token (first @player)]
    (boolean (or (winner-in-rows? @backing-grid player-token)
                 (winner-in-cols? @backing-grid player-token)
                 (winner-in-diagonals? @backing-grid player-token)))))

(defn game-over
  [player-token]
  (reset! winner player-token))

(defn play-until-win
  [row col]
  (let [g @backing-grid
        player-token (first @player)
        _ (aset g row col player-token)
        _ (reset! backing-grid g)
        _ (swap! turn-count inc)]
    (if (check-win)
      (game-over player-token)
      (swap! player #(set/difference players %)))))

(defn xo
  [row col val]
  (cond
    val (if (= val :X)
          [:i.fa-solid.fa-xmark]
          [:i.fa-regular.fa-circle])
    :else [:div.d-grid
           [:button.btn.btn-outline-light
            {:type     "button"
             :on-click (fn []
                         (play-until-win row col))}
            "-"]]))

(defn game-reset-button
  []
  [:button.btn.btn-success
   {:type     "button"
    :on-click #(game-reset)}
   "Game Reset"])

(defn page []
  [:div
   [:div.row.p-3
    (if @winner
      [:div.row.p-3
       [:div.col [:h2 (str "Winner is " (name @winner))]]
       [:div.col [game-reset-button]]]
      [:div.row.p-3
       [:div.col [:h3 (str "Turn taken " @turn-count)]]])]
   [:div
    [:table.table.table-bordered
     [:tbody (for [row (range @dimension)]
               ^{:key (str "tr-" row)}
               [:tr (for [col (range @dimension)]
                      (let [val (aget @backing-grid row col)]
                        ^{:key (str "td-" col)}
                        [:td.h2.text-center
                         [xo row col val]]))])]]]])

(defn home-page
  []
  (game-reset)
  [:div.container-fluid.px-4
   [page]])


(rdom/render [home-page] (js/document.getElementById "app"))
