// UpgradePlanResponse.java
package com.quyhoang.flexistudy.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpgradePlanResponse {
    String id;
    String name;
    Double price;
    String duration;
    String description;
}
