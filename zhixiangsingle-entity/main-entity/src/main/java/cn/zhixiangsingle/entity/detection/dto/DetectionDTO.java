package cn.zhixiangsingle.entity.detection.dto;

import cn.zhixiangsingle.entity.base.common.dto.BaseDTO;
import lombok.Data;
import lombok.ToString;

/**
 * @author hhp
 * @description AI识别实体
 * @date 2019/11/5 9:35
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Data
@ToString
public class DetectionDTO extends BaseDTO {
    private Integer id;
    private String imageFile;
    private String withMask;
    private String withHat;
    private String withCloth;
    private String withMouse;
    private String kitchenId;
    private String cameraId;
    private String detectionTime;
    private String stut;
}
