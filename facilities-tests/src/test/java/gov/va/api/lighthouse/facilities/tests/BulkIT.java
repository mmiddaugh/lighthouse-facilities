package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import org.junit.jupiter.api.Test;

public class BulkIT {
  private static final String ALL_PATH = "v0/facilities/all";

  @Test
  void all_csv() {
    ExpectedResponse response =
    facilitiesRequest("text/csv", ALL_PATH, 200);

    System.out.println("ALL CSV: \n" + response.response().asString());
  }

  @Test
  void all_geoJson() {
    ExpectedResponse response =  facilitiesRequest("application/geo+json", ALL_PATH, 200);

    System.out.println("ALL GEOJSON: \n" + response.response().asString());

    response.expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_json() {

    ExpectedResponse response =
    facilitiesRequest("application/json", ALL_PATH, 200);

    System.out.println("ALL JSON: \n" + response.response().asString());

            response.expectValid(GeoFacilitiesResponse.class);

  }

  @Test
  void all_noAccept() {
    ExpectedResponse response =
    facilitiesRequest(null, ALL_PATH, 200);

    System.out.println("ALL NOACCEPT: \n" + response.response().asString());

            response.expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_vndGeoJson() {
    ExpectedResponse response =
    facilitiesRequest("application/vnd.geo+json", ALL_PATH, 200);

    System.out.println("ALL VNDGEOJSON: \n" + response.response().asString());

        response.expectValid(GeoFacilitiesResponse.class);
  }
}
