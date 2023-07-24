package com.mewebstudio.javaspringbootboilerplate.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PaginationResponse<T> extends AbstractBaseResponse {
    @Schema(
        name = "page",
        description = "Page",
        type = "Integer",
        example = "1"
    )
    private Integer page;

    @Schema(
        name = "pages",
        description = "Pages",
        type = "Integer",
        example = "3"
    )
    private Integer pages;

    @Schema(
        name = "size",
        description = "size",
        type = "Integer",
        example = "3"
    )
    private Integer size;

    @Schema(
        name = "total",
        description = "Total number of pages",
        type = "Integer",
        example = "10"
    )
    private Long total;

    @ArraySchema(
        schema = @Schema(
            type = "T",
            description = "items"
        )
    )
    private List<T> items;

    public PaginationResponse(final Page<?> pageModel, final List<T> items) {
        this.page = pageModel.getNumber() + 1;
        this.pages = pageModel.getTotalPages();
        this.size = pageModel.getSize();
        this.total = pageModel.getTotalElements();
        this.items = items;
    }
}
