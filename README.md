[![Java CI with Gradle](https://github.com/robpelger/jayreq/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/robpelger/jayreq/actions/workflows/gradle.yml)

# JayReq

A dependency-free Java HTTP client.

JayReq is a thin wrapper around `java.net.http.HttpClient` that provides a clean, minimal API for making HTTP requests. Zero runtime dependencies — just Java 21+.

## Installation

**Gradle (Kotlin DSL)**
```kotlin
implementation("io.badgod:jayreq:<version>")
```

**Gradle (Groovy)**
```groovy
implementation 'io.badgod:jayreq:<version>'
```

**Maven**
```xml
<dependency>
    <groupId>io.badgod</groupId>
    <artifactId>jayreq</artifactId>
    <version>${version}</version>
</dependency>
```

## Quick start

```java
var response = JayReq.get("https://httpbin.org/get");
System.out.println(response.status()); // 200
System.out.println(response.body().value()); // Optional[...]
```

## Usage

### GET requests

The simplest way to make a GET request is the static shortcut:

```java
var response = JayReq.get("https://httpbin.org/get");
```

With custom headers:

```java
var response = JayReq.get(
    "https://httpbin.org/get",
    Headers.of("X-Custom", "value"),
    Headers.accept("application/json")
);
```

### POST, PUT, DELETE, PATCH

For other HTTP methods, use `JayReq.Client`:

```java
var client = new JayReq.Client();

// POST with a JSON body
var post = client.post(new Request(
    Method.POST,
    URI.create("https://httpbin.org/post"),
    Body.of("{\"name\": \"jay\"}"),
    Headers.of("Content-Type", "application/json")
));

// PUT
var put = client.put(new Request(
    Method.PUT,
    URI.create("https://httpbin.org/put"),
    Body.of("update-data")
));

// DELETE (no body)
var delete = client.delete(new Request("https://httpbin.org/delete"));

// PATCH
var patch = client.patch(new Request(
    Method.PATCH,
    URI.create("https://httpbin.org/patch"),
    Body.of("patch-data")
));
```

### Headers

Create headers with `Headers.of`:

```java
Headers.of("Content-Type", "application/json")
Headers.of("Accept", "text/html", "application/json") // multiple values
```

Built-in helpers for common headers:

```java
Headers.authBearer("my-token")
Headers.authBasic("username", "password")
Headers.accept("application/json")
```

Merge multiple headers together:

```java
var headers = Headers.mergeAll(
    Headers.of("Content-Type", "application/json"),
    Headers.authBearer("my-token"),
    Headers.of("X-Request-Id", "abc-123")
);
```

### Reading responses

```java
var response = JayReq.get("https://httpbin.org/get");

response.status();              // int — HTTP status code
response.body().value();        // Optional<String> — raw response body
response.headers();             // Headers — response headers
response.headers().get("Content-Type"); // Optional<List<String>>
```

Use a `Body.Converter` to transform the response body:

```java
var parsed = response.body((status, headers, body) -> {
    return new Gson().fromJson(body, MyType.class);
});
// parsed is Optional<MyType>
```

### Error handling

All request failures are wrapped in `JayReq.Error`, an unchecked exception:

```java
try {
    JayReq.get("https://invalid-host.example");
} catch (JayReq.Error e) {
    e.request();    // the original Request
    e.response();   // Optional<Response> (empty if the request never completed)
    e.getCause();   // the underlying exception
}
```

### Custom HttpClient

Pass your own `java.net.http.HttpClient` for full control over timeouts, redirects, etc.:

```java
var httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(5))
    .followRedirects(HttpClient.Redirect.NORMAL)
    .build();

var client = new JayReq.Client(httpClient);
var response = client.get(new Request("https://httpbin.org/get"));
```

## Requirements

- Java 21+

## Limitations

- Synchronous only — no async/reactive support
- String request and response bodies only
- No built-in retry or timeout configuration (use a custom `HttpClient`)
- No cookie management

## License

[Apache License 2.0](LICENSE)
