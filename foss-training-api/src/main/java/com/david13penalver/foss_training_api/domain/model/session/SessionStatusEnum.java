package com.david13penalver.foss_training_api.domain.model.session;

import lombok.Data;

public enum SessionStatusEnum {

    DRAFT,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    SKIPPED,
    PAUSED;

    public String toString() {
        return this.name();
    }

    public static SessionStatusEnum fromString(String text) {
        for (SessionStatusEnum b : SessionStatusEnum.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

}
