package com.david13penalver.foss_training_api.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionPart {

    private int id;
    private SessionPartEnum sessionPartEnum;

}
