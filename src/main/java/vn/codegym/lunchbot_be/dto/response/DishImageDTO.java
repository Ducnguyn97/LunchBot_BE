package vn.codegym.lunchbot_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishImageDTO {
    private Long id;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isPrimary;
}
