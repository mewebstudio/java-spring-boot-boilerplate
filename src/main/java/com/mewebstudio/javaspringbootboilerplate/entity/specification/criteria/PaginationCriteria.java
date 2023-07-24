package com.mewebstudio.javaspringbootboilerplate.entity.specification.criteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class PaginationCriteria {
    private Integer page;

    private Integer size;

    private String sortBy;

    private String sort;

    private String[] columns;
}
