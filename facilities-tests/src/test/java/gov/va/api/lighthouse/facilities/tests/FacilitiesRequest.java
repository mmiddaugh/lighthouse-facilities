package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.health.sentinel.ExpectedResponse.logAllWithTruncatedBody;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
final class FacilitiesRequest {
  static ExpectedResponse facilitiesRequest(
      String acceptHeader, String request, Integer expectedStatus) {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    log.info(
        "Expect {} with accept header ({}) is status code ({})",
        svc.apiPath() + request,
        acceptHeader,
        expectedStatus);
    RequestSpecification spec =
        RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
    if (acceptHeader != null) {
      spec = spec.accept(acceptHeader);
    }
    return ExpectedResponse.of(spec.request(Method.GET, svc.urlWithApiPath() + request))
        .logAction(logAllWithTruncatedBody(2000))
        .expect(expectedStatus);
  }

  static ExpectedResponse facilitiesRequestBulk(
          String acceptHeader, String request, Integer expectedStatus) {
    SystemDefinitions.Service svc = systemDefinition().facilities();
    log.info(
            "Expect {} with accept header ({}) is status code ({})",
            svc.apiPath() + request,
            acceptHeader,
            expectedStatus);
    RequestSpecification spec =
            RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
    if (acceptHeader != null) {
      spec = spec.accept(acceptHeader);
    }

    System.out.println(ExpectedResponse.of(spec.request(Method.GET,
            svc.urlWithApiPath() + request)).response().asString());

    return ExpectedResponse.of(spec.request(Method.GET, svc.urlWithApiPath() + request))
        .logAction(logAllWithTruncatedBody(2000))
            .expect(expectedStatus);
  }
}
