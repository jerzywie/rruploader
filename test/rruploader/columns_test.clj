(ns rruploader.columns-test
  (:require [rruploader.columns :refer :all]
            [clojure.test :refer :all]))

(deftest column-tests
 (testing "Column ranges"
   (is (= {:A :colA :B :colB :C :colC} (ranges->colmap "A-C")) "Simple range")
   (is (= {:A :colA :B :colB :C :colC} (ranges->colmap "a-C")) "Simple range, case insensitive")
   (is (= {:D :colD :E :colE :F :colF} (ranges->colmap "D-F")) "Simple range not starting at A")
   (is (= {:F :colF} (ranges->colmap "f")) "Single column")
   (is (= {:L :colL} (ranges->colmap "L-L")) "Single column repeated"))

 (testing "Column lists"
   (is (= {:A :colA :B :colB :X :colX :Y :colY :Z :colZ} (ranges->colmap "a-b,x-z"))))

 (testing "Error cases"
   (is (= {:A :colA :B :colB} (ranges->colmap "b-a")))))
