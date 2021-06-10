package gov.va.api.lighthouse.facilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InternalCollectorControllerTest {
  @Autowired JdbcTemplate template;

  @Autowired TestEntityManager testEntityManager;

  @Test
  void mentalHealthContacts() {
    template.execute(
        "CREATE TABLE App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber VARCHAR,"
            + "MHPhone VARCHAR,"
            + "Extension VARCHAR"
            + ")");
    template.execute(
        "INSERT INTO App.VHA_Mental_Health_Contact_Info ("
            + "StationNumber,"
            + "MHPhone,"
            + "Extension"
            + ") VALUES ("
            + "'999',"
            + "'800-867-5309',"
            + "'1234'"
            + ")");
    assertThat(InternalCollectorController.builder().jdbc(template).build().mentalHealthContacts())
        .isEqualTo(
            List.of(
                Map.of("STATIONNUMBER", "999", "MHPHONE", "800-867-5309", "EXTENSION", "1234")));
  }

  @Test
  @SneakyThrows
  void nca() {
    template.execute(
        "CREATE TABLE App.FacilityLocator_NCA (" + "FULL_NAME VARCHAR," + "SITE_ID VARCHAR" + ")");
    template.execute(
        "INSERT INTO App.FacilityLocator_NCA ("
            + "FULL_NAME,"
            + "SITE_ID"
            + ") VALUES ("
            + "'Some NCA',"
            + "'123'"
            + ")");
    assertThat(InternalCollectorController.builder().jdbc(template).build().nca())
        .isEqualTo(List.of(Map.of("FULL_NAME", "Some NCA", "SITE_ID", "123")));
  }

  @Test
  @SneakyThrows
  void stopCodes() {
    template.execute(
        "CREATE TABLE App.VSSC_ClinicalServices ("
            + "Sta6a VARCHAR,"
            + "PrimaryStopCode VARCHAR,"
            + "PrimaryStopCodeName VARCHAR,"
            + "AvgWaitTimeNew VARCHAR"
            + ")");
    template.execute(
        "INSERT INTO App.VSSC_ClinicalServices ("
            + "Sta6a,"
            + "PrimaryStopCode,"
            + "PrimaryStopCodeName,"
            + "AvgWaitTimeNew"
            + ") VALUES ("
            + "'402GA',"
            + "'123',"
            + "'PRIMARY CARE/MEDICINE',"
            + "'14.15'"
            + ")");
    assertThat(InternalCollectorController.builder().jdbc(template).build().stopCodes())
        .isEqualTo(
            List.of(
                Map.of(
                    "STA6A",
                    "402GA",
                    "PRIMARYSTOPCODE",
                    "123",
                    "PRIMARYSTOPCODENAME",
                    "PRIMARY CARE/MEDICINE",
                    "AVGWAITTIMENEW",
                    "14.15")));
  }

  @Test
  @SneakyThrows
  void vast() {
    template.execute(
        "CREATE TABLE App.Vast (" + "StationNumber VARCHAR," + "StationName VARCHAR" + ")");
    template.execute(
        "INSERT INTO App.Vast ("
            + "StationNumber,"
            + "StationName"
            + ") VALUES ("
            + "'123',"
            + "'Some VAMC'"
            + ")");
    assertThat(InternalCollectorController.builder().jdbc(template).build().vast())
        .isEqualTo(List.of(Map.of("STATIONNUMBER", "123", "STATIONNAME", "Some VAMC")));
  }

  @Test
  @SneakyThrows
  void vba() {
    template.execute(
        "CREATE TABLE App.FacilityLocator_VBA ("
            + "FACILITY_NAME VARCHAR,"
            + "FACILITY_NUMBER VARCHAR"
            + ")");
    template.execute(
        "INSERT INTO App.FacilityLocator_VBA ("
            + "FACILITY_NAME,"
            + "FACILITY_NUMBER"
            + ") VALUES ("
            + "'Some VBA',"
            + "'123'"
            + ")");
    assertThat(InternalCollectorController.builder().jdbc(template).build().vba())
        .isEqualTo(List.of(Map.of("FACILITY_NAME", "Some VBA", "FACILITY_NUMBER", "123")));
  }
}
