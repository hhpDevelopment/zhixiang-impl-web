package cn.zhixiangsingle.dao.detection;

import cn.zhixiangsingle.entity.detection.dto.DetectionDTO;

import java.util.List;
import java.util.Map;

/**
 * @author hhp
 * @description ${description}
 * @date 2019/11/5
 */
public interface DetectionMapper {
    Integer findDetectionTotal(DetectionDTO detectionDTO);

    List<Map<String,Object>> findDetectionList(DetectionDTO detectionDTO);
}
