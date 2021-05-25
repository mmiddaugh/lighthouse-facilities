package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import gov.va.api.lighthouse.facilities.api.v0.Facility;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Controllers {
  private static final Map<String, FacilityEntity.Type> ENTITY_TYPE_LOOKUP =
      caseInsensitiveMap(
          ImmutableMap.of(
              "benefits",
              FacilityEntity.Type.vba,
              "cemetery",
              FacilityEntity.Type.nca,
              "health",
              FacilityEntity.Type.vha,
              "vet_center",
              FacilityEntity.Type.vc));

  private static final Map<String, Facility.ServiceType> SERVICE_LOOKUP =
      caseInsensitiveMap(
          Streams.stream(
                  Iterables.concat(
                      List.<Facility.ServiceType>of(Facility.HealthService.values()),
                      List.<Facility.ServiceType>of(Facility.BenefitsService.values()),
                      List.<Facility.ServiceType>of(Facility.OtherService.values())))
              .collect(toMap(v -> v.toString(), Function.identity())));

  private static <T> Map<String, T> caseInsensitiveMap(@NonNull Map<String, T> source) {
    Map<String, T> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.putAll(source);
    return Collections.unmodifiableMap(map);
  }

  /** List of objects per page. */
  public static <T> List<T> page(List<T> objects, int page, int perPage) {
    checkArgument(page >= 1);
    checkArgument(perPage >= 0);
    if (perPage == 0) {
      return emptyList();
    }
    int fromIndex = (page - 1) * perPage;
    if (objects.size() < fromIndex) {
      return emptyList();
    }
    return objects.subList(fromIndex, Math.min(fromIndex + perPage, objects.size()));
  }

  /** Validates facility types. */
  public static FacilityEntity.Type validateFacilityType(String type) {
    FacilityEntity.Type mapped = ENTITY_TYPE_LOOKUP.get(trimToEmpty(type));
    if (mapped == null && isNotBlank(type)) {
      throw new ApiExceptions.InvalidParameter("type", type);
    }
    return mapped;
  }

  /** Validates services. */
  public static Set<Facility.ServiceType> validateServices(Collection<String> services) {
    if (isEmpty(services)) {
      return emptySet();
    }
    List<Facility.ServiceType> results = new ArrayList<>(services.size());
    for (String service : services) {
      Facility.ServiceType mapped = SERVICE_LOOKUP.get(trimToEmpty(service));
      if (mapped == null) {
        throw new ApiExceptions.InvalidParameter("services", service);
      }
      results.add(mapped);
    }
    return ImmutableSet.copyOf(results);
  }
}
