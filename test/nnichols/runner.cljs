(ns nnichols.runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [nnichols.util-test]))

(doo-tests 'nnichols.util-test)
