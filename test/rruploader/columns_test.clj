(ns rruploader.columns-test
  (:require [rruploader.columns :as sut]
            [rruploader.parsers :as par]
            [clojure.test :refer [deftest is testing]]))

(deftest column-tests
 (testing "Column ranges"
   (is (= {:A :colA :B :colB :C :colC} (sut/ranges->colmap "A-C")) "Simple range")
   (is (= {:A :colA :B :colB :C :colC} (sut/ranges->colmap "a-C")) "Simple range, case insensitive")
   (is (= {:D :colD :E :colE :F :colF} (sut/ranges->colmap "D-F")) "Simple range not starting at A")
   (is (= {:F :colF} (sut/ranges->colmap "f")) "Single column")
   (is (= {:L :colL} (sut/ranges->colmap "L-L")) "Single column repeated"))

 (testing "Column lists"
   (is (= {:A :colA :B :colB :X :colX :Y :colY :Z :colZ} (sut/ranges->colmap "a-b,x-z"))))

 (testing "Error cases"
   (is (thrown? Exception (sut/ranges->colmap "b-a")) "Reverse range not allowed")))

(deftest keyword-list-tests
  (let [sut-keyword-list #'rruploader.columns/keyword-list]
    (is (= '(:A :B :C) (sut-keyword-list 65 67)))
    (is (thrown? Exception (sut-keyword-list 66 65)))
    (is (thrown? Exception (sut-keyword-list 70 68)))))

(deftest parsers->parsermap-tests
  (let [colmap (sut/ranges->colmap "A-C,E,G-I")]
    (is (= {:colA par/hhmm-parser
            :colB par/dmy-parser
            :colC par/none-parser
            :colE par/none-parser
            :colG par/none-parser
            :colH par/description-parser
            :colI par/none-parser}
           (sut/parsers->parsermap "A:HHMM,B:DMY,H:DESC" colmap)))))
