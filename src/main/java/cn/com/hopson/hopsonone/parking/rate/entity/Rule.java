package cn.com.hopson.hopsonone.parking.rate.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 停车计费规则
 * </p>
 *
 * @author luliang
 * @since 2020-06-30
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Rule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 市场id
     */
    private Integer marketId;

    /**
     * 免费停车时间（分钟）
     */
    private Integer freeTime;

    /**
     * 24小时最高收费
     */
    private BigDecimal dayCost;

    /**
     * 0:分时计费 1：分时分段计费
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
