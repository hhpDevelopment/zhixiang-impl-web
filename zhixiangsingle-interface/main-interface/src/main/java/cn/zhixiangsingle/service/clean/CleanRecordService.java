package cn.zhixiangsingle.service.clean;

import cn.zhixiangsingle.entity.clean.cleanRecord.dto.CleanRecordDTO;
import cn.zhixiangsingle.web.responsive.ResultBean;

/**
 * @author hhp
 * @description ${description}
 * @date 2019/11/5
 */
public interface CleanRecordService {
    ResultBean findCleanRecordList(CleanRecordDTO cleanRecordDTO) throws Exception;
}
