(ns tubax.tests.core-test
  (:require
   [tubax.core :as core]
   [cljs.test :as t]))

;; TEST SUCCESS
(t/deftest parser-case1
  (t/testing "Case 1 - Empty element"
    (let [xml "<element/>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element :attrs nil :content nil})))))

(t/deftest parser-case2
  (t/testing "Case 2 - Empty element with attributes"
    (let [xml "<element att1='a' att2='b'/>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element :attrs {:att1 "a" :att2 "b"} :content nil})))))

(t/deftest parser-case3
  (t/testing "Case 3 - Text element"
    (let [xml "<element>value</element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element :attrs nil :content ["value"]})))))

(t/deftest parser-case4
  (t/testing "Case 4 - Text + attributes element"
    (let [xml "<element att1='a' att2='b'>value</element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element :attrs {:att1 "a" :att2 "b"} :content ["value"]})))))

(t/deftest parser-case5
  (t/testing "Case 5 - Subtree elements (no repetition)"
    (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-b>2</elem-b>
                   <elem-c>3</elem-c>
                 </element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element
                     :attrs nil
                     :content
                     [{:tag :elem-a :attrs nil :content ["1"]}
                      {:tag :elem-b :attrs nil :content ["2"]}
                      {:tag :elem-c :attrs nil :content ["3"]}]})))))

(t/deftest parser-case6
  (t/testing "Case 6 - Subtree elements (repetition)"
    (let [xml "<element>
                   <elem-a>1</elem-a>
                   <elem-a>2</elem-a>
                   <elem-b>3</elem-b>
                 </element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element
                     :attrs nil
                     :content
                     [{:tag :elem-a :attrs nil :content ["1"]}
                      {:tag :elem-a :attrs nil :content ["2"]}
                      {:tag :elem-b :attrs nil :content ["3"]}]})))))

(t/deftest parser-case7
  (t/testing "Case 7 - Nested tree"
    (let [xml "<element>
                   <elem-a>
                     <elem-b>
                       <elem-c>test</elem-c>
                     </elem-b>
                   </elem-a>
                 </element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element
                     :attrs nil
                     :content
                     [{:tag :elem-a
                       :attrs nil
                       :content
                       [{:tag :elem-b
                         :attrs nil
                         :content
                         [{:tag :elem-c
                           :attrs nil
                           :content ["test"]}]}]}]})))))

(t/deftest parser-case8
  (t/testing "Case 8 - Nested tree with children"
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
      (t/is (= result
             {:tag :element
              :attrs nil
              :content
              [{:tag :elem-a
                :attrs nil
                :content
                [{:tag :elem-b
                  :attrs nil
                  :content
                  [{:tag :elem-c :attrs nil :content ["test1"]}
                   {:tag :elem-c :attrs nil :content ["test2"]}
                   {:tag :elem-c :attrs nil :content ["test3"]}]}]}
               {:tag :elem-a
                :attrs nil
                :content ["other"]}]})))))

(t/deftest parser-full
  (t/testing "Full parser"
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
      (t/is (= result
             {:tag :rss :attrs {:version "2.0"}
              :content
              [{:tag :channel :attrs nil
                :content
                [{:tag :title :attrs nil :content ["RSS Title"]}
                 {:tag :description :attrs nil :content ["This is an example of an RSS feed"]}
                 {:tag :link :attrs nil :content ["http://www.example.com/main.html"]}
                 {:tag :lastBuildDate :attrs nil :content ["Mon, 06 Sep 2010 00:01:00 +0000"]}
                 {:tag :pubDate :attrs nil :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}
                 {:tag :ttl :attrs nil :content ["1800"]}
                 {:tag :item :attrs nil
                  :content
                  [{:tag :title :attrs nil :content ["Example entry"]}
                   {:tag :description :attrs nil :content ["Here is some text containing an interesting description."]}
                   {:tag :link :attrs nil :content ["http://www.example.com/blog/post/1"]}
                   {:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aeee-53f933c5395f"]}
                   {:tag :pubDate :attrs nil :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}]}
                 {:tag :item :attrs nil
                  :content
                  [{:tag :title :attrs nil :content ["Example entry2"]}
                   {:tag :description :attrs nil :content ["Here is some text containing an interesting description."]}
                   {:tag :link :attrs nil :content ["http://www.example.com/blog/post/1"]}
                   {:tag :guid :attrs {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aeee-53f933c5395f"]}
                   {:tag :pubDate :attrs nil :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}]}]}]})))))


;;; TEST ERRORS
(t/deftest parser-error1
  (t/testing "Error 1 - XML Syntax error"
    (let [xml "<element><a></element>"]
      (t/is (thrown? js/Error (-> xml core/xml->clj))))))

(t/deftest parser-error2
  (t/testing "Error 2 - Unfinished XML"
    (let [xml "<element><a><b></b></a>"]
      (t/is (thrown? js/Error (-> xml core/xml->clj))))))

;;; TEST OPTIONS
(t/deftest parser-options-strict
  (t/testing "Option 1 - Strict mode"
    (let [xml "<element><a><b></b></a>"]
      (t/is (thrown? js/Error (= (core/xml->clj xml {:strict true}))))
      (t/is (= (core/xml->clj xml {:strict false})
             {:tag :element
              :attrs nil
              :content
              [{:tag :a
                :attrs nil
                :content
                [{:tag :b :attrs nil :content nil}]}]})))

    (let [xml "<element><a><b><c></element>"]
      (t/is (thrown? js/Error (= (core/xml->clj xml {:strict true}))))
      (t/is (= (core/xml->clj xml {:strict false})
             {:tag :element
              :attrs nil
              :content
              [{:tag :a
                :attrs nil
                :content
                [{:tag :b
                  :attrs nil
                  :content
                  [{:tag :c
                    :attrs nil
                    :content nil}]}]}]})))))

(t/deftest parser-options-trim
  (t/testing "Option 2 - Trim"
    (let [xml "<element>  test  </element>"]
      (t/is (= (core/xml->clj xml {:trim false}) {:tag :element :attrs nil :content ["  test  "]}))
      (t/is (= (core/xml->clj xml {:trim true}) {:tag :element :attrs nil :content ["test"]}))
      (t/is (= (core/xml->clj xml) {:tag :element :attrs nil :content ["test"]})))))

(t/deftest parser-options-normalize
  (t/testing "Option 3 - Normalize"
    (let [xml "<element>t/testing\nnormalize</element>"]
      (t/is (= (core/xml->clj xml {:normalize false}) {:tag :element :attrs nil :content ["t/testing\nnormalize"]}))
      (t/is (= (core/xml->clj xml {:normalize true}) {:tag :element :attrs nil :content ["t/testing normalize"]}))
      (t/is (= (core/xml->clj xml) {:tag :element :attrs nil :content ["t/testing\nnormalize"]})))))

(t/deftest parser-options-lowercase
  (t/testing "Option 4 - Lowercase"
    (let [xml "<element att1='att'>test</element>"]
      (t/is (= (core/xml->clj xml {:strict false :lowercase false}) {:tag :ELEMENT :attrs {:ATT1 "att"} :content ["test"]}))
      (t/is (= (core/xml->clj xml {:strict false :lowercase true}) {:tag :element :attrs {:att1 "att"} :content ["test"]}))
      (t/is (= (core/xml->clj xml {:strict false}) {:tag :element :attrs {:att1 "att"} :content ["test"]})))))

(t/deftest parser-options-xmlns
  (t/testing "Option 5 - XMLNS"
    (let [xml "<element xmlns='http://foo' xmlns:t='http://t' t:att1='att'>
                  <t:test>a</t:test>
                  <test>b</test>
               </element>"]
      (t/is (= (core/xml->clj xml {:xmlns false})
             {:tag :element :attrs {:xmlns "http://foo", :xmlns:t "http://t", :t:att1 "att"}
              :content
              [{:tag :t:test :attrs nil :content ["a"]}
               {:tag :test :attrs nil :content ["b"]}]}))

      (t/is (= (core/xml->clj xml {:xmlns true})
             {:tag :element
              :attrs
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
              [{:tag :t:test :attrs nil :content ["a"]}
               {:tag :test :attrs nil :content ["b"]}]}))
      (t/is (= (core/xml->clj xml)
             {:tag :element
              :attrs {:xmlns "http://foo", :xmlns:t "http://t", :t:att1 "att"}
              :content
              [{:tag :t:test :attrs nil :content ["a"]}
               {:tag :test :attrs nil :content ["b"]}]})))))


(t/deftest parser-options-strict-entities
  (t/testing "Option 6 - Strict Entities"
    (let [xml "<element att1='&amp;&lt;&aacute;'>&amp;&lt;&aacute;</element>"]
      (t/is (= (core/xml->clj xml {:strict-entities false}) {:tag :element :attrs {:att1 "&<á"} :content ["&<á"]}))
      (t/is (thrown? js/Error (= (core/xml->clj xml {:strict-entities true}))))
      (t/is (= (core/xml->clj xml) {:tag :element :attrs {:att1 "&<á"} :content ["&<á"]})))))

;; TEST CDATA
(t/deftest parser-cdata
  (t/testing "Cdata value simple value"
    (let [xml "<element><![CDATA[value]]></element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element :attrs nil :content ["value"]}))))
  (t/testing "Cdata value multiple lines"
    (let [xml "<element><![CDATA[value\n\n\nvalue]]></element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element :attrs nil :content ["value\n\n\nvalue"]}))))
  (t/testing "Cdata value with xml inside"
    (let [xml "<element><![CDATA[<test></test>]]></element>"
          result (-> xml core/xml->clj)]
      (t/is (= result {:tag :element :attrs nil :content ["<test></test>"]})))))

