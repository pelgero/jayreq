package io.badgod.jayreq;

import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public interface JayReq {

    static Response get(String url, Headers... headers) {
        return new Client().get(new Request(url, headers));
    }

    Response get(Request request);

    Response post(Request request);

    Response put(Request request);

    Response delete(Request request);

    Response patch(Request request);

    Response head(Request request);

    Response options(Request request);

    Response trace(Request request);

    /**
     * Implementation
     */
    class Client implements JayReq {

        private final HttpClient httpClient;

        public Client() {
            this(HttpClient.newHttpClient());
        }

        public Client(HttpClient httpClient) {
            this.httpClient = httpClient;
        }

        @Override
        public Response get(Request request) {
            var getRequest = new Request(Method.GET, request.uri(), null, request.headers());
            return this.execute(getRequest);
        }

        @Override
        public Response post(Request request) {
            var postRequest = new Request(Method.POST, request.uri(), request.body(), request.headers());
            return this.execute(postRequest);
        }

        @Override
        public Response put(Request request) {
            var putRequest = new Request(Method.PUT, request.uri(), request.body(), request.headers());
            return this.execute(putRequest);
        }

        @Override
        public Response delete(Request request) {
            var deleteRequest = new Request(Method.DELETE, request.uri(), null, request.headers());
            return this.execute(deleteRequest);
        }

        @Override
        public Response patch(Request request) {
            var patchRequest = new Request(Method.PATCH, request.uri(), request.body(), request.headers());
            return this.execute(patchRequest);
        }

        @Override
        public Response head(Request request) {
            var headRequest = new Request(Method.HEAD, request.uri(), null, request.headers());
            return this.execute(headRequest);
        }

        @Override
        public Response options(Request request) {
            var optionsRequest = new Request(Method.OPTIONS, request.uri(), null, request.headers());
            return this.execute(optionsRequest);
        }

        @Override
        public Response trace(Request request) {
            var traceRequest = new Request(Method.TRACE, request.uri(), null, request.headers());
            return this.execute(traceRequest);
        }

        private Response execute(Request request) {
            try {
                var httpResp = httpClient.send(
                    createRequest(request),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                return new Response(
                    request,
                    httpResp.body(),
                    httpResp.statusCode(),
                    httpResp.headers().map());
            } catch (Exception e) {
                throw new Error(request, e);
            }
        }

        private static HttpRequest createRequest(Request request) {
            var builder = HttpRequest.newBuilder().uri(request.uri());

            builder = switch (request.method()) {
                case Method.GET -> builder.GET();
                case Method.DELETE -> builder.DELETE();
                case Method.HEAD -> builder.method("HEAD", HttpRequest.BodyPublishers.noBody());
                case Method.OPTIONS -> builder.method("OPTIONS", HttpRequest.BodyPublishers.noBody());
                case Method.TRACE -> builder.method("TRACE", HttpRequest.BodyPublishers.noBody());
                case Method.POST, Method.PUT, Method.PATCH -> builder.method(
                    request.method().name(),
                    request.body().value()
                        .map(HttpRequest.BodyPublishers::ofString)
                        .orElse(HttpRequest.BodyPublishers.noBody()));
            };

            if (request.headers().isPresent()) {
                builder = builder.headers(request.headers().toStringArray());
            }

            return builder.build();
        }
    }

    /**
     * Wrap any exception into a JayReq.Error that contains request and response of the invocation
     */
    class Error extends RuntimeException {
        private final Request request;
        private final Response response;

        public Error(Request request, Throwable cause) {
            this(request, null, cause);
        }

        public Error(Request request, Response response, Throwable cause) {
            super(createMessage(request, cause), cause);
            this.request = request;
            this.response = response;
        }

        public Request request() {
            return request;
        }

        public Optional<Response> response() {
            return Optional.ofNullable(response);
        }

        private static String createMessage(Request request, Throwable cause) {
            return String.format(
                "%s on HTTP request '%s %s': %s",
                cause.getClass().getSimpleName(),
                request.method(),
                request.uri(),
                cause.getCause().getClass().getSimpleName()
            );
        }
    }
}
