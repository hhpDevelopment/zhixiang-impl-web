package cn.zhixiangsingle.impl.clock;

import cn.zhixiangsingle.config.DynamicDataSourceContextHolder;
import cn.zhixiangsingle.dao.clock.ClockMapper;
import cn.zhixiangsingle.dao.site.SiteMapper;
import cn.zhixiangsingle.entity.base.IsEmptyUtils;
import cn.zhixiangsingle.entity.base.site.SiteData;
import cn.zhixiangsingle.entity.base.site.vo.SiteVO;
import cn.zhixiangsingle.entity.clock.dto.ClockDTO;
import cn.zhixiangsingle.pagination.MyStartEndUtil;
import cn.zhixiangsingle.service.clock.ClockService;
import cn.zhixiangsingle.web.responsive.IStatusMessage;
import cn.zhixiangsingle.web.responsive.ResultBean;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * All rights Reserved, Designed By www.zhixiangyun.net
 *
 * @version V1.0
 * @Title: zhixiangpingtai
 * @Package cn.zhixiangsingle.impl.classification
 * @Description: ${todo}
 * @author: hhp
 * @date: 2019/11/05 18:13
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Service(version = "1.0.0", interfaceClass = ClockService.class)
public class ClockServiceImpl implements ClockService {
    private static final Logger logger = LoggerFactory
            .getLogger(ClockServiceImpl.class);
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private ClockMapper clockMapper;
    /**
     *@描述
     *@参数 [clockDTO]
     *@返回值 cn.zhixiangsingle.web.responsive.ResultBean
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @Override
    public ResultBean findClockList(ClockDTO clockDTO) throws Exception {
        ResultBean resultBean = new ResultBean();
        if(IsEmptyUtils.isEmpty(clockDTO.getPage())){
            clockDTO.setPage(1);
        }
        if (IsEmptyUtils.isEmpty(clockDTO.getLimit())){
            clockDTO.setLimit(10);
        }
        Integer page = clockDTO.getPage();
        Integer limit = clockDTO.getLimit();
        StringBuffer stringBufferJson = new StringBuffer();
        InputStream stream = getClass().getClassLoader().getResourceAsStream("json/siteData.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            stringBufferJson.append(line);
        }
        JSONObject jobj = JSON.parseObject(stringBufferJson.toString());

        List<Map<String,Object>> mainLPLs = Lists.newArrayList();
        Integer total = 0;
        int startNum = (page-1)*limit;
        int endNum = startNum+limit;
        ArrayList<Integer> totalAll = Lists.newArrayList();
        clockDTO.setId(-1);
        clockDTO.setClockingTime("c");
        if(IsEmptyUtils.isEmpty(clockDTO.getClasses())){
            clockDTO.setClasses("c");
        }
        if(IsEmptyUtils.isEmpty(clockDTO.getClockName())){
            clockDTO.setClockName("c");
        }
        if(IsEmptyUtils.isEmpty(clockDTO.getOnOrDownClass())){
            clockDTO.setOnOrDownClass("o");
        }
        if(IsEmptyUtils.isEmpty(clockDTO.getClockStatus())){
            clockDTO.setClockStatus("c");
        }
        clockDTO.setSomTemp("s");
        clockDTO.setRecomTemper("r");
        clockDTO.setSymptom("s");
        clockDTO.setClockImg("c");
        if(!IsEmptyUtils.isEmpty(clockDTO.getSdIds())){
            String[] sdIds = clockDTO.getSdIds().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            for(String sdId:sdIds){
                clockDTO.setSdId(Integer.parseInt(sdId));
                String dataSourceName = "";
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdId))){
                    siteData = JSON.parseObject(jobj.get(sdId).toString(),SiteData.class);
                }
                clockDTO.setPicturePrefix(siteData.getPicturePrefix());
                if(!IsEmptyUtils.isEmpty(siteData)){
                    dataSourceName = siteData.getDateSourceName();
                }
                if(!IsEmptyUtils.isEmpty(dataSourceName)){
                    if(IsEmptyUtils.isEmpty(stringBuffer)){
                        stringBuffer.append(sdId);
                    }else{
                        stringBuffer.append(","+sdId);
                    }
                    logger.info("--"+dataSourceName+"--");
                    DynamicDataSourceContextHolder.setDataSourceType(dataSourceName);
                    Integer totalStatus = clockMapper.findClockTotal(clockDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                clockDTO.setSdId(Integer.parseInt(sourceNames[i]));
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                clockDTO.setPicturePrefix(siteData.getPicturePrefix());
                logger.info("--"+siteData.getDateSourceName()+"--");
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                clockDTO.setStart(startEndNums.get(i).get(0));
                clockDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                clockDTO.setSdId(Integer.parseInt(sourceNames[i]));
                clockDTO.setSiteName(siteData.getName());
                clockDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = clockMapper.findClockList(clockDTO);
                mainLPLs.addAll(mainLPList);
            }
            DynamicDataSourceContextHolder.setDataSourceType(null);
        }else{
            List<SiteVO> userSites = siteMapper.findUserSites(clockDTO.getUserId());
            StringBuffer stringBuffer = new StringBuffer();
            for(SiteVO siteVO:userSites){
                clockDTO.setSdId(siteVO.getSdId());
                String dataSourceName = "";
                String sdIdStr = siteVO.getSdId().toString();
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdIdStr))){
                    siteData = JSON.parseObject(jobj.get(sdIdStr).toString(),SiteData.class);
                }
                clockDTO.setPicturePrefix(siteData.getPicturePrefix());
                if(!IsEmptyUtils.isEmpty(siteData)){
                    dataSourceName = siteData.getDateSourceName();
                }
                if(!IsEmptyUtils.isEmpty(dataSourceName)){
                    if(IsEmptyUtils.isEmpty(stringBuffer)){
                        stringBuffer.append(siteVO.getSdId());
                    }else{
                        stringBuffer.append(","+siteVO.getSdId());
                    }
                    logger.info("null sdIds...--"+dataSourceName+"----");
                    DynamicDataSourceContextHolder.setDataSourceType(dataSourceName);
                    Integer totalStatus = clockMapper.findClockTotal(clockDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                logger.info("--"+siteData.getDateSourceName()+"--");
                clockDTO.setPicturePrefix(siteData.getPicturePrefix());
                clockDTO.setSdId(Integer.parseInt(sourceNames[i]));
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                clockDTO.setStart(startEndNums.get(i).get(0));
                clockDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                clockDTO.setSdId(Integer.parseInt(sourceNames[i]));
                clockDTO.setSiteName(siteData.getName());
                clockDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = clockMapper.findClockList(clockDTO);
                mainLPLs.addAll(mainLPList);
            }
            DynamicDataSourceContextHolder.setDataSourceType(null);
        }
        resultBean.setSuccess(true);
        resultBean.setResult(mainLPLs);
        resultBean.setTotal(total);
        resultBean.setResultCode(IStatusMessage.SystemStatus.SUCCESS.getCode());
        resultBean.setMsg(IStatusMessage.SystemStatus.SUCCESS.getMessage());
        return resultBean;
    }

    @Override
    public ResultBean updateClock(ClockDTO clockDTO) throws Exception {
        return null;
    }

    @Override
    public ResultBean addClock(ClockDTO clockDTO) throws Exception {
        return null;
    }
}
