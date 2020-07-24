package gov.va.api.lighthouse.facilitiesmockservices;

import static com.google.common.base.Preconditions.checkState;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import com.google.common.io.Resources;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mockserver.client.MockServerClient;
import org.mockserver.mockserver.MockServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Creates a mock server that can support requests needed by facilities collector. This also
 * includes a /help endpoint to allow humans to see what requests are supported.
 */
@Component
@Slf4j
public class MockServices {
  /** All queries added to the mock server are listed here, except for /help. */
  private final List<String> supportedQueries = new ArrayList<>();

  @Autowired MockServicesOptions options;

  /** Configurable health check statuses for the health collector. */
  @Value("${mock.atc-status-code:200}")
  int atcStatusCode;

  @Value("${mock.atp-status-code:200}")
  int atpStatusCode;

  @Value("${mock.arcgis-status-code:200}")
  int arcgisStatusCode;

  @Value("${mock.cems-status-code:200}")
  int cemsStatusCode;

  /** The mock server itself. */
  private MockServer ms;

  private void addAccessToCareHealthCheck(MockServerClient mock) {
    mock.when(addQuery("/Shep/getRawData?location=FL"))
        .respond(
            response()
                .withStatusCode(atpStatusCode)
                .withHeader(contentApplicationJson())
                .withBody(contentOf("/access-to-care-satisfaction.json")));
  }

  private void addAccessToCareSatisfactionScores(MockServerClient mock) {
    mock.when(addQuery("/Shep/getRawData?location=*"))
        .respond(
            response()
                .withStatusCode(atpStatusCode)
                .withHeader(contentApplicationJson())
                .withBody(contentOf("/access-to-care-satisfaction.json")));
  }

  private void addAccessToCareWaitTimes(MockServerClient mock) {
    mock.when(addQuery("/atcapis/v1.1/patientwaittimes"))
        .respond(
            response()
                .withStatusCode(atcStatusCode)
                .withHeader(contentApplicationJson())
                .withBody(contentOf("/access-to-care-wait-times.json")));
  }

  private void addArcGisBenefitsFacilities(MockServerClient mock) {
    mock.when(
            addQuery(
                "/aqgBd3l68G8hEFFE/ArcGIS/rest/services/VBA_Facilities/FeatureServer/0/query"
                    + "?f=json&inSR=4326&outSR=4326&orderByFields=Facility_Number"
                    + "&outFields=*&resultOffset=0&returnCountOnly=false"
                    + "&returnDistinctValues=false&returnGeometry=true&where=1=1"))
        .respond(
            response()
                .withStatusCode(arcgisStatusCode)
                .withHeader(contentTextPlain())
                .withBody(contentOf("/arcgis-benefits-facilities.json")));
  }

  private void addArcGisCemeteries(MockServerClient mock) {
    mock.when(
            addQuery(
                "/aqgBd3l68G8hEFFE/ArcGIS/rest/services/NCA_Facilities/FeatureServer/0/query"
                    + "?f=json&inSR=4326&orderByFields=SITE_ID&outFields=*&outSR=4326"
                    + "&resultOffset=0&returnCountOnly=false&returnDistinctValues=false"
                    + "&returnGeometry=true&where=1=1"))
        .respond(
            response()
                .withStatusCode(arcgisStatusCode)
                .withHeader(contentTextPlain())
                .withBody(contentOf("/arcgis-cemeteries.json")));
  }

  private void addArcGisHealthCheck(MockServerClient mock) {
    mock.when(addQuery("/aqgBd3l68G8hEFFE/ArcGIS/rest/info/healthCheck?f=json"))
        .respond(
            response()
                .withStatusCode(arcgisStatusCode)
                .withHeader(contentTextPlain())
                .withBody(
                    "{\"currentVersion\":10.7,\"fullVersion\":\"10.7\",\"owningSystemUrl\":\"https://www.arcgis.com\",\"owningTenant\":\"aqgBd3l68G8hEFFE\",\"authInfo\":{\"isTokenBasedSecurity\":true,\"tokenServicesUrl\":\"https://www.arcgis.com/sharing/generateToken\"}}"));
  }

  private void addBing(MockServerClient mock) {
    mock.when(addQuery("/REST/v1/Locations"))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentApplicationJson())
                .withBody(contentOf("/bing.json")));
  }

  private void addHelp(MockServerClient mock) {
    mock.when(request().withPath("/help"))
        .respond(
            response()
                .withStatusCode(200)
                .withHeader(contentTextPlain())
                .withBody(supportedQueries.stream().sorted().collect(Collectors.joining("\n"))));
    log.info("List of supported queries available at http://localhost:{}/help", options.getPort());
  }

  private void addPssgDriveTimeBands(MockServerClient mock) {
    /* Add a few pages of results. The last page should be empty []. */
    int pageSize = 30;
    int page = 0;
    for (; page < 3; page++) {
      mock.when(
              addQuery(
                  "/arcgis2/rest/services/Portal/MonthlyVAST_TTB/FeatureServer/0/query"
                      + "?f=json&inSR=4326&outSR=4326&orderByFields=Name&outFields=*"
                      + "&returnCountOnly=false&returnDistinctValues=false"
                      + "&returnGeometry=true&where=1=1"
                      + ("&resultOffset=" + (page * pageSize))
                      + ("&resultRecordCount=" + pageSize)))
          .respond(
              response()
                  .withStatusCode(200)
                  .withHeader(contentApplicationJson())
                  .withBody(contentOf("/pssg-drive-time-bands-" + page + ".json")));
    }
  }

  @SneakyThrows
  private HttpRequest addQuery(String path) {
    log.info("http://localhost:{}{}", options.getPort(), path);
    supportedQueries.add("http://localhost:" + options.getPort() + path);
    URL url = new URL("http://localhost" + path);
    HttpRequest request = request().withPath(url.getPath());
    if (url.getQuery() == null) {
      return request;
    }
    /*
     * Split the query portion of the path and each of the parameters individually. Also note that
     * sometimes the parameter value includes an '=' ... looking at you 'where=1=1'
     */
    Stream.of(url.getQuery().split("&"))
        .forEach(
            q -> {
              var pv = q.split("=", 2);
              request.withQueryStringParameter(
                  pv[0], URLDecoder.decode(pv[1], StandardCharsets.UTF_8));
            });
    return request;
  }

  private void addStateCemeteries(MockServerClient mock) {
    mock.when(addQuery("/cems/cems.xml"))
        .respond(
            response()
                .withStatusCode(cemsStatusCode)
                .withHeader(contentTextXml())
                .withBody(contentOf("/cems.xml")));
  }

  private Header contentApplicationJson() {
    return new Header("Content-Type", "application/json");
  }

  @SneakyThrows
  private String contentOf(String resource) {
    log.info("Loading resource {}", resource);
    // noinspection UnstableApiUsage
    return Resources.toString(getClass().getResource(resource), StandardCharsets.UTF_8);
  }

  private Header contentTextPlain() {
    return new Header("Content-Type", "text/plain");
  }

  private Header contentTextXml() {
    return new Header("Content-Type", "text/xml");
  }

  /** Start the server and configure it to support requests. */
  public void start() {
    checkState(ms == null, "Mock Services have already been started");
    log.info("Starting mock services on port {}", options.getPort());
    ms = new MockServer(options.getPort());
    MockServerClient mock = new MockServerClient("localhost", options.getPort());
    addAccessToCareHealthCheck(mock);
    addAccessToCareWaitTimes(mock);
    addAccessToCareSatisfactionScores(mock);
    addStateCemeteries(mock);
    addArcGisBenefitsFacilities(mock);
    addArcGisCemeteries(mock);
    addArcGisHealthCheck(mock);
    addPssgDriveTimeBands(mock);
    addBing(mock);
    addHelp(mock);
  }
}
