package cn.zhixiangsingle.controller.clean;

import cn.zhixiangsingle.entity.base.user.User;
import cn.zhixiangsingle.entity.clean.cleanRecord.dto.CleanRecordDTO;
import cn.zhixiangsingle.service.clean.CleanRecordService;
import cn.zhixiangsingle.service.site.SiteService;
import cn.zhixiangsingle.web.responsive.IStatusMessage;
import cn.zhixiangsingle.web.responsive.ResultBean;
import com.alibaba.dubbo.config.annotation.Reference;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author hhp
 * @description 清洗记录view
 * @date 2019/11/5 14:00
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
@RequestMapping("/cleanRecord")
public class CleanRecordController {
    private static final Logger logger = LoggerFactory
            .getLogger(CleanRecordController.class);
    @Reference(version = "1.0.0")
    private SiteService siteService;
    @Reference(version = "1.0.0")
    private CleanRecordService cleanRecordService;
    /**
     *@描述
     *@参数 []
     *@返回值 org.springframework.web.servlet.ModelAndView
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @RequestMapping(value = "/getListPage", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getListPage() {
        logger.debug("食材清洗信息列表！");
        ModelAndView mav = new ModelAndView("/qxjp/scqx/scqx");
        try {
            mav.addObject("msg", "ok");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("跳转食材清洗列表异常！", e);
        }
        return mav;
    }
    /**
     *@描述
     *@参数 [cleanRecordDTO]
     *@返回值 cn.zhixiangsingle.web.responsive.ResultBean
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @PostMapping(value = "/getCleanRecordList")
    @ResponseBody
    public ResultBean getCleanRecordList(CleanRecordDTO cleanRecordDTO) {
        logger.debug("获取AI识别列表！ cleanRecordDTO = "+cleanRecordDTO);
        ResultBean resultBean = new ResultBean();
        try {
            User existUser = (User) SecurityUtils.getSubject().getPrincipal();
            cleanRecordDTO.setUserId(existUser.getId());
            resultBean = cleanRecordService.findCleanRecordList(cleanRecordDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取食材清洗列表异常！", e);
            resultBean.setSuccess(false);
            resultBean.setResultCode(IStatusMessage.SystemStatus.ERROR.getCode());
            resultBean.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
        }
        return resultBean;
    }
}
