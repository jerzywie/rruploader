(ns rruploader.parsers-test
  (:require [rruploader.parsers :as sut]
            [clojure.test :refer :all]))

(def sut-ex-betw-delim #'rruploader.parsers/extract-between-delimiters)
(def sut-extract-kandv #'rruploader.parsers/extract-kandv)

(deftest description-parser-tests
  (testing "extract-between-delimiters"
    (are [desc extract] (= extract (sut-ex-betw-delim desc "[" "]"))
      "Avon - Unknown Wall  [we want this bit]"               "we want this bit"
      "before the good bit[we want this bit]but not this bit" "we want this bit"
      "xyzwe want] not this bit"                              "xyzwe want"
      "xyz[we want"                                           "we want")

    (is (= "this bit" (sut-ex-betw-delim "pre+this bit+post" "+" "+")) "Same-delimiter test")

    (is (= "" (sut-ex-betw-delim "pre this bit+post" "+" "+")) "Same-delimiter one-off test"))

  (testing "extract-kandv-tests"
    (are [kv result] (= result (sut-extract-kandv kv))
      " akey (avalue)"
      {"akey" "avalue"}

      " This is a key ( and this is a value) we don't care about this bit"
      {"This is a key" "and this is a value"}))

  (testing "description-parser-itself"
    (are [desc xtra-cols result] (= result (sut/description-parser desc xtra-cols))
      "pre [k(v)]"
      ["a-key"]
      {"a-key" "v"}

      "a longer preamble [multi word key (even more words)]"
      ["the-keyname"]
      {"the-keyname" "even more words"}

      "pre [k1(v1);k2(v2);k3(v3)]"
      ["k1-name" "k2-name" "k3-name"]
      {"k1-name" "v1" "k2-name" "v2" "k3-name" "v3"}

      "pre pre [ first keyword ( and its value ) ; k2 ( v2) ; last one ( 10:22 )]"
      ["first" "second" "third"]
      {"first" "and its value"
       "second" "v2"
       "third" "10:22"})))
