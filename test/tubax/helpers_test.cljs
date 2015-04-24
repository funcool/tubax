(ns tubax.helpers-test
  (:require [tubax.helpers :as helpers]
            [cljs.test :as test :refer-macros [deftest is testing]]))


(deftest helpers-access
  (testing "Helpers access"
    (let [node [:item {:att1 "att"} ["value"]]]
      (is (= (helpers/tag node) :item))
      (is (= (helpers/attributes node) {:att1 "att"}))
      (is (= (helpers/children node) ["value"]))
      (is (= (helpers/text node) "value"))))
  (testing "Unexpected values"
    (is (= (helpers/text [:item {} [[:itemb {} ["value"]]]]) nil))))
