# tweetbird

A [re-frame](https://github.com/Day8/re-frame) application
designed to publish kafka events to tweetspace.

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```


## What and why

tweetspace is an example big data architecure of a fictional company,
that users can register for to find people with similar interests
based on what they tweet. For the evaluation of this architecture,
some raw data is needed, which is produced by tweetbird.

## Config parameters

Tweetbird is configured with edn configurations, they can
however be changed dynamically during runtime. Parameters are
- watches: An array of searchwords that are periodically queried via
twitters [search API](https://developer.twitter.com/en/docs/tweets/search/api-reference/get-search-tweets.html).
- users: An integer number of randomly chosen users that are registered
and tracked.