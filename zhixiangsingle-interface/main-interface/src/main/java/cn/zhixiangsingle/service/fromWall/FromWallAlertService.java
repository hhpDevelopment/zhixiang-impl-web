package cn.zhixiangsingle.service.fromWall;

import cn.zhixiangsingle.entity.fromWall.fromWallAlert.dto.FromWallAlertDTO;
import cn.zhixiangsingle.web.responsive.ResultBean;

/**
 * @author hhp
 * @description ${description}
 * @date 2019/11/5
 */
public interface FromWallAlertService {
    ResultBean findFromWallAlertList(FromWallAlertDTO fromWallAlertDTO) throws Exception;
}
