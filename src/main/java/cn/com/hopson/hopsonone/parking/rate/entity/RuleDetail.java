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
 * 停车计费规则详情
 * </p>
 *
 * @author luliang
 * @since 2020-06-30
 */
@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RuleDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 市场id
     */
    private Integer marketId;

    /**
     * 0：分钟 1：小时
     */
    private Integer unitType;

    /**
     * 时段
     */
    private Integer timeInterval;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 分时分段,时段开始
     */
    private Integer intervalStart;

    /**
     * 分时分段,时段结束
     */
    private Integer intervalEnd;

    /**
     * 分时分段,时段最高收费
     */
    private BigDecimal intervalCost;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
