package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import org.junit.jupiter.api.Test;

public class BulkIT {
  private static final String ALL_PATH = "v0/facilities/all";

  @Test
  void all_csv() throws InterruptedException {
    Thread.sleep(100);
    ExpectedResponse response =
    facilitiesRequest("text/csv", ALL_PATH, 200);
  }

  @Test
  void all_geoJson() throws InterruptedException {
    Thread.sleep(100);
    ExpectedResponse response =  facilitiesRequest("application/geo+json", ALL_PATH, 200);
    response.expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_json() throws InterruptedException {
    Thread.sleep(100);
    ExpectedResponse response =
    facilitiesRequest("application/json", ALL_PATH, 200);
            response.expectValid(GeoFacilitiesResponse.class);

  }

  @Test
  void all_noAccept() throws InterruptedException {
    Thread.sleep(100);
    facilitiesRequest(null, ALL_PATH, 200).expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_vndGeoJson() throws InterruptedException {
    Thread.sleep(100);
    ExpectedResponse response =
    facilitiesRequest("application/vnd.geo+json", ALL_PATH, 200);

        response.expectValid(GeoFacilitiesResponse.class);
  }
}
