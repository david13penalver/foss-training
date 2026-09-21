package com.david13penalver.foss_training_api.infrastructure.adapters.in.rest.dto.common;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Paginated response wrapper containing page metadata and content elements")
public class PageResponseDto<T> {

    @Schema(description = "Page elements")
    private List<T> content;

    @Schema(description = "Current 0-based page index", example = "0")
    private int page;

    @Schema(description = "Number of elements per page", example = "20")
    private int size;

    @Schema(description = "Total number of elements matching the query", example = "42")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "3")
    private int totalPages;

    @Schema(description = "Whether this is the first page", example = "true")
    private boolean first;

    @Schema(description = "Whether this is the last page", example = "false")
    private boolean last;

    @Schema(description = "Whether there is a subsequent page", example = "true")
    private boolean hasNext;

    @Schema(description = "Whether there is a preceding page", example = "false")
    private boolean hasPrevious;
}
