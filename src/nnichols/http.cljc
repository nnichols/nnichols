(ns nnichols.http
  "A bunch of utility functions for http requests")

(defn bodiless-json-response
  "Creates a boddiless ring response with status `code`"
  [code]
  {:status code
   :headers {"Content-Type" "application/json; charset=UTF-8"}})
