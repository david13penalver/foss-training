package com.david13penalver.foss_training_api.domain.model.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionPart {

    private int id;
    private Session session;
    private SessionPartEnum sessionPartEnum;

}
