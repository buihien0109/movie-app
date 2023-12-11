package com.example.movieapp.response;

import com.example.movieapp.model.Film;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageResponseImpl<T> implements PageResponse<T> {
    @JsonIgnore
    private List<T> data;
    @JsonIgnore
    private Integer currentPage;
    @JsonIgnore
    private Integer limit;

    @Override
    public Integer getCurrentPage() {
        return currentPage;
    }

    @Override
    public Integer getLimit() {
        return limit;
    }

    @Override
    public Integer getTotalElements() {
        return data.size();
    }

    @Override
    public Integer getTotalPages() {
        return (int) Math.ceil((double) data.size() / limit);
    }

    @Override
    public List<T> getContent() {
        int fromIndex = (currentPage - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, data.size());
        return data.subList(fromIndex, toIndex);
    }

    @Override
    public Integer getPreviousPage() {
        return currentPage - 1;
    }

    @Override
    public Integer getNextPage() {
        return currentPage + 1;
    }

    @Override
    public Boolean hasPreviousPage() {
        return currentPage > 1;
    }

    @Override
    public Boolean hasNextPage() {
        return currentPage < getTotalPages();
    }

    @Override
    public Boolean isFirstPage() {
        return currentPage == 1;
    }

    @Override
    public Boolean isLastPage() {
        return currentPage.equals(getTotalPages());
    }
}
