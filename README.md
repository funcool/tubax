cljs-xml
========

Currently there is no good way to parse XML and other markup languages with Clojurescript. There are no Clojurescript-based libraries and most of the Javascript ones require access to the DOM.

This last point is critical because HTML5 Web Workers don't have access to these APIs so an alternative is necessary.

*cljs-xl* uses behind the scenes [sax-js](https://github.com/isaacs/sax-js) a very lightweight library for for XML parsing based on SAX (simple api for xml).

Over this interface we provide a core/async interface that is used in a similar way to this:

```clojure
(def xml "<rss version=\"2.0\">
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
         </rss>")

(go
  (let [parsed-xml (<! (core/xml->clj xml))]
     (print (str parsed-xml)))

;; [:rss {:version "2.0"}
;;   [[:channel {}
;;     [[:title {} ["RSS Title"]]
;;      [:description {} ["This is an example of an RSS feed"]]
;;      [:link {} ["http://www.example.com/main.html"]]
;;      [:lastBuildDate {} ["Mon, 06 Sep 2010 00:01:00 +0000"]]
;;      [:pubDate {} ["Sun, 06 Sep 2009 16:20:00 +0000"]]
;;      [:ttl {} ["1800"]]
;;      [:item {}
;;       [[:title {} ["Example entry"]]
;;        [:description {} ["Here is some text containing an interesting description."]]
;;        [:link {} ["http://www.example.com/blog/post/1"]]
;;        [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aeee-53f933c5395f"]]
;;        [:pubDate {} ["Sun, 06 Sep 2009 16:20:00 +0000"]]]]
;;      [:item {}
;;       [[:title {} ["Example entry2"]]
;;        [:description {} ["Here is some text containing an interesting description."]]
;;        [:link {} ["http://www.example.com/blog/post/1"]]
;;        [:guid {:isPermaLink "false"} ["7bd204c6-1655-4c27-aeee-53f933c5395f"]]
;;        [:pubDate {} ["Sun, 06 Sep 2009 16:20:00 +0000"]]]]]]]]
```