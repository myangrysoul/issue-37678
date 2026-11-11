package repro;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_XML;

import java.io.IOException;
import java.time.Duration;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okio.ByteString;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.dataformat.xml.XmlMapper;

class JacksonXmlEncoderReproducerTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final Order ORDER = new Order("123");

    private final XmlMapper xmlMapper = new XmlMapper();

    @Test
    void When_SendDtoAsXml_Expect_XmlRequestBody() throws IOException, InterruptedException {
        assertXmlRequest(ORDER);
    }

    @Test
    void When_SendSerializedXml_Expect_XmlRequestBody() throws IOException, InterruptedException {
        assertXmlRequest(xmlMapper.writeValueAsString(ORDER));
    }

    private void assertXmlRequest(Object body) throws IOException, InterruptedException {
        try (MockWebServer server = new MockWebServer()) {
            server.start();
            server.enqueue(new MockResponse.Builder().code(NO_CONTENT.value()).build());

            ResponseEntity<Void> response = WebClient.create()
                    .post()
                    .uri(server.url("/orders").uri())
                    .contentType(APPLICATION_XML)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block(TIMEOUT);

            assertNotNull(response);
            assertEquals(NO_CONTENT, response.getStatusCode());

            RecordedRequest request = server.takeRequest(TIMEOUT.toSeconds(), SECONDS);
            assertNotNull(request);
            ByteString requestBody = request.getBody();
            assertNotNull(requestBody);
            assertEquals(ORDER, xmlMapper.readValue(requestBody.toByteArray(), Order.class));
        }
    }

    public record Order(String id) {
    }
}
