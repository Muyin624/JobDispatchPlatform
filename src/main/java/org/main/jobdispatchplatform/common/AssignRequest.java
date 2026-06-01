package org.main.jobdispatchplatform.common;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AssignRequest {
    @Min(value = 1, message = "订单ID不能为空")
    private int orderId;

    @Min(value = 1, message = "摄影师ID不能为空")
    private int photographerId;
}
