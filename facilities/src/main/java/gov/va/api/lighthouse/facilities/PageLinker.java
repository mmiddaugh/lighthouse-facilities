package gov.va.api.lighthouse.facilities;

import static com.google.common.base.Preconditions.checkArgument;

import gov.va.api.lighthouse.facilities.api.v0.PageLinks;
import gov.va.api.lighthouse.facilities.api.v0.Pagination;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

final class PageLinker {
  final String url;

  final MultiValueMap<String, String> params;

  final Integer totalEntries;

  @Builder
  PageLinker(
      @NonNull String url,
      @NonNull MultiValueMap<String, String> params,
      @NonNull Integer totalEntries) {
    checkArgument(Parameters.pageOf(params) >= 1);
    checkArgument(Parameters.perPageOf(params) >= 0);
    checkArgument(totalEntries >= 0);
    this.url = url;
    this.params = params;
    this.totalEntries = totalEntries;
  }

  private static Stream<String> toKeyValueString(Map.Entry<String, List<String>> entry) {
    return entry.getValue().stream()
        .map(
            value ->
                URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                    + '='
                    + URLEncoder.encode(value, StandardCharsets.UTF_8));
  }

  private int lastPage() {
    if (totalEntries == 0) {
      return 1;
    }
    return (int) Math.ceil((double) totalEntries / (double) Parameters.perPageOf(params));
  }

  PageLinks links() {
    int page = Parameters.pageOf(params);
    int perPage = Parameters.perPageOf(params);
    // If perPage == 0, only return the self link
    if (perPage == 0) {
      return PageLinks.builder().self(toUrl(page)).build();
    }
    int lastPage = lastPage();
    boolean hasPrevious = page >= 2 && page <= lastPage + 1;
    boolean hasNext = page <= lastPage - 1;
    return PageLinks.builder()
        .self(toUrl(page))
        .first(toUrl(1))
        .prev(hasPrevious ? toUrl(page - 1) : null)
        .next(hasNext ? toUrl(page + 1) : null)
        .last(toUrl(lastPage))
        .build();
  }

  Pagination pagination() {
    int page = Parameters.pageOf(params);
    int perPage = Parameters.perPageOf(params);
    return Pagination.builder()
        .currentPage(page)
        .entriesPerPage(perPage)
        .totalPages(perPage == 0 ? 0 : lastPage())
        .totalEntries(totalEntries)
        .build();
  }

  private String toUrl(int currentPage) {
    MultiValueMap<String, String> mutableParams = new LinkedMultiValueMap<>(params);
    mutableParams.remove("page");
    mutableParams.remove("per_page");
    StringBuilder sb = new StringBuilder(url).append('?');
    String joinedParams =
        mutableParams.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getKey))
            .flatMap(PageLinker::toKeyValueString)
            .collect(Collectors.joining("&"));
    if (!joinedParams.isEmpty()) {
      sb.append(joinedParams).append('&');
    }
    return sb.append("page=")
        .append(currentPage)
        .append("&per_page=")
        .append(Parameters.perPageOf(params))
        .toString();
  }
}
