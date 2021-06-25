package nextstep.subway;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.CacheControl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;


import java.util.concurrent.TimeUnit;

import static nextstep.subway.WebMvcConfig.PREFIX_STATIC_RESOURCES;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StaticResourcesTest {
    private static final Logger logger = LoggerFactory.getLogger(StaticResourcesTest.class);

    @Autowired
    private WebTestClient client;


    @Test
    void get_static_resources() {
        String uri = PREFIX_STATIC_RESOURCES +  "/js/main.js";
        EntityExchangeResult<String> response = client
                    .get()
                    .uri(uri)
                .exchange()
                    .expectStatus()
                        .isOk()
                    .expectHeader()
                        .cacheControl(CacheControl.noCache().cachePrivate())
                    .expectBody(String.class)
                        .returnResult();

        logger.debug("body : {}", response.getResponseBody());

        String etag = response.getResponseHeaders()
                .getETag();

        client
            .get()
                .uri(uri)
                .header("If-None-Match", etag)
            .exchange()
                .expectStatus()
                    .isNotModified();
    }
}