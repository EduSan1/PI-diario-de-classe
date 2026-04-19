package com.diarioclasse.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;

import java.util.List;

public record PaginaResponse<T>(
        List<T> content,
        PageableInfo pageable,
        String nextPage,
        String previousPage,
        long totalElements,
        int totalPages,
        @JsonInclude(JsonInclude.Include.NON_NULL) Integer numberOfElements
) {

    public record PageableInfo(int pageNumber, int pageSize, long offset) {}

    public static <T> PaginaResponse<T> from(Page<T> page, String baseUrl) {
        String next = page.hasNext()
                ? baseUrl + "?page=" + (page.getNumber() + 1) + "&size=" + page.getSize()
                : null;
        String previous = page.hasPrevious()
                ? baseUrl + "?page=" + (page.getNumber() - 1) + "&size=" + page.getSize()
                : null;
        Integer numElements = page.getNumberOfElements() != page.getTotalElements()
                ? page.getNumberOfElements()
                : null;

        return new PaginaResponse<>(
                page.getContent(),
                new PageableInfo(page.getNumber(), page.getSize(), page.getPageable().getOffset()),
                next,
                previous,
                page.getTotalElements(),
                page.getTotalPages(),
                numElements
        );
    }
}
