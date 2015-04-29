(ns tubax.helpers-test
  (:require [tubax.helpers :as helpers]
            [cljs.test :as test :refer-macros [deftest is testing]]))

(def testing-data
  {:tag :rss :attributes {:version "2.0"}
   :content
   [{:tag :channel :attributes {}
     :content
     [{:tag :title :attributes {} :content ["RSS Title"]}
      {:tag :description :attributes {} :content ["This is an example of an RSS feed"]}
      {:tag :link :attributes {} :content ["http://www.example.com/main.html"]}
      {:tag :lastBuildDate :attributes {} :content ["Mon, 06 Sep 2010 00:01:00 +0000"]}
      {:tag :pubDate :attributes {} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}
      {:tag :ttl :attributes {} :content ["1800"]}
      {:tag :item :attributes {}
       :content
       [{:tag :title :attributes {} :content ["Example entry"]}
        {:tag :description :attributes {} :content ["Here is some text containing an interesting description."]}
        {:tag :link :attributes {} :content ["http://www.example.com/blog/post/1"]}
        {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}
        {:tag :pubDate :attributes {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}]}
      {:tag :item :attributes {}
       :content
       [{:tag :title :attributes {} :content ["Example entry2"]}
        {:tag :description :attributes {} :content ["Here is some text containing an interesting description."]}
        {:tag :link :attributes {} :content ["http://www.example.com/blog/post/2"]}
        {:tag :guid :attributes {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]}
        {:tag :pubDate :attributes {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}
        {:tag :author :attributes {} :content ["John McCarthy"]}]}]}]})

(deftest helpers-access
  (testing "Helpers access"
    (let [node {:tag :item :attributes {:att1 "att"} :content ["value"]}]
      (is (= (helpers/get-tag node) :item))
      (is (= (helpers/get-attributes node) {:att1 "att"}))
      (is (= (helpers/get-children node) ["value"]))
      (is (= (helpers/get-text node) "value"))))
  (testing "Unexpected values"
    (is (= (helpers/get-text {:tag :item :attributes {} :content [{:tag :itemb :attributes {} :content ["value"]}]}) nil)))
  (testing "Check if node"
    (is (= (helpers/is-node {:tag :item :attributes {} :content []}) true))
    (is (= (helpers/is-node "test") false))
    (is (= (helpers/is-node {:tag :item :content []}) false))
    (is (= (helpers/is-node [:tag "test" :attributes {} :content []]) false))))

(deftest find-first
  (testing "Find first tag"
    (is (= (helpers/find-first testing-data {:tag :link})
           {:tag :link :attributes {} :content ["http://www.example.com/main.html"]}))
    (is (= (helpers/find-first testing-data {:tag :guid})
           {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}))
    (is (= (helpers/find-first testing-data {:tag :author})
           {:tag :author :attributes {} :content ["John McCarthy"]}))
    (is (= (helpers/find-first testing-data {:tag :no-tag})
           nil)))
  (testing "Find first path"
    (is (= (helpers/find-first testing-data {:path [:rss :channel :ttl]})
           {:tag :ttl :attributes {} :content ["1800"]}))
    (is (= (helpers/find-first testing-data {:path [:rss :channel :item :link]})
           {:tag :link :attributes {} :content ["http://www.example.com/blog/post/1"]}))
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
           {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}))
    (is (= (helpers/find-first testing-data {:attribute :year})
           {:tag :pubDate :attributes {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}))
    (is (= (helpers/find-first testing-data {:attribute :not-existing})
           nil)))
  (testing "Find first keyword equality"
    (is (= (helpers/find-first testing-data {:attribute [:isPermaLink "false"]})
           {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}))
    (is (= (helpers/find-first testing-data {:attribute [:isPermaLink "true"]})
           {:tag :guid :attributes {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]}))
    (is (= (helpers/find-first testing-data {:attribute [:year "2013"]})
           {:tag :pubDate :attributes {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}))
    (is (= (helpers/find-first testing-data {:attribute [:year "2009"]})
           {:tag :pubDate :attributes {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}))
    (is (= (helpers/find-first testing-data {:attribute [:year "2010"]})
           nil))
    (is (= (helpers/find-first testing-data {:attribute [:not-existing true]})
           nil))
    (is (= (helpers/find-first testing-data {:attribute [:shouldfail]})
           nil))))

(deftest find-all
  (testing "Find all by tag"
    (is (= (helpers/find-all testing-data {:tag :link})
           '({:tag :link :attributes {} :content ["http://www.example.com/main.html"]}
             {:tag :link :attributes {} :content ["http://www.example.com/blog/post/1"]}
             {:tag :link :attributes {} :content ["http://www.example.com/blog/post/2"]})))
    (is (= (helpers/find-all testing-data {:tag :guid})
           '({:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}
             {:tag :guid :attributes {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]})))
    (is (= (helpers/find-all testing-data {:tag :author})
           '({:tag :author :attributes {} :content ["John McCarthy"]})))
    (is (= (helpers/find-all testing-data {:tag :no-tag})
           '())))
  (testing "Find first path"
    (is (= (helpers/find-all testing-data {:path [:rss :channel :ttl]})
           '({:tag :ttl :attributes {} :content ["1800"]})))
    (is (= (helpers/find-all testing-data {:path [:rss :channel :item :link]})
           '({:tag :link :attributes {} :content ["http://www.example.com/blog/post/1"]}
             {:tag :link :attributes {} :content ["http://www.example.com/blog/post/2"]})))
    (is (= (helpers/find-all testing-data {:path [:rss :channel :item :notexists]})
           '()))
    (is (= (helpers/find-all testing-data {:path nil})
           '()))
    (is (= (helpers/find-all testing-data {:path []})
           '()))
    (is (= (helpers/find-all testing-data {:path [:badroot]})
           '()))
    )
  (testing "Find all keyword"
    (is (= (helpers/find-all testing-data {:attribute :isPermaLink})
           '({:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}
             {:tag :guid :attributes {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]})))
    (is (= (helpers/find-all testing-data {:attribute :year})
           '({:tag :pubDate :attributes {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}
             {:tag :pubDate :attributes {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]})))
    (is (= (helpers/find-all testing-data {:attribute :not-existing})
           '())))
  (testing "Find all keyword equality"
    (is (= (helpers/find-all testing-data {:attribute [:isPermaLink "false"]})
           '({:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]})))
    (is (= (helpers/find-all testing-data {:attribute [:isPermaLink "true"]})
           '({:tag :guid :attributes {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]})))
    (is (= (helpers/find-all testing-data {:attribute [:year "2013"]})
           '({:tag :pubDate :attributes {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]})))
    (is (= (helpers/find-all testing-data {:attribute [:year "2009"]})
           '({:tag :pubDate :attributes {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]})))
    (is (= (helpers/find-all testing-data {:attribute [:year "2010"]})
           '()))
    (is (= (helpers/find-all testing-data {:attribute [:not-existing true]})
           '()))
    (is (= (helpers/find-all testing-data {:attribute [:shouldfail]})
           '()))))
