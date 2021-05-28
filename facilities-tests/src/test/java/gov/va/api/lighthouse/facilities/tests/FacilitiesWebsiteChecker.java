package gov.va.api.lighthouse.facilities.tests;

import static org.apache.commons.lang3.StringUtils.isBlank;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FacilitiesWebsiteChecker {

  public static void main(String[] args) {
    log.info("Beginning website validation!");
    GeoFacilitiesResponse allFacilitiesResponse =
        ExpectedResponse.of(
                RestAssured.given()
                    .baseUri("https://api.va.gov")
                    .relaxedHTTPSValidation()
                    .header("apikey", System.getProperty("apikey", "unset"))
                    .request(
                        Method.GET, "https://api.va.gov/services/va_facilities/v0/facilities/all"))
            .expect(200)
            .expectValid(GeoFacilitiesResponse.class);

    int invalidWebsiteCount = 0;
    if (allFacilitiesResponse.features() != null) {
      for (GeoFacility facility : allFacilitiesResponse.features()) {
        if (!isBlank(facility.properties().website())) {
          try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request =
                HttpRequest.newBuilder().uri(URI.create(facility.properties().website())).build();

            HttpResponse<String> responseCode =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            if (responseCode.statusCode() != 200) {
              log.info(
                  "Invalid website: {} for facility {} status code: {}",
                  facility.properties().website(),
                  facility.properties().id(),
                  responseCode.statusCode());
              invalidWebsiteCount++;
            }
          } catch (Throwable e) {
            log.info("Invalid website for facility {}", facility.properties().id());
            invalidWebsiteCount++;
          }
        }
      }
    }
    log.info("Completed website validation! Invalid Website Count: {}", invalidWebsiteCount);
  }
}
