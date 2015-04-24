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
      (is (= result [:element {} []])))))

(deftest parser-case2
  (testing "Case 2 - Empty element with attributes"
    (let [xml "<element att1='a' att2='b'/>"
          result (-> xml core/xml->clj)]
      (is (= result [:element {:att1 "a" :att2 "b"} []])))))

(deftest parser-case3
  (testing "Case 3 - Text element"
    (let [xml "<element>value</element>"
          result (-> xml core/xml->clj)]
      (is (= result [:element {} ["value"]])))))

(deftest parser-case4
  (testing "Case 4 - Text + attributes element"
    (let [xml "<element att1='a' att2='b'>value</element>"
          result (-> xml core/xml->clj)]
      (is (= result [:element {:att1 "a" :att2 "b"} ["value"]])))))

(deftest parser-case5
  (testing "Case 5 - Subtree elements (no repetition)"
    (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-b>2</elem-b>
                   <elem-c>3</elem-c>
                 </element>"
          result (-> xml core/xml->clj)]
      (is (= result [:element {} [[:elem-a {} ["1"]] [:elem-b {} ["2"]] [:elem-c {} ["3"]]]])))))

(deftest parser-case6
  (testing "Case 6 - Subtree elements (repetition)"
    (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-a>2</elem-a>
                   <elem-b>3</elem-b>
                 </element>"
          result (-> xml core/xml->clj)]
      (is (= result [:element {} [[:elem-a {} ["1"]] [:elem-a {} ["2"]] [:elem-b {} ["3"]]]])))))

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
      (is (= result [:element {} [[:elem-a {} [[:elem-b {} [[:elem-c {} ["test"]]]]]]  ]])))))

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
             [:element {}
              [[:elem-a {}
                [[:elem-b {}
                  [[:elem-c {} ["test1"]]
                   [:elem-c {} ["test2"]]
                   [:elem-c {} ["test3"]]]]]]
               [:elem-a {} ["other"]]]])))))


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
                   [:pubDate {} ["Sun, 06 Sep 2009 16:20:00 +0000"]]]]]]]])))))


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
      (is (= (core/xml->clj xml :strict false) [:element {} [[:a {} [[:b {} []]]]]])))))

(deftest parser-options-trim
  (testing "Option 2 - Trim"
    (let [xml "<element>  test  </element>"]
      (is (= (core/xml->clj xml :trim false) [:element {} ["  test  "]]))
      (is (= (core/xml->clj xml :trim true) [:element {} ["test"]]))
      (is (= (core/xml->clj xml) [:element {} ["test"]])))))

(deftest parser-options-normalize
  (testing "Option 3 - Normalize"
    (let [xml "<element>testing\nnormalize</element>"]
      (is (= (core/xml->clj xml :normalize false) [:element {} ["testing\nnormalize"]]))
      (is (= (core/xml->clj xml :normalize true) [:element {} ["testing normalize"]]))
      (is (= (core/xml->clj xml) [:element {} ["testing\nnormalize"]])))))

(deftest parser-options-lowercase
  (testing "Option 4 - Lowercase"
    (let [xml "<element att1='att'>test</element>"]
      (is (= (core/xml->clj xml :strict false :lowercase false) [:ELEMENT {:ATT1 "att"} ["test"]]))
      (is (= (core/xml->clj xml :strict false :lowercase true) [:element {:att1 "att"} ["test"]]))
      (is (= (core/xml->clj xml :strict false) [:element {:att1 "att"} ["test"]])))))

(deftest parser-options-xmlns
  (testing "Option 5 - XMLNS"
    (let [xml "<element xmlns='http://foo' xmlns:t='http://t' t:att1='att'>
                  <t:test>a</t:test>
                  <test>b</test>
               </element>"]
      (is (= (core/xml->clj xml :xmlns false)
             [:element {:xmlns "http://foo", :xmlns:t "http://t", :t:att1 "att"}
              [[:t:test {} ["a"]]
               [:test {} ["b"]]]]))

      (is (= (core/xml->clj xml :xmlns true)
             [:element {:xmlns   {:name "xmlns"
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
              [[:t:test {} ["a"]]
               [:test {} ["b"]]]]))
      (is (= (core/xml->clj xml)
             [:element {:xmlns "http://foo", :xmlns:t "http://t", :t:att1 "att"}
              [[:t:test {} ["a"]]
               [:test {} ["b"]]]])))))


(deftest parser-options-strict-entities
  (testing "Option 6 - Strict Entities"
    (let [xml "<element att1='&amp;&lt;&aacute;'>&amp;&lt;&aacute;</element>"]
      (is (= (core/xml->clj xml :strict-entities false) [:element {:att1 "&<á"} ["&<á"]]))
      (is (thrown? js/Error (= (core/xml->clj xml :strict-entities true))))
      (is (= (core/xml->clj xml) [:element {:att1 "&<á"} ["&<á"]])))))
