package cn.zhixiangsingle.entity.clock.dto;

import cn.zhixiangsingle.entity.base.common.dto.BaseDTO;
import lombok.Data;
import lombok.ToString;

/**
 * All rights Reserved, Designed By www.zhixiangyun.net
 *
 * @version V1.0
 * @Title: zhixiangpingtai
 * @Package cn.zhixiangsingle.entity.classification.dto
 * @Description: ${todo}
 * @author: hhp
 * @date: 2019/11/04 16:14
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Data
@ToString
public class ClockDTO extends BaseDTO {
    private Integer id;
    private String classes;
    private Integer mainAccountId;
    private String clockingTime;
    private String clockImg;
    private String clockName;
    private String clockStatus;
    private String symptom;
    private Integer sdId;
    private String onOrDownClass;
    private String somTemp;
    private String recomTemper;
}
