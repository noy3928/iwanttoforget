package com.soak.soak.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
