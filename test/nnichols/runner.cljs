(ns nnichols.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [nnichols.http-test]
            [nnichols.palette-test]
            [nnichols.parse-test]
            [nnichols.predicate-test]
            [nnichols.spec-test]
            [nnichols.util-test]))

(doo-tests 'nnichols.http-test
           'nnichols.palette-test
           'nnichols.parse-test
           'nnichols.predicate-test
           'nnichols.spec-test
           'nnichols.util-test)
