tubax
========
http://en.wikipedia.org/wiki/Tubax

<blockquote>
While the timbre of the E♭ tubax is more focused and compact than that of the full-sized contrabass saxophone.
</blockquote>

## Rationale
Currently there is no good way to parse XML and other markup languages with Clojurescript. There are no Clojurescript-based libraries and most of the Javascript ones require access to the DOM.

This last point is critical because HTML5 Web Workers don't have access to these APIs so an alternative is necessary.

Also we're aiming to be fully compatible with the clojure.xml format so we can use helping functions inside the Clojure library.

## Usage

[![Clojars Project](http://clojars.org/funcool/tubax/latest-version.svg)](http://clojars.org/funcool/tubax)

*Tubax* uses behind the scenes [sax-js](https://github.com/isaacs/sax-js) a very lightweight library for for XML parsing based on SAX (simple api for xml).

You can check full documentation here: http://funcool.github.io/tubax

A basic usage example:

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

(core/xml->clj xml)

;; => {:tag :rss :attributes {:version "2.0"}
;;       :content
;;       [{:tag :channel :attributes {}
;;         :content
;;         [{:tag :title :attributes {} :content ["RSS Title"]}
;;          {:tag :description :attributes {} :content ["This is an example of an RSS feed"]}
;;          {:tag :link :attributes {} :content ["http://www.example.com/main.html"]}
;;          {:tag :lastBuildDate :attributes {} :content ["Mon, 06 Sep 2010 00:01:00 +0000"]}
;;          {:tag :pubDate :attributes {} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}
;;          {:tag :ttl :attributes {} :content ["1800"]}
;;          {:tag :item :attributes {}
;;           :content
;;           [{:tag :title :attributes {} :content ["Example entry"]}
;;            {:tag :description :attributes {} :content ["Here is some text containing an interesting description."]}
;;            {:tag :link :attributes {} :content ["http://www.example.com/blog/post/1"]}
;;            {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aaaa-111111111111"]}
;;            {:tag :pubDate :attributes {} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}]}
;;          {:tag :item :attributes {}
;;           :content
;;           [{:tag :title :attributes {} :content ["Example entry2"]}
;;            {:tag :description :attributes {} :content ["Here is some text containing an interesting description."]}
;;            {:tag :link :attributes {} :content ["http://www.example.com/blog/post/2"]}
;;            {:tag :guid :attributes {:isPermaLink "false"} :content ["7bd204c6-1655-4c27-aeee-53f933c5395f"]}
;;            {:tag :pubDate :attributes {} :content ["Sun, 06 Sep 2009 16:20:00 +0000"]}]}]}]}
```

This data structure is fully compatible with the XML zipper inside `clojure.zip`

```
(require '[clojure.zip :as z])

(-> xml core/xml->clj
    z/xml-zip
    z/down
    z/down
    z/rightmost
    z/node
    :content
    first)

;; => "Example entry2"
```

## License

Licensed under [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)
