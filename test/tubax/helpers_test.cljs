(ns tubax.helpers-test
  (:require [tubax.helpers :as helpers]
            [cljs.test :as test :refer-macros [deftest is testing]]))


(deftest helpers-access
  (testing "Helpers access"
    (let [node [:item {:att1 "att"} ["value"]]]
      (is (= (helpers/get-tag node) :item))
      (is (= (helpers/get-attributes node) {:att1 "att"}))
      (is (= (helpers/get-children node) ["value"]))
      (is (= (helpers/get-text node) "value"))))
  (testing "Unexpected values"
    (is (= (helpers/get-text [:item {} [[:itemb {} ["value"]]]]) nil))))


(def testing-data
  [:rss {:version "2.0"}
   [[:channel {}
     [[:title {} ["RSS Title"]]
      [:description {} ["This is an example of an RSS feed"]]
      [:link {} ["http://www.example.com/main.html"]]
      [:lastBuildDate {} ["Mon, 06 Sep 2010 00:01:00 +0000"]]
      [:pubDate {} ["Sun, 06 Sep 2009 16:20:00 +0000"]]
      [:ttl {} ["1800"]]
      [:item {}
       [[:title {} ["Example entry"]]
        [:description {} ["Here is some text containing an interesting description."]]
        [:link {} ["http://www.example.com/blog/post/1"]]
        [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aaaa-111111111111"]]
        [:pubDate {:year "2013"} ["Sun, 06 Sep 2013 16:20:00 +0000"]]]]
      [:item {}
       [[:title {} ["Example entry2"]]
        [:description {} ["Here is some text containing an interesting description."]]
        [:link {} ["http://www.example.com/blog/post/2"]]
        [:guid {:isPermaLink "true"} ["7bd204c6-1655-4c27-bbbb-222222222222"]]
        [:pubDate {:year "2009"} ["Sun, 06 Sep 2009 16:20:00 +0000"]]
        [:author {} "John McCarthy"]]]]]]])


(deftest find-first
  (testing "Find first tag"
    (is (= (helpers/find-first testing-data {:tag :link})
           [:link {} ["http://www.example.com/main.html"]]))
    (is (= (helpers/find-first testing-data {:tag :guid})
           [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aaaa-111111111111"]]))
    (is (= (helpers/find-first testing-data {:tag :author})
           [:author {} "John McCarthy"]))
    (is (= (helpers/find-first testing-data {:tag :no-tag})
           nil)))
  (testing "Find first path"
    (is (= (helpers/find-first testing-data {:path [:rss :channel :ttl]})
           [:ttl {} ["1800"]]))
    (is (= (helpers/find-first testing-data {:path [:rss :channel :item :link]})
           [:link {} ["http://www.example.com/blog/post/1"]]))
    (is (= (helpers/find-first testing-data {:path [:rss :channel :item :notexists]})
           nil))
    (is (= (helpers/find-first testing-data {:path nil})
           nil))
    (is (= (helpers/find-first testing-data {:path []})
           nil))
    (is (= (helpers/find-first testing-data {:path [:badroot]})
           nil)))
  (testing "Find first keyword"
    (is (= (helpers/find-first testing-data {:attribute :isPermaLink})
           [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aaaa-111111111111"]]))
    (is (= (helpers/find-first testing-data {:attribute :year})
           [:pubDate {:year "2013"} ["Sun, 06 Sep 2013 16:20:00 +0000"]]))
    (is (= (helpers/find-first testing-data {:attribute :not-existing})
           nil)))
  (testing "Find first keyword equality"
    (is (= (helpers/find-first testing-data {:attribute [:isPermaLink "false"]})
           [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aaaa-111111111111"]]))
    (is (= (helpers/find-first testing-data {:attribute [:isPermaLink "true"]})
           [:guid {:isPermaLink "true"} ["7bd204c6-1655-4c27-bbbb-222222222222"]]))
    (is (= (helpers/find-first testing-data {:attribute [:year "2013"]})
           [:pubDate {:year "2013"} ["Sun, 06 Sep 2013 16:20:00 +0000"]]))
    (is (= (helpers/find-first testing-data {:attribute [:year "2009"]})
           [:pubDate {:year "2009"} ["Sun, 06 Sep 2009 16:20:00 +0000"]]))
    (is (= (helpers/find-first testing-data {:attribute [:year "2010"]})
           nil))
    (is (= (helpers/find-first testing-data {:attribute [:not-existing true]})
           nil))))
