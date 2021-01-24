package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import org.junit.jupiter.api.Test;

public class BulkIT {
  private static final String ALL_PATH = "v0/facilities/all";

  @Test
  void all_csv() throws InterruptedException {
    ExpectedResponse response = facilitiesRequest("text/csv", ALL_PATH, 200);
  }

  @Test
  void all_geoJson() throws InterruptedException {
    //    for(int i = 0; i < 100; i++)
    facilitiesRequest("application/geo+json", ALL_PATH, 200)
        .expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_json() throws InterruptedException {
    //    for(int i = 0; i < 100; i++)
    facilitiesRequest("application/json", ALL_PATH, 200).expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_noAccept() throws InterruptedException {

    //    for(int i = 0; i < 100; i++)
    facilitiesRequest(null, ALL_PATH, 200).expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_vndGeoJson() throws InterruptedException {

    //    for (int i = 0; i < 100; i++) {
    facilitiesRequest("application/vnd.geo+json", ALL_PATH, 200)
        .expectValid(GeoFacilitiesResponse.class);
    //    }
  }
}
