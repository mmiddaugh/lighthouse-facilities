package gov.va.api.lighthouse.facilities.tests;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v0.ApiError;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(RequiresFacilitiesExtension.class)
public class SearchIT {
  @Test
  void searchByBoundingBox() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox;
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByBoundingBoxMutuallyExclusive() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&lat=" + latitude + "&long=" + longitude;
    facilitiesRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/json", request, 400).expectValid(ApiError.class);
    facilitiesRequest(null, request, 400).expectValid(ApiError.class);
  }

  @Test
  void searchByBoundingBoxWithServices() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByBoundingBoxWithType() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&type=health";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByBoundingBoxWithTypeAndServices() {
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?" + bbox + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByIds() {
    final String facilities = systemDefinition().facilitiesIds().facilitiesList();
    final String request = "v0/facilities?ids=" + facilities;
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLong() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/facilities?lat=" + latitude + "&long=" + longitude;
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongMutuallyExclusive() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String state = systemDefinition().facilitiesIds().state();
    final String request =
        "v0/facilities?lat=" + latitude + "&long=" + longitude + "&state=" + state;
    facilitiesRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/json", request, 400).expectValid(ApiError.class);
    facilitiesRequest(null, request, 400).expectValid(ApiError.class);
  }

  @Test
  void searchByLatLongWithIds() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String facilities = systemDefinition().facilitiesIds().facilitiesList();
    final String request =
        "v0/facilities?lat=" + latitude + "&long=" + longitude + "&ids=" + facilities;
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongWithServices() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request =
        "v0/facilities?lat=" + latitude + "&long=" + longitude + "&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongWithType() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request = "v0/facilities?lat=" + latitude + "&long=" + longitude + "&type=health";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByLatLongWithTypeAndServices() {
    final String latitude = systemDefinition().facilitiesIds().latitude();
    final String longitude = systemDefinition().facilitiesIds().longitude();
    final String request =
        "v0/facilities?lat="
            + latitude
            + "&long="
            + longitude
            + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByState() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state;
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByStateMutuallyExclusive() {
    final String state = systemDefinition().facilitiesIds().state();
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?state=" + state + "&zip=" + zip;
    facilitiesRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/json", request, 400).expectValid(ApiError.class);
    facilitiesRequest(null, request, 400).expectValid(ApiError.class);
  }

  @Test
  void searchByStateWithServices() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByStateWithType() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&type=health";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByStateWithTypeAndServices() {
    final String state = systemDefinition().facilitiesIds().state();
    final String request = "v0/facilities?state=" + state + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZip() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip;
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZipMutuallyExclusive() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String bbox = systemDefinition().facilitiesIds().bbox();
    final String request = "v0/facilities?zip=" + zip + "&" + bbox;
    facilitiesRequest("application/vnd.geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/geo+json", request, 400).expectValid(ApiError.class);
    facilitiesRequest("application/json", request, 400).expectValid(ApiError.class);
    facilitiesRequest(null, request, 400).expectValid(ApiError.class);
  }

  @Test
  void searchByZipWithServices() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZipWithType() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&type=health";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }

  @Test
  void searchByZipWithTypeAndServices() {
    final String zip = systemDefinition().facilitiesIds().zip();
    final String request = "v0/facilities?zip=" + zip + "&type=health&services[]=PrimaryCare";
    facilitiesRequest("application/vnd.geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/geo+json", request, 200)
        .expectValid(GeoFacilitiesResponse.class);
    facilitiesRequest("application/json", request, 200).expectValid(FacilitiesResponse.class);
    facilitiesRequest(null, request, 200).expectValid(FacilitiesResponse.class);
  }
}