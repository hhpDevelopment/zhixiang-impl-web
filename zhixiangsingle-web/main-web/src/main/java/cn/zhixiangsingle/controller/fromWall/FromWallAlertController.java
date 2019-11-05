package cn.zhixiangsingle.controller.fromWall;

import cn.zhixiangsingle.entity.base.user.User;
import cn.zhixiangsingle.entity.fromWall.fromWallAlert.dto.FromWallAlertDTO;
import cn.zhixiangsingle.service.fromWall.FromWallAlertService;
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
 * @description 三里报警信息view
 * @date 2019/11/5 13:38
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
@RequestMapping("/fromWallAlert")
public class FromWallAlertController {
    private static final Logger logger = LoggerFactory
            .getLogger(FromWallAlertController.class);
    @Reference(version = "1.0.0")
    private SiteService siteService;
    @Reference(version = "1.0.0")
    private FromWallAlertService fromWallAlertService;
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
        logger.debug("三离警报信息列表！");
        ModelAndView mav = new ModelAndView("/hjjc/sljb/sljbxx");
        try {
            mav.addObject("msg", "ok");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("跳转三离警报列表异常！", e);
        }
        return mav;
    }
    /**
     *@描述
     *@参数 [fromWallAlertDTO]
     *@返回值 cn.zhixiangsingle.web.responsive.ResultBean
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @PostMapping(value = "/getFromWallAlertList")
    @ResponseBody
    public ResultBean getFromWallAlertList(FromWallAlertDTO fromWallAlertDTO) {
        logger.debug("获取三离报警列表！ fromWallAlertDTO = "+fromWallAlertDTO);
        ResultBean resultBean = new ResultBean();
        try {
            User existUser = (User) SecurityUtils.getSubject().getPrincipal();
            fromWallAlertDTO.setUserId(existUser.getId());
            resultBean = fromWallAlertService.findFromWallAlertList(fromWallAlertDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取三离报警列表异常！", e);
            resultBean.setSuccess(false);
            resultBean.setResultCode(IStatusMessage.SystemStatus.ERROR.getCode());
            resultBean.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
        }
        return resultBean;
    }
}
