# An OpenSearch client for Eclipse Vert.x
[![Build Status](https://github.com/reactiverse/opensearch-client/actions/workflows/ci-cd.yml/badge.svg?branch=main)](https://github.com/reactiverse/opensearch-client/actions/workflows/ci-cd.yml)

This client exposes the [OpenSearch Java High Level REST Client](https://opensearch.org/docs/latest/clients/java-rest-high-level/) for [Eclipse Vert.x](https://vertx.io/) applications.

## Overview

This client is based on automatically generated shims using a source-to-source transformation from the client source code.

The generated shims ensure that asynchronous event processing respect the Vert.x threading model.

## Usage

The following modules can be used:

* `opensearch-rest-high-level-client`: a classic Vert.x API based on callbacks and Vert.x 4.1.0
* `opensearch-rest-high-level-client-mutiny`: a [Mutiny](https://smallrye.io/smallrye-mutiny/) API of the client
* `opensearch-rest-high-level-client-rxjava2`: a RxJava 2 API of the client
* `opensearch-rest-high-level-client-rxjava3`: a RxJava 3 API of the client

The Maven `groupId` is `io.reactiverse`.

## Sample usage

Here is a sample usage of the RxJava 2 API where an index request is followed by a get request:

```java
  String yo = "{\"foo\": \"bar\"}";
  IndexRequest req = new IndexRequest("posts").id("1").source(yo, XContentType.JSON);
  client
  .rxIndexAsync(req, RequestOptions.DEFAULT)
  .flatMap(resp -> client.rxGetAsync(new GetRequest("posts").id("1"), RequestOptions.DEFAULT))
  .subscribe(resp -> {
  // Handle the response here
  });
```

## Legal

Originally developped by [Julien Ponge](https://julien.ponge.org/).

    Copyright 2018 Red Hat, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
