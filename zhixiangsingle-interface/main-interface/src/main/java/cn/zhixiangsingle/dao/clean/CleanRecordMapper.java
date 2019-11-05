package cn.zhixiangsingle.dao.clean;

import cn.zhixiangsingle.entity.clean.cleanRecord.dto.CleanRecordDTO;

import java.util.List;
import java.util.Map;

/**
 * @author hhp
 * @description ${description}
 * @date 2019/11/5
 */
public interface CleanRecordMapper {
    Integer findCleanRecordTotal(CleanRecordDTO cleanRecordDTO);

    List<Map<String,Object>> findCleanRecordList(CleanRecordDTO cleanRecordDTO);
}
