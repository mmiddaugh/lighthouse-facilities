package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequestBulk;

import gov.va.api.health.sentinel.ExpectedResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import org.junit.jupiter.api.Test;

public class BulkIT {
  private static final String ALL_PATH = "v0/facilities/all";

  @Test
  void all_csv() {
    ExpectedResponse response =
    facilitiesRequestBulk("text/csv", ALL_PATH, 200);
  }

  @Test
  void all_geoJson() {
    ExpectedResponse response =  facilitiesRequestBulk("application/geo+json", ALL_PATH, 200);
    response.expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_json() {

    ExpectedResponse response =
    facilitiesRequestBulk("application/json", ALL_PATH, 200);
            response.expectValid(GeoFacilitiesResponse.class);

  }

  @Test
  void all_noAccept() {
    ExpectedResponse response =
    facilitiesRequestBulk(null, ALL_PATH, 200);

            response.expectValid(GeoFacilitiesResponse.class);
  }

  @Test
  void all_vndGeoJson() {
    ExpectedResponse response =
    facilitiesRequestBulk("application/vnd.geo+json", ALL_PATH, 200);

        response.expectValid(GeoFacilitiesResponse.class);
  }
}
