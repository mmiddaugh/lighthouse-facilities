# facilities

Primary Facilities application.

## Location Searches
A variety of location-based searches are supported, including state, zip,
GPS coordinates, and GPS coordinates bounding box, with optional filters by facility
type and services provided.

Example:

`http://localhost:8085/v0/facilities?lat=28.112464&long=-80.7015994&type=health&services[]=audiology&per_page=2`

```
{
  "data" : [
    {
      "id" : "vha_675GA",
      "type" : "va_facilities",
      "attributes" : {
        "name" : "Viera VA Clinic",
        "facility_type" : "va_health_facility",
        "classification" : "Health Care Center (HCC)",
        "website" : "https://www.orlando.va.gov/locations/Viera.asp",
        "lat" : 28.255238570000074,
        "long" : -80.73907112999996,
        "address" : {
          "mailing" : { },
          "physical" : {
            "zip" : "32940-8007",
            "city" : "Viera",
            "state" : "FL",
            "address_1" : "2900 Veterans Way",
            "address_2" : null,
            "address_3" : null
          }
        },
        "phone" : {
          "fax" : "321-637-3515",
          "main" : "321-637-3788",
          "pharmacy" : "877-646-4550",
          "after_hours" : "877-741-3400",
          "patient_advocate" : "321-637-3534",
          "mental_health_clinic" : "321-637-3788",
          "enrollment_coordinator" : "321-637-3527"
        },
        "hours" : {
          "friday" : "730AM-430PM",
          "monday" : "730AM-430PM",
          "sunday" : "Closed",
          "tuesday" : "730AM-430PM",
          "saturday" : "Closed",
          "thursday" : "730AM-430PM",
          "wednesday" : "730AM-430PM"
        },
        "services" : {
          "other" : [ ],
          "health" : [
            "Audiology",
            "Cardiology",
            "DentalServices",
            "Dermatology",
            "Gastroenterology",
            "MentalHealthCare",
            "Nutrition",
            "Ophthalmology",
            "Optometry",
            "Orthopedics",
            "Podiatry",
            "PrimaryCare",
            "SpecialtyCare",
            "Urology"
          ],
          "last_updated" : "2020-03-30"
        },
        "satisfaction" : {
          "health" : {
            "primary_care_urgent" : 0.7400000095367432,
            "primary_care_routine" : 0.8299999833106995
          },
          "effective_date" : "2019-06-20"
        },
        "wait_times" : {
          "health" : [
            {
              "service" : "Audiology",
              "new" : 1.534246,
              "established" : 1.681506
            },
            {
              "service" : "Cardiology",
              "new" : 18.966666,
              "established" : 6.538888
            },
            {
              "service" : "Dermatology",
              "new" : 0.268292,
              "established" : 0.125
            },
            {
              "service" : "Gastroenterology",
              "new" : 23.990566,
              "established" : 2.981481
            },
            {
              "service" : "MentalHealthCare",
              "new" : 8.575,
              "established" : 3.294549
            },
            {
              "service" : "Ophthalmology",
              "new" : 15.125,
              "established" : 1.70886
            },
            {
              "service" : "Optometry",
              "new" : 72.647058,
              "established" : 5.76
            },
            {
              "service" : "Orthopedics",
              "new" : 33.272727,
              "established" : 5.823529
            },
            {
              "service" : "PrimaryCare",
              "new" : 15.75909,
              "established" : 0.881985
            },
            {
              "service" : "SpecialtyCare",
              "new" : 20.566895,
              "established" : 4.51956
            },
            {
              "service" : "Urology",
              "new" : 52.4,
              "established" : 6.463768
            }
          ],
          "effective_date" : "2020-03-30"
        },
        "mobile" : false,
        "active_status" : "A",
        "operating_status" : {
          "code" : "NORMAL"
        },
        "visn" : "8"
      }
    },
    {
      "id" : "vha_675",
      "type" : "va_facilities",
      "attributes" : {
        "name" : "Orlando VA Medical Center",
        "facility_type" : "va_health_facility",
        "classification" : "VA Medical Center (VAMC)",
        "website" : "https://www.orlando.va.gov/locations/directions.asp",
        "lat" : 28.366691570000057,
        "long" : -81.27650338999996,
        "address" : {
          "mailing" : { },
          "physical" : {
            "zip" : "32827-5812",
            "city" : "Orlando",
            "state" : "FL",
            "address_1" : "13800 Veterans Way",
            "address_2" : null,
            "address_3" : null
          }
        },
        "phone" : {
          "fax" : "407-631-0100",
          "main" : "407-631-1000",
          "pharmacy" : "407-646-4500",
          "after_hours" : "877-741-3400",
          "patient_advocate" : "407-631-1187",
          "mental_health_clinic" : "407-631-2050",
          "enrollment_coordinator" : "407-631-1060"
        },
        "hours" : {
          "friday" : "24/7",
          "monday" : "24/7",
          "sunday" : "24/7",
          "tuesday" : "24/7",
          "saturday" : "24/7",
          "thursday" : "24/7",
          "wednesday" : "24/7"
        },
        "services" : {
          "other" : [ ],
          "health" : [
            "Audiology",
            "Cardiology",
            "DentalServices",
            "Dermatology",
            "Gastroenterology",
            "Gynecology",
            "MentalHealthCare",
            "Nutrition",
            "Ophthalmology",
            "Optometry",
            "Orthopedics",
            "Podiatry",
            "PrimaryCare",
            "SpecialtyCare",
            "Urology",
            "WomensHealth"
          ],
          "last_updated" : "2020-03-30"
        },
        "satisfaction" : {
          "health" : {
            "primary_care_urgent" : 0.5899999737739563,
            "primary_care_routine" : 0.7699999809265137,
            "specialty_care_urgent" : 0.7599999904632568,
            "specialty_care_routine" : 0.8299999833106995
          },
          "effective_date" : "2019-06-20"
        },
        "wait_times" : {
          "health" : [
            {
              "service" : "Audiology",
              "new" : 3.833333,
              "established" : 2.463043
            },
            {
              "service" : "Cardiology",
              "new" : 34.048192,
              "established" : 7.066213
            },
            {
              "service" : "Dermatology",
              "new" : 15.788381,
              "established" : 2.315789
            },
            {
              "service" : "Gastroenterology",
              "new" : 48.716216,
              "established" : 20.689486
            },
            {
              "service" : "Gynecology",
              "new" : 16.688524,
              "established" : 2.222672
            },
            {
              "service" : "MentalHealthCare",
              "new" : 10.0229,
              "established" : 5.082898
            },
            {
              "service" : "Ophthalmology",
              "new" : 8.057142,
              "established" : 6.154471
            },
            {
              "service" : "Optometry",
              "new" : 42.463414,
              "established" : 6.361955
            },
            {
              "service" : "Orthopedics",
              "new" : 17.644067,
              "established" : 5.800711
            },
            {
              "service" : "PrimaryCare",
              "new" : 20.384615,
              "established" : 2.594115
            },
            {
              "service" : "SpecialtyCare",
              "new" : 25.287425,
              "established" : 6.989446
            },
            {
              "service" : "Urology",
              "new" : 55.364583,
              "established" : 16.009881
            },
            {
              "service" : "WomensHealth",
              "new" : 22.428571,
              "established" : 1.744
            }
          ],
          "effective_date" : "2020-03-30"
        },
        "mobile" : false,
        "active_status" : "A",
        "operating_status" : {
          "code" : "NORMAL"
        },
        "visn" : "8"
      }
    }
  ],
  "links" : {
    "self" : "http://localhost:8085/v0/facilities?lat=28.112464&long=-80.7015994&services%5B%5D=audiology&type=health&page=1&per_page=2",
    "first" : "http://localhost:8085/v0/facilities?lat=28.112464&long=-80.7015994&services%5B%5D=audiology&type=health&page=1&per_page=2",
    "prev" : null,
    "next" : "http://localhost:8085/v0/facilities?lat=28.112464&long=-80.7015994&services%5B%5D=audiology&type=health&page=2&per_page=2",
    "last" : "http://localhost:8085/v0/facilities?lat=28.112464&long=-80.7015994&services%5B%5D=audiology&type=health&page=263&per_page=2"
  },
  "meta" : {
    "pagination" : {
      "current_page" : 1,
      "per_page" : 2,
      "total_pages" : 263,
      "total_entries" : 525
    },
    "distances" : [
      {
        "id" : "vha_675GA",
        "distance" : 10.13
      },
      {
        "id" : "vha_675",
        "distance" : 39.16
      }
    ]
  }
}
```

## Nearby Facilities
The API supports queries for facilities that are near a given location,
which may be GPS coordinates or an address.
(For the latter, [Bing Maps API](https://docs.microsoft.com/en-us/bingmaps/rest-services/)
is used to geocode the address.)

Nearby calculations are based on drivetime bands, which are isochrones
around VA Facilities, in ten-minute increments. This data is provided by
[PSSG](https://vhanflwebgistst.v08.med.va.gov) (VA network only).
(At the time of this writing, PSSG is unavailable, so an offline copy of
the drivetime bands has been manually uploaded into the higher environments.)

Example:

`http://localhost:8085/v0/nearby?lat=28.112464&lng=-80.7015994&drive_time=30`

```
{
  "data" : [
    {
      "id" : "vha_675GA",
      "type" : "nearby_facility",
      "attributes" : {
        "min_time" : 10,
        "max_time" : 20
      },
      "relationships" : {
        "va_facility" : {
          "links" : {
            "related" : "http://localhost:8085/v0/facilities/vha_675GA"
          }
        }
      }
    }
  ],
  "links" : {
    "related" : "http://localhost:8085/v0/facilities?ids=vha_675GA",
    "self" : "http://localhost:8085/v0/nearby?drive_time=30&lat=28.112464&lng=-80.7015994&page=1&per_page=20",
    "first" : "http://localhost:8085/v0/nearby?drive_time=30&lat=28.112464&lng=-80.7015994&page=1&per_page=20",
    "prev" : null,
    "next" : null,
    "last" : "http://localhost:8085/v0/nearby?drive_time=30&lat=28.112464&lng=-80.7015994&page=1&per_page=20"
  },
  "meta" : {
    "pagination" : {
      "current_page" : 1,
      "per_page" : 20,
      "total_pages" : 1,
      "total_entries" : 1
    }
  }
}
```

## CMS Updates
Some facilities information is provided by the
[va.gov Content Management System](https://github.com/department-of-veterans-affairs/va.gov-cms).

Example:

```
curl -s -w %{http_code} http://localhost:8085/v0/facilities/vha_402/cms-overlay \
-HContent-Type:application/json \
-d'{"operating_status":{"code":"CLOSED","additional_info":"flavor text"}}'
```

## Local Development

`../make-configs.sh`

Use `less config/application-dev.properties` to verify application properties for local development.

`java -Dspring.profiles.active=dev -jar target/facilities-${VERSION}.jar`