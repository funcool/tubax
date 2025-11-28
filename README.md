# tubax

http://en.wikipedia.org/wiki/Tubax

<blockquote>
While the timbre of the E♭ tubax is more focused and compact than that of the full-sized contrabass saxophone.
</blockquote>

## Rationale

Currently there is no good way to parse XML and other markup languages
with Clojurescript. There are no Clojurescript-based libraries and
most of the Javascript ones require access to the DOM.

This last point is critical because HTML5 Web Workers don't have
access to these APIs so an alternative is necessary.

Another alternative to XML processing is to go to a
middle-ground. There are some libraries that will parse XML into a
JSON format.

The problem with these is that JSON is not a faithfull representation
of the XML format. There are some XML that couldn't be represented as
JSON.

For example, the following XML will loss information when transformed into JSON.

```xml
<root>
    <field-a>A</field-a>
    <field-b>B</field-b>
    <field-a>A</field-a>
```

Another main objective of *tubax* is to be fully compatible with the
`clojure.xml` format so we can access the functionality currently in
the Clojure API like zippers.


## Getting Started


*Tubax* uses behind the scenes
[sax-js](https://github.com/isaacs/sax-js) a very lightweight library
for for XML parsing based on SAX (simple api for xml).

All examples will use this XML as if it existed in a `(def xml-data "...")` definition.

```xml
<rss version="2.0">
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
      <guid isPermaLink="false">7bd204c6-1655-4c27-aeee-53f933c5395f</guid>
      <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
    </item>
    <item>
      <title>Example entry2</title>
      <description>Here is some text containing an interesting description.</description>
      <link>http://www.example.com/blog/post/1</link>
      <guid isPermaLink="true">7bd204c6-1655-4c27-aeee-53f933c5395f</guid>
      <pubDate>Sun, 06 Sep 2009 16:20:00 +0000</pubDate>
    </item>
  </channel>
</rss>
```


In order to parse a XML file you only have to make a call to the `xml->clj` function

```clojure
(core/xml->clj xml-data)
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

The `xml->clj` function accept the following options:

- `:strict` - Enables strict parsing mode; defaults to `true`
- `:trim` - Enables triming of whitespaces; defaults to `true`
- `:normalize` - Enables whitespace normalization; defaults to `true` (the
  normalization replaces all whitespaces-characters (like tabs, end of lines,
  ...) for whitespaces.
- `:xmlns` - Enables support for xml namespaces; defaults to `false`
- `:strict-entities`: Enables stricter parser for xml entities; defaults to
  `false` (raises an error if non-predefined entity is found.


## License

Licensed under [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)
