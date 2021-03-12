package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.CollectionUtils.unmodifiableMultiValueMap;

import java.util.List;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@NoArgsConstructor(staticName = "builder")
final class Parameters {
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

  static int pageOf(@NonNull MultiValueMap<String, String> parameters) {
    String page = parameters.getFirst("page");
    if (page == null) {
      throw new IllegalArgumentException(parameters.toString());
    }
    return Integer.parseInt(page);
  }

  static int perPageOf(@NonNull MultiValueMap<String, String> parameters) {
    String perPage = parameters.getFirst("per_page");
    if (perPage == null) {
      throw new IllegalArgumentException(parameters.toString());
    }
    return Integer.parseInt(perPage);
  }

  Parameters add(String key, int value) {
    params.add(key, Integer.toString(value));
    return this;
  }

  Parameters add(String key, @NonNull Object value) {
    params.add(key, value.toString());
    return this;
  }

  Parameters addAll(String key, List<?> values) {
    if (values != null && !isEmpty(values)) {
      for (Object val : values) {
        checkArgument(val != null);
        params.add(key, val.toString());
      }
    }
    return this;
  }

  Parameters addIgnoreNull(String key, Object value) {
    if (value != null) {
      params.add(key, value.toString());
    }
    return this;
  }

  MultiValueMap<String, String> build() {
    return unmodifiableMultiValueMap(params);
  }
}
