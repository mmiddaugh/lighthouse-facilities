package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.CLIENT_KEY_DEFAULT;
import static java.util.stream.Collectors.toList;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.pssg.BandUpdateResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PssgRequestBot {
  private static final String BASE_URI =
      Environment.get() == Environment.LOCAL
          ? "http://localhost:8666/"
          : "https://vaww.pssg.med.va.gov/";

  private static final int RECORD_COUNT = 30;

  public static void main(String[] args) {
    final SystemDefinitions.Service svc = SystemDefinitions.systemDefinition().facilitiesInternal();
    var bandIds =
        ExpectedResponse.of(
                RestAssured.given()
                    .baseUri(svc.url())
                    .port(svc.port())
                    .relaxedHTTPSValidation()
                    .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT))
                    .request(Method.GET, svc.urlWithApiPath() + "internal/management/bands"))
            .expect(200)
            .expectListOf(String.class);
    log.info("{} has {} bands", Environment.get(), bandIds.size());

    int initialBatchCount = bandIds.size() / RECORD_COUNT;
    List<BandUpdateResponse> responses =
        IntStream.rangeClosed(0, initialBatchCount)
            .map(i -> i * RECORD_COUNT)
            .parallel()
            .mapToObj(o -> updateBands(o))
            .collect(toList());

    BandUpdateResponse totalResponse =
        BandUpdateResponse.builder()
            .bandsCreated(new ArrayList<>())
            .bandsUpdated(new ArrayList<>())
            .build();
    for (BandUpdateResponse r : responses) {
      totalResponse.bandsCreated().addAll(r.bandsCreated());
      totalResponse.bandsUpdated().addAll(r.bandsUpdated());
    }

    int extraOffset = (initialBatchCount + 1) * RECORD_COUNT;
    log.info("Checking for extra bands starting at offset {}", extraOffset);
    while (true) {
      BandUpdateResponse r = updateBands(extraOffset);
      if (r.bandsCreated().isEmpty() && r.bandsUpdated().isEmpty()) {
        break;
      }
      totalResponse.bandsCreated().addAll(r.bandsCreated());
      totalResponse.bandsUpdated().addAll(r.bandsUpdated());
      extraOffset += RECORD_COUNT;
    }

    log.info(
        "Completed PSSG Update! {} PSSG bands in {}",
        totalResponse.bandsCreated().size() + totalResponse.bandsUpdated().size(),
        Environment.get());
    log.info(
        "{} bands created: {}", totalResponse.bandsCreated().size(), totalResponse.bandsCreated());
    log.info(
        "{} bands updated: {}", totalResponse.bandsUpdated().size(), totalResponse.bandsUpdated());
  }

  @SneakyThrows
  static BandUpdateResponse updateBands(int offset) {
    String requestString =
        "arcgis2/rest/services/Portal/MonthlyVAST_TTB/FeatureServer/0/"
            + "query?f=json&inSR=4326&outSR=4326&orderByFields=Name&outFields=*&resultOffset="
            + offset
            + "&returnCountOnly=false&returnDistinctValues=false&"
            + "returnGeometry=true&where=1=1&resultRecordCount="
            + RECORD_COUNT;
    log.info(String.format("Updating for offset %05d", offset));

    String pssgResponse =
        ExpectedResponse.of(
                RestAssured.given()
                    .baseUri(BASE_URI)
                    .relaxedHTTPSValidation()
                    .request(Method.GET, requestString))
            .expect(200)
            .response()
            .asString();

    final SystemDefinitions.Service svc = SystemDefinitions.systemDefinition().facilitiesInternal();
    BandUpdateResponse updateResponse =
        ExpectedResponse.of(
                RestAssured.given()
                    .baseUri(svc.url())
                    .port(svc.port())
                    .relaxedHTTPSValidation()
                    .header("client-key", System.getProperty("client-key", CLIENT_KEY_DEFAULT))
                    .contentType("application/json")
                    .body(pssgResponse)
                    .request(Method.POST, svc.urlWithApiPath() + "internal/management/bands"))
            .expect(200)
            .expectValid(BandUpdateResponse.class);
    log.info(
        String.format(
            "Finished offset %05d with %s bands",
            offset, updateResponse.bandsCreated().size() + updateResponse.bandsUpdated().size()));
    return updateResponse;
  }
}
