package cn.zhixiangsingle.service.detection;

import cn.zhixiangsingle.entity.detection.dto.DetectionDTO;
import cn.zhixiangsingle.web.responsive.ResultBean;

/**
 * @author hhp
 * @description ${description}
 * @date 2019/11/5
 */
public interface DetectionService {
    ResultBean findDetectionList(DetectionDTO detectionDTO) throws Exception;
}
