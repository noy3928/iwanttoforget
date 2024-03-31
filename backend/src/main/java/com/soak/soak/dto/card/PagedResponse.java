package com.soak.soak.dto.card;

import com.soak.soak.model.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    private PageInfo pageInfo;
    private List<T> data;
}
