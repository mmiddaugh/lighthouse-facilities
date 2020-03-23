package gov.va.api.lighthouse.facilities;

import static gov.va.api.lighthouse.facilities.FacilitiesJacksonConfig.createMapper;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gov.va.api.lighthouse.facilities.api.v0.FacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.FacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilitiesResponse;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacility;
import gov.va.api.lighthouse.facilities.api.v0.GeoFacilityReadResponse;
import gov.va.api.lighthouse.facilities.api.v0.NearbyResponse;
import lombok.SneakyThrows;
import org.junit.Test;

/**
 * These tests exercise round-trip deserialization and re-serialization of responses from the
 * original Rails Facilities API, verifying equality. Responses are used precisely as originally
 * captured, with the exception of string trimming. We have not implemented custom deserialization
 * behavior, so string-trimming is still applied during deserialization. Because custom
 * deserialization is not required in the app in production, we can accept this for unit tests; the
 * source messages have been edited to replace empty strings with null, and to remove any trailing
 * whitespace.
 */
public class SerializationV0Test {
  @Test
  @SneakyThrows
  public void all() {
    GeoFacilitiesResponse actual =
        createMapper()
            .readValue(getClass().getResourceAsStream("/all.json"), GeoFacilitiesResponse.class);
    assertThat(actual).isExactlyInstanceOf(GeoFacilitiesResponse.class);
    ObjectNode expected =
        (ObjectNode) createMapper().readTree(getClass().getResourceAsStream("/all.json"));
    ArrayNode expectedFeatures = (ArrayNode) expected.get("features");
    for (int i = 0; i < expectedFeatures.size(); i++) {
      JsonNode expectedFeature = expectedFeatures.get(i);
      GeoFacility actualNode = actual.features().get(i);
      assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(actualNode))
          .isEqualTo(
              createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(expectedFeature));
    }
  }

  @Test
  @SneakyThrows
  public void nearby() {
    String path = "/nearby.json";
    roundTrip(path, NearbyResponse.class);
  }

  @Test
  @SneakyThrows
  public void readBenefits() {
    String path = "/read-benefits.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readBenefitsGeoJson() {
    String path = "/read-benefits-geojson.json";
    roundTrip(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readCemetery() {
    String path = "/read-cemetery.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readCemeteryGeoJson() {
    String path = "/read-cemetery-geojson.json";
    roundTrip(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readHealth() {
    String path = "/read-health.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readHealthGeoJson() {
    String path = "/read-health-geojson.json";
    roundTrip(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readStateCemetery() {
    String path = "/read-state-cemetery.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readStateCemeteryGeoJson() {
    String path = "/read-state-cemetery-geojson.json";
    roundTrip(path, GeoFacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readVetCenter() {
    String path = "/read-vet-center.json";
    roundTrip(path, FacilityReadResponse.class);
  }

  @Test
  @SneakyThrows
  public void readVetCenterGeoJson() {
    String path = "/read-vet-center-geojson.json";
    roundTrip(path, GeoFacilityReadResponse.class);
  }

  @SneakyThrows
  private <T> void roundTrip(String path, Class<T> clazz) {
    T response = createMapper().readValue(getClass().getResourceAsStream(path), clazz);
    assertThat(response).isExactlyInstanceOf(clazz);
    assertThat(createMapper().writerWithDefaultPrettyPrinter().writeValueAsString(response))
        .isEqualTo(
            createMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(createMapper().readTree(getClass().getResourceAsStream(path))));
  }

  @Test
  @SneakyThrows
  public void searchByBbox() {
    String path = "/search-bbox.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByBboxGeoJson() {
    String path = "/search-bbox-geojson.json";
    roundTrip(path, GeoFacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByIds() {
    String path = "/search-ids.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByIdsGeoJson() {
    String path = "/search-ids-geojson.json";
    roundTrip(path, GeoFacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByLatLong() {
    String path = "/search-lat-long.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByLatLongGeoJson() {
    String path = "/search-lat-long-geojson.json";
    roundTrip(path, GeoFacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByState() {
    String path = "/search-state.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByStateGeoJson() {
    String path = "/search-state-geojson.json";
    roundTrip(path, GeoFacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByZip() {
    String path = "/search-zip.json";
    roundTrip(path, FacilitiesResponse.class);
  }

  @Test
  @SneakyThrows
  public void searchByZipGeoJson() {
    String path = "/search-zip-geojson.json";
    roundTrip(path, GeoFacilitiesResponse.class);
  }
}