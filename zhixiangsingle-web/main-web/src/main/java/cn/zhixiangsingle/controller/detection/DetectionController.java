package cn.zhixiangsingle.controller.detection;

import cn.zhixiangsingle.entity.base.user.User;
import cn.zhixiangsingle.entity.detection.dto.DetectionDTO;
import cn.zhixiangsingle.service.detection.DetectionService;
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
 * @description ai识别
 * @date 2019/11/5 9:20
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
@RequestMapping("/detection")
public class DetectionController {
    private static final Logger logger = LoggerFactory
            .getLogger(DetectionController.class);
    @Reference(version = "1.0.0")
    private SiteService siteService;
    @Reference(version = "1.0.0")
    private DetectionService detectionService;
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
        logger.debug("ai识别信息列表！");
        ModelAndView mav = new ModelAndView("/aisb/aisb/aisb");
        try {
            mav.addObject("msg", "ok");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("跳转ai识别列表异常！", e);
        }
        return mav;
    }
    /**
     *@描述
     *@参数 [detectionDTO]
     *@返回值 cn.zhixiangsingle.web.responsive.ResultBean
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @PostMapping(value = "/getDetectionList")
    @ResponseBody
    public ResultBean getDetectionList(DetectionDTO detectionDTO) {
        logger.debug("获取AI识别列表！ detectionDTO = "+detectionDTO);
        ResultBean resultBean = new ResultBean();
        try {
            User existUser = (User) SecurityUtils.getSubject().getPrincipal();
            detectionDTO.setUserId(existUser.getId());
            resultBean = detectionService.findDetectionList(detectionDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取AI识别列表异常！", e);
            resultBean.setSuccess(false);
            resultBean.setResultCode(IStatusMessage.SystemStatus.ERROR.getCode());
            resultBean.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
        }
        return resultBean;
    }
}
