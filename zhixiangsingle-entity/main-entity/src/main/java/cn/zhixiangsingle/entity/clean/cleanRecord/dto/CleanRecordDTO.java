package cn.zhixiangsingle.entity.clean.cleanRecord.dto;

import cn.zhixiangsingle.entity.base.common.dto.BaseDTO;
import lombok.Data;
import lombok.ToString;

/**
 * @author hhp
 * @description 清洗记录实体
 * @date 2019/11/5 14:05
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Data
@ToString
public class CleanRecordDTO extends BaseDTO {
    //ID
    private Integer id;
    //清洗浸泡类别
    private Integer cleanType;
    //操作人名称
    private String czrName;
    //操作人id
    private Integer czrId;
    //实际耗时
    private String sjTime;
    //规定耗时
    private String gdTime;
    //操作开始时间
    private String startTime;
    //操作结束时间
    private String endTime;
    //图像拍摄信息
    private String imgs;
    // 1.正在清洗   2.清洗结束  3.强制终止   5准备清洗
    private String status;
    //规定水位
    private String gdWater;
    //传感器编号
    private String cgqBh;
    //结束原因
    private String stopYy;
    //实时水位
    private String realWater;
}
