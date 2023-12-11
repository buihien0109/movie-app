package com.example.movieapp.response;

import java.util.List;

public interface PageResponse<T> {
    Integer getCurrentPage();

    Integer getLimit();

    Integer getTotalElements();

    Integer getTotalPages();

    List<T> getContent();

    Integer getPreviousPage();

    Integer getNextPage();

    Boolean hasPreviousPage();

    Boolean hasNextPage();

    Boolean isFirstPage();

    Boolean isLastPage();
}
