package cn.zhixiangsingle.dao.fromWallAlert;

import cn.zhixiangsingle.entity.fromWall.fromWallAlert.dto.FromWallAlertDTO;

import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.zhixiangyun.net
 *
 * @version V1.0
 * @Title: zhixiangpingtai
 * @Package cn.zhixiangsingle.dao.fromWallAlert
 * @Description: ${todo}
 * @author: hhp
 * @date: 2019/10/29 16:47
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
public interface FromWallAlertMapper {
    Integer insertSelective(FromWallAlertDTO fromWallAlertDTO);

    Integer findFromWallAlertTotal(FromWallAlertDTO fromWallAlertDTO);

    List<Map<String,Object>> findFromWallAlertList(FromWallAlertDTO fromWallAlertDTO);
}
