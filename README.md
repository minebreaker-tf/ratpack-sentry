Using Sentry in Ratpack application

[![CircleCI](https://circleci.com/gh/minebreaker-tf/ratpack-sentry.svg?style=svg)](https://circleci.com/gh/minebreaker-tf/ratpack-sentry)
[![codecov](https://codecov.io/gh/minebreaker-tf/ratpack-sentry/branch/master/graph/badge.svg)](https://codecov.io/gh/minebreaker-tf/ratpack-sentry)
![](https://img.shields.io/badge/maturity-development-green.svg)
![](https://img.shields.io/badge/license-MIT-green.svg)


* [Ratpack](https://ratpack.io)
* [Sentry](https://sentry.io)

Sentry by default uses `ThreadLocalContextManager`,
which means it can't handle contexts in Ratpack very well.
`RatpackSentryContextManager` provides `Context`s per
Ratpack Execution.
If not in Execution, returns singleton `Context`.


## Set up

### Gradle

```groovy:build.gradle
repositories {
    maven {
        url = "https://dl.bintray.com/minebreaker/test"
    }
}
dependencies {
    compile 'io.sentry:sentry-logback:1.7.4'
}
```

### Java code

Specify `SentryClientFactory` for `RatpackSentryClientFactory`.

Manually call `Sentry.init()`

```java
public class Main {
    // your application entry point
    public static void main(String[] args){
        Sentry.init(new RatpackSentryClientFactory());

        RatpackServer.start( /* ... */);
    }
}
```

or specify `factory` parameter in `sentry.properties`

[Docs](https://docs.sentry.io/clients/java/config/#custom-functionality)

```properties:sentry.properties
factory=rip.deadcode.ratpack.sentry.RatpackSentryClientFactory
```
