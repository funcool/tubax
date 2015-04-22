(ns tubax.core-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [tubax.core :as core]
            [cljs-testrunners.node :as node]
            [cljs.core.async :as async :refer [<!]]
            [cljs.test :as test :refer-macros [deftest is async testing]]))

(enable-console-print!)

(deftest parser-case1
  (async done
   (testing "Case 1 - Empty element"
     (go
       (let [xml "<element/>"
             result (-> xml core/xml->clj <!)]
         (is (= result [:element {} []]))
         (done))))))

(deftest parser-case2
  (async done
   (testing "Case 2 - Empty element with attributes"
     (go
       (let [xml "<element att1='a' att2='b'/>"
             result (-> xml core/xml->clj <!)]
         (is (= result [:element {:att1 "a" :att2 "b"} []]))
         (done))))))

(deftest parser-case3
  (async done
   (testing "Case 3 - Text element"
     (go
       (let [xml "<element>value</element>"
             result (-> xml core/xml->clj <!)]
         (is (= result [:element {} ["value"]]))
         (done))))))

(deftest parser-case4
  (async done
   (testing "Case 4 - Text + attributes element"
     (go
       (let [xml "<element att1='a' att2='b'>value</element>"
             result (-> xml core/xml->clj <!)]
         (is (= result [:element {:att1 "a" :att2 "b"} ["value"]]))
         (done))))))

(deftest parser-case5
  (async done
   (testing "Case 5 - Subtree elements (no repetition)"
     (go
       (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-b>2</elem-b>
                   <elem-c>3</elem-c>
                 </element>"
             result (-> xml core/xml->clj <!)]
         (is (= result [:element {} [[:elem-a {} ["1"]] [:elem-b {} ["2"]] [:elem-c {} ["3"]]]]))
         (done))))))

(deftest parser-case6
  (async done
   (testing "Case 6 - Subtree elements (repetition)"
     (go
       (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-a>2</elem-a>
                   <elem-b>3</elem-b>
                 </element>"
             result (-> xml core/xml->clj <!)]
         (is (= result [:element {} [[:elem-a {} ["1"]] [:elem-a {} ["2"]] [:elem-b {} ["3"]]]]))
         (done))))))

(deftest parser-case7
  (async done
   (testing "Case 7 - Nested tree"
     (go
       (let [xml "<element>
                   <elem-a>
                     <elem-b>
                       <elem-c>test</elem-c>
                     </elem-b>
                   </elem-a>
                 </element>"
             result (-> xml core/xml->clj <!)]
         (is (= result [:element {} [[:elem-a {} [[:elem-b {} [[:elem-c {} ["test"]]]]]]  ]]))
         (done))))))

(deftest parser-case8
  (async done
   (testing "Case 8 - Nested tree with children"
     (go
       (let [xml "<element>
                   <elem-a>
                     <elem-b>
                       <elem-c>test1</elem-c>
                       <elem-c>test2</elem-c>
                       <elem-c>test3</elem-c>
                     </elem-b>
                   </elem-a>
                   <elem-a>other</elem-a>
                 </element>"
             result (-> xml core/xml->clj <!)]
         (is (= result
                [:element {}
                 [[:elem-a {}
                   [[:elem-b {}
                     [[:elem-c {} ["test1"]]
                      [:elem-c {} ["test2"]]
                      [:elem-c {} ["test3"]]]]]]
                  [:elem-a {} ["other"]]]]))
         (done))))))


(deftest parser-full
  (async done
   (testing "Full parser"
     (go
       (let [xml "<rss version=\"2.0\">
                   <channel>
                     <title>RSS Title</title>
                     <description>This is an example of an RSS feed</description>
                     <link>http://www.example.com/main.html</link>
                     <lastBuildDate>Mon, 06 Sep 2010 00:01:00 +0000 </lastBuildDate>
                     <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
                     <ttl>1800</ttl>
                     <item>
                       <title>Example entry</title>
                       <description>Here is some text containing an interesting description.</description>
                       <link>http://www.example.com/blog/post/1</link>
                       <guid isPermaLink=\"false\">7bd204c6-1655-4c27-aeee-53f933c5395f</guid>
                       <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
                     </item>
                     <item>
                       <title>Example entry2</title>
                       <description>Here is some text containing an interesting description.</description>
                       <link>http://www.example.com/blog/post/1</link>
                       <guid isPermaLink=\"false\">7bd204c6-1655-4c27-aeee-53f933c5395f</guid>
                       <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
                     </item>
                   </channel>
                 </rss>"
             result (-> xml core/xml->clj <!)]
         (is (= result
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
                      [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aeee-53f933c5395f"]]
                      [:pubDate {} ["Sun, 06 Sep 2009 16:20:00 +0000"]]]]
                    [:item {}
                     [[:title {} ["Example entry2"]]
                      [:description {} ["Here is some text containing an interesting description."]]
                      [:link {} ["http://www.example.com/blog/post/1"]]
                      [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aeee-53f933c5395f"]]
                      [:pubDate {} ["Sun, 06 Sep 2009 16:20:00 +0000"]]]]]]]]))
         (done))))))

(defn main [] (node/run-tests))
(set! *main-cli-fn* main)
