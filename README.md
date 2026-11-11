# WebClient XML encoding reproducer

Spring Framework 7.0.10, Jackson XML 3.1.8 and Java 21. The tests use
`WebClient.create()` with its default codecs and a local MockWebServer.

Open this directory as a Gradle project in IntelliJ IDEA and select JDK 21
as the Gradle JVM. Run `JacksonXmlEncoderReproducerTest`, or run from the
project directory on Windows:

```powershell
.\gradlew.bat test
```

On macOS or Linux, run `sh ./gradlew test`.

Both tests expect the server to receive XML that can be read back as the
original DTO. With Spring Framework 7.0.10, the expected results are:

| Test | Expected result on 7.0.10 |
| --- | --- |
| `When_SendDtoAsXml_Expect_XmlRequestBody` | Fails with `UnsupportedOperationException: Stream encoding is currently not supported` |
| `When_SendSerializedXml_Expect_XmlRequestBody` | Passes with an XML string generated from the same DTO |

The first test passes a DTO directly to `bodyValue`. Spring wraps that DTO
in a `Mono` and calls the encoder through `EncoderHttpMessageWriter`.
The test checks the expected HTTP behavior and is intended to turn green
once Spring can encode the DTO on this path.
