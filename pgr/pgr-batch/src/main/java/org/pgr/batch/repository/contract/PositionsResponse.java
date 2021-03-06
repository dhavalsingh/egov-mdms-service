package org.pgr.batch.repository.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pgr.batch.service.model.Position;

import java.util.List;

@Getter
@AllArgsConstructor
public class PositionsResponse {

    @JsonProperty("Position")
    List<PositionResponse> positions;

    private String getDesignationId() {
        return positions.get(0).getDepartmentDesignation().getDesignation().getId();
    }

    private String getDepartmentId() {
        return positions.get(0).getDepartmentDesignation().getDepartment().toString();
    }

    public Position toDomain() {
        return new Position(getDesignationId(), getDepartmentId());
    }
}

