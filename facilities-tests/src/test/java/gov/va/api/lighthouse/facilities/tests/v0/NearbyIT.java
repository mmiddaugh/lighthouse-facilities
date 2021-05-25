package gov.va.api.lighthouse.facilities.tests.v0;

import static gov.va.api.lighthouse.facilities.tests.FacilitiesRequest.facilitiesRequest;
import static gov.va.api.lighthouse.facilities.tests.SystemDefinitions.systemDefinition;

import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import org.junit.jupiter.api.Test;

public class NearbyIT {
  @Test
  void searchByAddress() {
    final String streetAddress = systemDefinition().ids().streetAddress();
    final String city = systemDefinition().ids().city();
    final String state = systemDefinition().ids().state();
    final String zip = systemDefinition().ids().zip();
    final String request =
        "v0/nearby?street_address="
            + streetAddress
            + "&city="
            + city
            + "&state="
            + state
            + "&zip="
            + zip;
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByAddressWithDriveTime() {
    final String streetAddress = systemDefinition().ids().streetAddress();
    final String city = systemDefinition().ids().city();
    final String state = systemDefinition().ids().state();
    final String zip = systemDefinition().ids().zip();
    final String request =
        "v0/nearby?street_address="
            + streetAddress
            + "&city="
            + city
            + "&state="
            + state
            + "&zip="
            + zip
            + "&drive_time=90";
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByLatLong() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude;
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }

  @Test
  void searchByLatLongWithDriveTime() {
    final String latitude = systemDefinition().ids().latitude();
    final String longitude = systemDefinition().ids().longitude();
    final String request = "v0/nearby?lat=" + latitude + "&lng=" + longitude + "&drive_time=90";
    facilitiesRequest("application/json", request, 200).expectValid(NearbyResponse.class);
  }
}
