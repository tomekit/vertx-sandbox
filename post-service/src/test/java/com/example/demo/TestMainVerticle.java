package com.example.demo;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {

    @BeforeEach
    @DisplayName("Deploy MainVerticle")
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        vertx.deployVerticle(new MainVerticle(), testContext.succeedingThenComplete());
    }

    // Repeat this test 3 times
    @RepeatedTest(3)
    void http_server_check_response(Vertx vertx, VertxTestContext testContext) {
        HttpClient client = vertx.createHttpClient();
        client.request(HttpMethod.GET, 8888, "localhost", "/hello")
            .compose(req -> req.send().compose(HttpClientResponse::body))
            .onComplete(
                testContext.succeeding(
                    buffer -> testContext.verify(
                        () -> {
                            assertThat(buffer.toString()).isEqualTo("Hello from my route");
                            testContext.completeNow();
                        }
                    )
                )
            );
    }

    @Test
    @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
    @DisplayName("a simple test to verify deploy")
    void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
        testContext.completeNow();
    }

    static Stream<Arguments> testData() {
        return Stream.of(
            Arguments.of("complex object1", 4),
            Arguments.of("complex object2", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    void test2(String obj, int count, Vertx vertx, VertxTestContext testContext) {
        System.out.println(obj);
        System.out.println(count);
        // your test code
        testContext.completeNow();
    }

    @AfterEach
    @DisplayName("Check that the verticle is still there")
    void lastChecks(Vertx vertx) {
        assertThat(vertx.deploymentIDs())
            .isNotEmpty()
            .hasSize(1);
    }
}
