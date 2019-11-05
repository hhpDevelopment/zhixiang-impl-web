package cn.zhixiangsingle.service.clock;

import cn.zhixiangsingle.entity.clock.dto.ClockDTO;
import cn.zhixiangsingle.web.responsive.ResultBean;

public interface ClockService {
    ResultBean findClockList(ClockDTO clockDTO) throws Exception;

    ResultBean updateClock(ClockDTO clockDTO) throws Exception;

    ResultBean addClock(ClockDTO clockDTO)  throws Exception;
}
