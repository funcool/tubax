(ns tubax.tests.helpers-test
  (:require
   [tubax.helpers :as helpers]
   [cljs.test :as t]))

(def testing-data
  {:tag :rss :attrs {:version "2.0"}
   :content
   [{:tag :channel :attrs {}
     :content
     [{:tag :title :attrs {} :content ["RSS Title"]}
      {:tag :description :attrs {} :content ["This is an example of an RSS feed"]}
      {:tag :link :attrs {} :content ["http://www.example.com/main.html"]}
      {:tag :lastBuildDate :attrs {} :content ["Mon, 06 Sep 2010 00:01:00 +0000"]}
      {:tag :pubDate :attrs {} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}
      {:tag :ttl :attrs {} :content ["1800"]}
      {:tag :item :attrs {}
       :content
       [{:tag :title :attrs {} :content ["Example entry"]}
        {:tag :description :attrs {} :content ["Here is some text containing an interesting description."]}
        {:tag :link :attrs {} :content ["http://www.example.com/blog/post/1"]}
        {:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}
        {:tag :pubDate :attrs {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}]}
      {:tag :item :attrs {}
       :content
       [{:tag :title :attrs {} :content ["Example entry2"]}
        {:tag :description :attrs {} :content ["Here is some text containing an interesting description."]}
        {:tag :link :attrs {} :content ["http://www.example.com/blog/post/2"]}
        {:tag :guid :attrs {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]}
        {:tag :pubDate :attrs {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}
        {:tag :author :attrs {} :content ["John McCarthy"]}]}]}]})

(t/deftest helpers-access
  (t/testing "Helpers access"
    (let [node {:tag :item :attrs {:att1 "att"} :content ["value"]}]
      (t/is (= (helpers/tag node) :item))
      (t/is (= (helpers/attributes node) {:att1 "att"}))
      (t/is (= (helpers/children node) ["value"]))
      (t/is (= (helpers/text node) "value"))))

  (t/testing "Unexpected values"
    (t/is (= (helpers/text {:tag :item :attrs {} :content [{:tag :itemb :attrs {} :content ["value"]}]}) nil)))

  (t/testing "Check if node"
    (t/is (= (helpers/is-node {:tag :item :attrs {} :content []}) true))
    (t/is (= (helpers/is-node "test") false))
    (t/is (= (helpers/is-node {:tag :item :content []}) false))
    (t/is (= (helpers/is-node [:tag "test" :attrs {} :content []]) false))))

(t/deftest find-first
  (t/testing "Find first tag"
    (t/is (= (helpers/find-first testing-data {:tag :link})
           {:tag :link :attrs {} :content ["http://www.example.com/main.html"]}))
    (t/is (= (helpers/find-first testing-data {:tag :guid})
           {:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}))
    (t/is (= (helpers/find-first testing-data {:tag :author})
           {:tag :author :attrs {} :content ["John McCarthy"]}))
    (t/is (= (helpers/find-first testing-data {:tag :no-tag})
           nil)))
  (t/testing "Find first path"
    (t/is (= (helpers/find-first testing-data {:path [:rss :channel :ttl]})
           {:tag :ttl :attrs {} :content ["1800"]}))
    (t/is (= (helpers/find-first testing-data {:path [:rss :channel :item :link]})
           {:tag :link :attrs {} :content ["http://www.example.com/blog/post/1"]}))
    (t/is (= (helpers/find-first testing-data {:path [:rss :channel :item :notexists]})
           nil))
    (t/is (= (helpers/find-first testing-data {:path nil})
           nil))
    (t/is (= (helpers/find-first testing-data {:path []})
           nil))
    (t/is (= (helpers/find-first testing-data {:path [:badroot]})
           nil)))
  (t/testing "Find first keyword"
    (t/is (= (helpers/find-first testing-data {:attribute :isPermaLink})
           {:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}))
    (t/is (= (helpers/find-first testing-data {:attribute :year})
           {:tag :pubDate :attrs {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}))
    (t/is (= (helpers/find-first testing-data {:attribute :not-existing})
           nil)))
  (t/testing "Find first keyword equality"
    (t/is (= (helpers/find-first testing-data {:attribute [:isPermaLink "false"]})
           {:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}))
    (t/is (= (helpers/find-first testing-data {:attribute [:isPermaLink "true"]})
           {:tag :guid :attrs {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]}))
    (t/is (= (helpers/find-first testing-data {:attribute [:year "2013"]})
           {:tag :pubDate :attrs {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}))
    (t/is (= (helpers/find-first testing-data {:attribute [:year "2009"]})
           {:tag :pubDate :attrs {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}))
    (t/is (= (helpers/find-first testing-data {:attribute [:year "2010"]})
           nil))
    (t/is (= (helpers/find-first testing-data {:attribute [:not-existing true]})
           nil))
    (t/is (= (helpers/find-first testing-data {:attribute [:shouldfail]})
           nil))))

(t/deftest find-all
  (t/testing "Find all by tag"
    (t/is (= (helpers/find-all testing-data {:tag :link})
           '({:tag :link :attrs {} :content ["http://www.example.com/main.html"]}
             {:tag :link :attrs {} :content ["http://www.example.com/blog/post/1"]}
             {:tag :link :attrs {} :content ["http://www.example.com/blog/post/2"]})))
    (t/is (= (helpers/find-all testing-data {:tag :guid})
           '({:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}
             {:tag :guid :attrs {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]})))
    (t/is (= (helpers/find-all testing-data {:tag :author})
           '({:tag :author :attrs {} :content ["John McCarthy"]})))
    (t/is (= (helpers/find-all testing-data {:tag :no-tag})
           '())))
  (t/testing "Find first path"
    (t/is (= (helpers/find-all testing-data {:path [:rss :channel :ttl]})
           '({:tag :ttl :attrs {} :content ["1800"]})))
    (t/is (= (helpers/find-all testing-data {:path [:rss :channel :item :link]})
           '({:tag :link :attrs {} :content ["http://www.example.com/blog/post/1"]}
             {:tag :link :attrs {} :content ["http://www.example.com/blog/post/2"]})))
    (t/is (= (helpers/find-all testing-data {:path [:rss :channel :item :notexists]})
           '()))
    (t/is (= (helpers/find-all testing-data {:path nil})
           '()))
    (t/is (= (helpers/find-all testing-data {:path []})
           '()))
    (t/is (= (helpers/find-all testing-data {:path [:badroot]})
           '()))
    )
  (t/testing "Find all keyword"
    (t/is (= (helpers/find-all testing-data {:attribute :isPermaLink})
           '({:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}
             {:tag :guid :attrs {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]})))
    (t/is (= (helpers/find-all testing-data {:attribute :year})
           '({:tag :pubDate :attrs {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]}
             {:tag :pubDate :attrs {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]})))
    (t/is (= (helpers/find-all testing-data {:attribute :not-existing})
           '())))
  (t/testing "Find all keyword equality"
    (t/is (= (helpers/find-all testing-data {:attribute [:isPermaLink "false"]})
           '({:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]})))
    (t/is (= (helpers/find-all testing-data {:attribute [:isPermaLink "true"]})
           '({:tag :guid :attrs {:isPermaLink "true"} :content ["7bd204c6-1655-4c27-bbbb-222222222222"]})))
    (t/is (= (helpers/find-all testing-data {:attribute [:year "2013"]})
           '({:tag :pubDate :attrs {:year "2013"} :content ["Sun, 06 Sep 2013 16:20:00 +0000"]})))
    (t/is (= (helpers/find-all testing-data {:attribute [:year "2009"]})
           '({:tag :pubDate :attrs {:year "2009"} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]})))
    (t/is (= (helpers/find-all testing-data {:attribute [:year "2010"]})
           '()))
    (t/is (= (helpers/find-all testing-data {:attribute [:not-existing true]})
           '()))
    (t/is (= (helpers/find-all testing-data {:attribute [:shouldfail]})
           '()))))
