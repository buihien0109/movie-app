package com.example.movieapp.response;

import com.example.movieapp.model.Film;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageResponseImpl<T> {
    @JsonIgnore
    List<T> data;

    @JsonIgnore
    Integer currentPage;

    @JsonIgnore
    Integer limit;

    public Integer getCurrentPage() {
        return data.size();
    }

    public Integer getTotalPage() {
        return (int) Math.ceil((double) data.size() / limit);
    }

    public List<T> getContent() {
        int fromIndex = (currentPage - 1) * limit;
        int toIndex = Math.min(fromIndex + limit, data.size());
        return data.subList(fromIndex, toIndex);
    }

    public Integer getPreviousPage() {
        return currentPage - 1;
    }

    public Integer getNextPage() {
        return currentPage + 1;
    }

    public boolean hasPreviousPage() {
        return currentPage > 1;
    }

    public boolean hasNextPage() {
        return currentPage < getTotalPage();
    }

    public boolean isFirstPage() {
        return currentPage == 1;
    }

    public boolean isLastPage() {
        return currentPage.equals(getTotalPage());
    }
}
