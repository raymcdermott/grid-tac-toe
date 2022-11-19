(ns cljs-ui
  (:require
    [reagent.core :as r]
    [reagent.dom :as rdom]))

(def dimension (r/atom 3))
(def backing-grid (r/atom (make-array int @dimension @dimension)))
(def update-count (r/atom 0))

(defn update-grid
  [row col val]
  (let [g @backing-grid]
    (aset g row col val)
    (reset! backing-grid g)
    (swap! update-count inc)))

(defn xo
  [row col val]
  [:div.d-grid.gap-2
   (if val
     [:h3 (if (= val :X)
            [:i.fa-solid.fa-xmark]
            [:i.fa-regular.fa-circle])]
     [:button.btn.btn-outline-light
      {:type     "button"
       :on-click (fn []
                   (println :click :row row :col col :val val)
                   (update-grid row col :X))}
      [:h3 ""]])])

(defn grid []
  [:div
   [:p (str "Turns taken " @update-count)]
   [:table.table.table-bordered
    [:tbody (for [row (range @dimension)]
              ^{:key (str "tr-" row)}
              [:tr (for [col (range @dimension)]
                     (let [val (aget @backing-grid row col)]
                       ^{:key (str "td-" col)}
                       [:td.text-center
                        [xo row col val]]))])]]])

(defn home-page
  []
  [:div.container-fluid [grid]])


(rdom/render [home-page] (js/document.getElementById "app"))
