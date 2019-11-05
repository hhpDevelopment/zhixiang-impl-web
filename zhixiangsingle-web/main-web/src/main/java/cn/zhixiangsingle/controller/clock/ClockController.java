package cn.zhixiangsingle.controller.clock;

import cn.zhixiangsingle.entity.base.IsEmptyUtils;
import cn.zhixiangsingle.entity.base.site.vo.SiteVO;
import cn.zhixiangsingle.entity.base.user.User;
import cn.zhixiangsingle.entity.clock.dto.ClockDTO;
import cn.zhixiangsingle.service.clock.ClockService;
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

import java.util.List;

/**
 * @author hhp
 * @description 考勤体温/晨检
 * @date 2019/11/5
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Controller
@RequestMapping("/clock")
public class ClockController {
    private static final Logger logger = LoggerFactory
            .getLogger(ClockController.class);
    @Reference(version = "1.0.0")
    private SiteService siteService;
    @Reference(version = "1.0.0")
    private ClockService clockService;
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
        logger.debug("跳转晨会信息列表！");
        ModelAndView mav = new ModelAndView("/rlzb/kqtg/kqtggl");
        try {
            mav.addObject("msg", "ok");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("跳转晨会列表异常！", e);
        }
        return mav;
    }
    /**
     *@描述
     *@参数 [clockDTO]
     *@返回值 cn.zhixiangsingle.web.responsive.ResultBean
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @PostMapping(value = "/getClockList")
    @ResponseBody
    public ResultBean getMorningMeetingList(ClockDTO clockDTO) {
        logger.debug("获取考勤体感列表！ clockDTO = "+clockDTO);
        ResultBean resultBean = new ResultBean();
        try {
            User existUser = (User) SecurityUtils.getSubject().getPrincipal();
            clockDTO.setUserId(existUser.getId());
            resultBean = clockService.findClockList(clockDTO);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取考勤体感列表异常！", e);
            resultBean.setSuccess(false);
            resultBean.setResultCode(IStatusMessage.SystemStatus.ERROR.getCode());
            resultBean.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
        }
        return resultBean;
    }
    /**
     *@描述
     *@参数 [clockDTO]
     *@返回值 cn.zhixiangsingle.web.responsive.ResultBean
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @RequestMapping(value = "/setClock", method = RequestMethod.POST)
    @ResponseBody
    public ResultBean setClock(ClockDTO clockDTO) {
        logger.debug("添加或修改考勤体感信息 --clockDTO-" + clockDTO);
        ResultBean resultBean = new ResultBean();
        try {
            User existUser = (User) SecurityUtils.getSubject().getPrincipal();
            if (!IsEmptyUtils.isEmpty(clockDTO)) {
                clockDTO.setUserId(existUser.getId());
                if(!IsEmptyUtils.isEmpty(clockDTO.getId())){
                    resultBean = clockService.updateClock(clockDTO);
                }else{
                    resultBean = siteService.getUserSites(existUser.getId());
                    if(!IsEmptyUtils.isEmpty(resultBean.getResult())){
                        List<SiteVO> siteVOList = (List<SiteVO>)resultBean.getResult();
                        if(siteVOList.size()>1){
                            if(!IsEmptyUtils.isEmpty(clockDTO.getSdIds())){
                                resultBean = clockService.addClock(clockDTO);
                            }else{
                                resultBean.setSuccess(false);
                                resultBean.setResultCode(IStatusMessage.SystemStatus.SITE_ID_LOSE.getCode());
                                resultBean.setMsg(IStatusMessage.SystemStatus.SITE_ID_LOSE.getMessage());
                            }
                        }else{
                            clockDTO.setSdId(existUser.getSdId());
                            clockDTO.setSdIds("");
                            resultBean = clockService.addClock(clockDTO);
                        }
                    }else{
                        resultBean.setSuccess(false);
                        resultBean.setResultCode(IStatusMessage.SystemStatus.SITE_ROLE_IS_EMPTY.getCode());
                        resultBean.setMsg(IStatusMessage.SystemStatus.SITE_ROLE_IS_EMPTY.getMessage());
                    }
                }
            }else{
                resultBean.setSuccess(false);
                resultBean.setResultCode(IStatusMessage.SystemStatus.PARAMETERS_LOSE.getCode());
                resultBean.setMsg(IStatusMessage.SystemStatus.PARAMETERS_LOSE.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加或修改考勤体感信息异常！", e);
            resultBean.setSuccess(false);
            resultBean.setResultCode(IStatusMessage.SystemStatus.ERROR.getCode());
            resultBean.setMsg(IStatusMessage.SystemStatus.ERROR.getMessage());
        }
        return resultBean;
    }
}
