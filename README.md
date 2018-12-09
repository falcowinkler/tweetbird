# tweetbird

[![CircleCI](https://circleci.com/gh/falcowinkler/tweetbird.svg?style=svg)](https://circleci.com/gh/falcowinkler/tweetbird)

A [re-frame](https://github.com/Day8/re-frame) application
designed to publish kafka events to tweetspace.

## What and why

tweetspace is an example big data architecure of a fictional company,
that users can register for to find people with similar interests
based on what they tweet. For the evaluation of this architecture,
some raw data is needed, which is produced by tweetbird.

## Development Mode

### Prerequisites

Set twitter credentials as environment variables

```
TWITTER_APP_KEY
TWITTER_APP_SECRET
TWITTER_USER_TOKEN
TWITTER_USER_TOKEN_SECRET
```

Start schema-registry, kafka and zookeeper locally:

```bash
confluent start schema-registry
```

To work on the frontend you need to compile sass:

```bash
lein sass4clj auto
```

### Run frontend:

```bash
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Run backend:

```bash
lein run
```

## Production Build

To compile clojurescript to javascript:

```bash
lein clean
lein cljsbuild once min
```


