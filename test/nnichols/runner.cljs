(ns nnichols.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [nnichols.http-test]
            [nnichols.parse-test]
            [nnichols.predicate-test]
            [nnichols.spec-test]
            [nnichols.string-test]
            [nnichols.util-test]
            [nnichols.xml-test]))

(doo-tests 'nnichols.http-test
           'nnichols.parse-test
           'nnichols.predicate-test
           'nnichols.spec-test
           'nnichols.string-test
           'nnichols.util-test
           'nnichols.xml-test)
