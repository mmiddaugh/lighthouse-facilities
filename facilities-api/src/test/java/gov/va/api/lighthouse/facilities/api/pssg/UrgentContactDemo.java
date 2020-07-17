package gov.va.api.lighthouse.facilities.api.pssg;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact.Clinic;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact.Contact;
import gov.va.api.lighthouse.facilities.api.urgentcontact.UrgentContact.PhoneNumber;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;

public class UrgentContactDemo {
  @SneakyThrows
  public static void main(String[] args) {
    System.out.println(
        JacksonConfig.createMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(
                UrgentContact.builder()
                    .id("vha_675GA")
                    .facilityId("vha_675GA")
                    .clinic(null)
                    .contact(
                        UrgentContact.Contact.builder()
                            .name("Bob Nelson")
                            .email("bob.nelson@foo.bar")
                            .phone(
                                UrgentContact.PhoneNumber.builder().number("123-456-7890").build())
                            .build())
                    .note("foobar")
                    .phoneNumbers(
                        List.of(
                            UrgentContact.PhoneNumber.builder()
                                .label("Primary Care")
                                .number("123-456-7890")
                                .extension("9999")
                                .build(),
                            UrgentContact.PhoneNumber.builder()
                                .label("Mental Health")
                                .number("123-456-7890")
                                .extension("9999")
                                .build(),
                            UrgentContact.PhoneNumber.builder()
                                .label("Main")
                                .number("123-456-7890")
                                .extension("9999")
                                .build()))
                    .lastUpdated(Instant.now())
                    .build()));

    System.out.println(
        JacksonConfig.createMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(
                UrgentContact.builder()
                    .id("vha_675GA_1")
                    .facilityId("vha_675GA")
                    .clinic(
                        UrgentContact.Clinic.builder()
                            .name("cardiology")
                            .specialty("cardiology")
                            .build())
                    .contact(
                        UrgentContact.Contact.builder()
                            .name("Bob Nelson")
                            .email("bob.nelson@foo.bar")
                            .phone(
                                UrgentContact.PhoneNumber.builder().number("123-456-7890").build())
                            .build())
                    .note("foobar")
                    .phoneNumbers(
                        List.of(
                            UrgentContact.PhoneNumber.builder()
                                .number("123-456-7890")
                                .extension("9999")
                                .build(),
                            UrgentContact.PhoneNumber.builder()
                                .number("123-456-7890")
                                .extension("9999")
                                .build(),
                            UrgentContact.PhoneNumber.builder()
                                .number("123-456-7890")
                                .extension("9999")
                                .build()))
                    .lastUpdated(Instant.now())
                    .build()));
  }
}
