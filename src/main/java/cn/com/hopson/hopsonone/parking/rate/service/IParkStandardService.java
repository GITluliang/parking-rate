package cn.com.hopson.hopsonone.parking.rate.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 停车计费规则 服务实现类
 * </p>
 *
 * @author luliang
 * @since 2020-06-30
 */
@Service
public interface IParkStandardService {

    /**
     * 根据入场时间与固定时长进行计费
     *
     * @param marketId 商场id
     * @param in       入场时间
     * @param parkTime 固定时长，整数（小时）
     * @param carNo    车牌号
     * @return
     */
    BigDecimal fee(Integer marketId, String in, Integer parkTime);

}
