(ns tubax.core-test
  (:require [tubax.core :as core]
            [cljs-testrunners.node :as node]
            [cljs.test :as test :refer-macros [deftest is testing]]

            ;; Additional test classes
            [tubax.helpers-test]))

;;; TEST SUCCESS
(deftest parser-case1
  (testing "Case 1 - Empty element"
    (let [xml "<element/>"
          result (-> xml core/xml->clj)]
      (is (= result {:tag :element :attributes {} :content []})))))

(deftest parser-case2
  (testing "Case 2 - Empty element with attributes"
    (let [xml "<element att1='a' att2='b'/>"
          result (-> xml core/xml->clj)]
      (is (= result {:tag :element :attributes {:att1 "a" :att2 "b"} :content []})))))

(deftest parser-case3
  (testing "Case 3 - Text element"
    (let [xml "<element>value</element>"
          result (-> xml core/xml->clj)]
      (is (= result {:tag :element :attributes {} :content ["value"]})))))

(deftest parser-case4
  (testing "Case 4 - Text + attributes element"
    (let [xml "<element att1='a' att2='b'>value</element>"
          result (-> xml core/xml->clj)]
      (is (= result {:tag :element :attributes {:att1 "a" :att2 "b"} :content ["value"]})))))

(deftest parser-case5
  (testing "Case 5 - Subtree elements (no repetition)"
    (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-b>2</elem-b>
                   <elem-c>3</elem-c>
                 </element>"
          result (-> xml core/xml->clj)]
      (is (= result {:tag :element
                     :attributes {}
                     :content
                     [{:tag :elem-a :attributes {} :content ["1"]}
                      {:tag :elem-b :attributes {} :content ["2"]}
                      {:tag :elem-c :attributes {} :content ["3"]}]})))))

(deftest parser-case6
  (testing "Case 6 - Subtree elements (repetition)"
    (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-a>2</elem-a>
                   <elem-b>3</elem-b>
                 </element>"
          result (-> xml core/xml->clj)]
      (is (= result {:tag :element
                     :attributes {}
                     :content
                     [{:tag :elem-a :attributes {} :content ["1"]}
                      {:tag :elem-a :attributes {} :content ["2"]}
                      {:tag :elem-b :attributes {} :content ["3"]}]})))))

(deftest parser-case7
  (testing "Case 7 - Nested tree"
    (let [xml "<element>
                   <elem-a>
                     <elem-b>
                       <elem-c>test</elem-c>
                     </elem-b>
                   </elem-a>
                 </element>"
          result (-> xml core/xml->clj)]
      (is (= result {:tag :element
                     :attributes {}
                     :content
                     [{:tag :elem-a
                       :attributes {}
                       :content
                       [{:tag :elem-b
                         :attributes {}
                         :content
                         [{:tag :elem-c
                           :attributes {}
                           :content ["test"]}]}]}]})))))

(deftest parser-case8
  (testing "Case 8 - Nested tree with children"
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
          result (-> xml core/xml->clj)]
      (is (= result
             {:tag :element
              :attributes {}
              :content
              [{:tag :elem-a
                :attributes {}
                :content
                [{:tag :elem-b
                  :attributes {}
                  :content
                  [{:tag :elem-c :attributes {} :content ["test1"]}
                   {:tag :elem-c :attributes {} :content ["test2"]}
                   {:tag :elem-c :attributes {} :content ["test3"]}]}]}
               {:tag :elem-a
                :attributes {}
                :content ["other"]}]})))))


(deftest parser-full
  (testing "Full parser"
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
          result (-> xml core/xml->clj)]
      (is (= result
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
                   {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aeee-53f933c5395f"]}
                   {:tag :pubDate :attributes {} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}]}
                 {:tag :item :attributes {}
                  :content
                  [{:tag :title :attributes {} :content ["Example entry2"]}
                   {:tag :description :attributes {} :content ["Here is some text containing an interesting description."]}
                   {:tag :link :attributes {} :content ["http://www.example.com/blog/post/1"]}
                   {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aeee-53f933c5395f"]}
                   {:tag :pubDate :attributes {} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}]}]}]})))))


;;; TEST ERRORS
(deftest parser-error1
  (testing "Error 1 - XML Syntax error"
    (let [xml "<element><a></element>"]
      (is (thrown? js/Error (-> xml core/xml->clj))))))

(deftest parser-error2
  (testing "Error 2 - Unfinished XML"
    (let [xml "<element><a><b></b></a>"]
      (is (thrown? js/Error (-> xml core/xml->clj))))))

;;; TEST OPTIONS
(deftest parser-options-strict
  (testing "Option 1 - Strict mode"
    (let [xml "<element><a><b></b></a>"]
      (is (thrown? js/Error (= (core/xml->clj xml :strict true))))
      (is (= (core/xml->clj xml :strict false) {:tag :element
                                                :attributes {}
                                                :content
                                                [{:tag :a
                                                  :attributes {}
                                                  :content
                                                  [{:tag :b :attributes {} :content []}]}]})))))

(deftest parser-options-trim
  (testing "Option 2 - Trim"
    (let [xml "<element>  test  </element>"]
      (is (= (core/xml->clj xml :trim false) {:tag :element :attributes {} :content ["  test  "]}))
      (is (= (core/xml->clj xml :trim true) {:tag :element :attributes {} :content ["test"]}))
      (is (= (core/xml->clj xml) {:tag :element :attributes {} :content ["test"]})))))

(deftest parser-options-normalize
  (testing "Option 3 - Normalize"
    (let [xml "<element>testing\nnormalize</element>"]
      (is (= (core/xml->clj xml :normalize false) {:tag :element :attributes {} :content ["testing\nnormalize"]}))
      (is (= (core/xml->clj xml :normalize true) {:tag :element :attributes {} :content ["testing normalize"]}))
      (is (= (core/xml->clj xml) {:tag :element :attributes {} :content ["testing\nnormalize"]})))))

(deftest parser-options-lowercase
  (testing "Option 4 - Lowercase"
    (let [xml "<element att1='att'>test</element>"]
      (is (= (core/xml->clj xml :strict false :lowercase false) {:tag :ELEMENT :attributes {:ATT1 "att"} :content ["test"]}))
      (is (= (core/xml->clj xml :strict false :lowercase true) {:tag :element :attributes {:att1 "att"} :content ["test"]}))
      (is (= (core/xml->clj xml :strict false) {:tag :element :attributes {:att1 "att"} :content ["test"]})))))

(deftest parser-options-xmlns
  (testing "Option 5 - XMLNS"
    (let [xml "<element xmlns='http://foo' xmlns:t='http://t' t:att1='att'>
                  <t:test>a</t:test>
                  <test>b</test>
               </element>"]
      (is (= (core/xml->clj xml :xmlns false)
             {:tag :element :attributes {:xmlns "http://foo", :xmlns:t "http://t", :t:att1 "att"}
              :content
              [{:tag :t:test :attributes {} :content ["a"]}
               {:tag :test :attributes {} :content ["b"]}]}))

      (is (= (core/xml->clj xml :xmlns true)
             {:tag :element
              :attributes
              {:xmlns   {:name "xmlns"
                         :value "http://foo"
                         :prefix "xmlns"
                         :local ""
                         :uri "http://www.w3.org/2000/xmlns/"}
               :xmlns:t {:name "xmlns:t"
                         :value "http://t"
                         :prefix "xmlns"
                         :local "t"
                         :uri "http://www.w3.org/2000/xmlns/"}
               :t:att1 {:name "t:att1"
                        :value "att"
                        :prefix "t"
                        :local "att1"
                        :uri "http://t"}}
              :content
              [{:tag :t:test :attributes {} :content ["a"]}
               {:tag :test :attributes {} :content ["b"]}]}))
      (is (= (core/xml->clj xml)
             {:tag :element
              :attributes {:xmlns "http://foo", :xmlns:t "http://t", :t:att1 "att"}
              :content
              [{:tag :t:test :attributes {} :content ["a"]}
               {:tag :test :attributes {} :content ["b"]}]})))))


(deftest parser-options-strict-entities
  (testing "Option 6 - Strict Entities"
    (let [xml "<element att1='&amp;&lt;&aacute;'>&amp;&lt;&aacute;</element>"]
      (is (= (core/xml->clj xml :strict-entities false) {:tag :element :attributes {:att1 "&<á"} :content ["&<á"]}))
      (is (thrown? js/Error (= (core/xml->clj xml :strict-entities true))))
      (is (= (core/xml->clj xml) {:tag :element :attributes {:att1 "&<á"} :content ["&<á"]})))))
