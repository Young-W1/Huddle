package com.capstone.huddle.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper for paginated API responses with metadata.
 * Provides consistent pagination information across all list endpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private boolean success;
    private String message;
    private List<T> content;
    private PaginationMetadata pagination;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    /**
     * Create a successful paged response from a Spring Data Page object.
     */
    public static <T> PagedResponse<T> of(Page<T> page, String message) {
        return PagedResponse.<T>builder()
                .success(true)
                .message(message)
                .content(page.getContent())
                .pagination(PaginationMetadata.builder()
                        .page(page.getNumber())
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .first(page.isFirst())
                        .last(page.isLast())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .build();
    }

    /**
     * Create an error paged response.
     */
    public static <T> PagedResponse<T> error(String message) {
        return PagedResponse.<T>builder()
                .success(false)
                .message(message)
                .content(List.of())
                .pagination(PaginationMetadata.builder()
                        .page(0)
                        .size(0)
                        .totalElements(0)
                        .totalPages(0)
                        .first(true)
                        .last(true)
                        .hasNext(false)
                        .hasPrevious(false)
                        .build())
                .build();
    }
}

