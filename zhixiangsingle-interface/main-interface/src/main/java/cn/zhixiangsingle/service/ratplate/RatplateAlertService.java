package cn.zhixiangsingle.service.ratplate;

import cn.zhixiangsingle.entity.ratplate.ratplateAlert.dto.RatplateAlertDTO;
import cn.zhixiangsingle.web.responsive.ResultBean;

/**
 * @author hhp
 * @description ${description}
 * @date 2019/11/5
 */
public interface RatplateAlertService {
    ResultBean findRatplateAlertList(RatplateAlertDTO ratplateAlertDTO) throws Exception;
}
