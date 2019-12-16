(ns nnichols.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [nnichols.palette-test]
            [nnichols.parse-test]
            [nnichols.predicate-test]
            [nnichols.util-test]))

(doo-tests 'nnichols.palette-test
           'nnichols.parse-test
           'nnichols.predicate-test
           'nnichols.util-test)
