package com.david13penalver.foss_training_api.domain.model.common;

public record PageQuery(int page, int size, String sortBy, String sortDirection) {

    public PageQuery {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 20;
        }
        if (sortDirection == null || (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc"))) {
            sortDirection = "asc";
        }
    }

    public static PageQuery of(int page, int size) {
        return new PageQuery(page, size, null, "asc");
    }

    public static PageQuery of(int page, int size, String sortBy, String sortDirection) {
        return new PageQuery(page, size, sortBy, sortDirection);
    }
}
