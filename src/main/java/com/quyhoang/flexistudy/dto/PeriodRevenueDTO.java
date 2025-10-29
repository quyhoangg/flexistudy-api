package com.quyhoang.flexistudy.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PeriodRevenueDTO {
    String period;
    Long revenue;

    public PeriodRevenueDTO(String period, Number revenue) {
        this.period = period;
        this.revenue = revenue != null ? revenue.longValue() : 0L;
    }
}
