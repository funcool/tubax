(ns cljs-xml.core-test
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [cemerick.cljs.test :refer (is deftest done testing)])
  (:require [cljs.core.async :as async :refer [<!]]
            [cljs-xml.core :as core]
            [cemerick.cljs.test :as t]))

(def example-xml-1 "
<?xml version=\"1.0\" encoding=\"UTF-8\" ?>
<rss version=\"2.0\">
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
</rss>
")

(deftest ^:async parser-case1
  (testing "Case 1 - Empty element"
    (print "== case 1")
    (go
      (let [xml "<element/>"
            result (-> xml core/xml->clj <!)]
        (is (contains? result :element))
        (is (= nil (-> result :element )))
        (done)))))

(deftest ^:async parser-case2
  (testing "Case 2 - Empty element with attributes"
    (print "== case 2")
    (go
      (let [xml "<element att1='a' att2='b'/>"
            result (-> xml core/xml->clj <!)]
        (is (contains? result :element))
        (is (contains? (:element result) :att1))
        (is (contains? (:element result) :att2))
        (is (= "a" (-> result :element :att1)))
        (is (= "b" (-> result :element :att2)))
        (done)))))

(deftest ^:async parser-case3
  (testing "Case 3 - Text element"
    (print "== case 3")
    (go
      (let [xml "<element>value</element>"
            result (-> xml core/xml->clj <!)]
        (is (contains? result :element))
        (is (= "value" (-> result :element)))
        (done)))))

(deftest ^:async parser-case4
  (testing "Case 4 - Text + attributes element"
    (print "== case 4")
    (go
      (let [xml "<element att1='a' att2='b'>value</element>"
            result (-> xml core/xml->clj <!)]
        (is (contains? result :element))
        (is (contains? (-> result :element first) :att1))
        (is (contains? (-> result :element first) :att2))
        (is (= "a" (-> result :element first :att1)))
        (is (= "b" (-> result :element first :att2)))
        (is (= "value" (-> result :element second)))
        (done)))))
