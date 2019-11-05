package cn.zhixiangsingle.dao.clock;

import cn.zhixiangsingle.entity.clock.dto.ClockDTO;

import java.util.List;
import java.util.Map;

public interface ClockMapper {
    Integer findClockTotal(ClockDTO clockDTO);

    List<Map<String,Object>> findClockList(ClockDTO clockDTO);
}
