package cn.zhixiangsingle.impl.ratplate;

import cn.zhixiangsingle.config.DynamicDataSourceContextHolder;
import cn.zhixiangsingle.dao.ratplateAlert.RatplateAlertMapper;
import cn.zhixiangsingle.dao.site.SiteMapper;
import cn.zhixiangsingle.entity.base.IsEmptyUtils;
import cn.zhixiangsingle.entity.base.site.SiteData;
import cn.zhixiangsingle.entity.base.site.vo.SiteVO;
import cn.zhixiangsingle.entity.ratplate.ratplateAlert.dto.RatplateAlertDTO;
import cn.zhixiangsingle.pagination.MyStartEndUtil;
import cn.zhixiangsingle.service.ratplate.RatplateAlertService;
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
 * @author hhp
 * @description 挡鼠板异常信息业务层
 * @date 2019/11/5 12:53
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Service(version = "1.0.0", interfaceClass = RatplateAlertService.class)
public class RatplateAlertServiceImpl implements RatplateAlertService {
    private static final Logger logger = LoggerFactory
            .getLogger(RatplateAlertServiceImpl.class);
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private RatplateAlertMapper ratplateAlertMapper;

    @Override
    public ResultBean findRatplateAlertList(RatplateAlertDTO ratplateAlertDTO) throws Exception {
        ResultBean resultBean = new ResultBean();
        if(IsEmptyUtils.isEmpty(ratplateAlertDTO.getPage())){
            ratplateAlertDTO.setPage(1);
        }
        if (IsEmptyUtils.isEmpty(ratplateAlertDTO.getLimit())){
            ratplateAlertDTO.setLimit(10);
        }
        Integer page = ratplateAlertDTO.getPage();
        Integer limit = ratplateAlertDTO.getLimit();
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
        ratplateAlertDTO.setId(-1);
        ratplateAlertDTO.setRatplateStartTime("r");
        ratplateAlertDTO.setRatplateSensor("r");
        if(IsEmptyUtils.isEmpty(ratplateAlertDTO.getRatplateArea())){
            ratplateAlertDTO.setRatplateArea("r");
        }
        if(IsEmptyUtils.isEmpty(ratplateAlertDTO.getRatplateStatus())){
            ratplateAlertDTO.setRatplateStatus("r");
        }
        ratplateAlertDTO.setRatplateRultionsTime("r");
        ratplateAlertDTO.setRatplateDescription("r");
        if(!IsEmptyUtils.isEmpty(ratplateAlertDTO.getSdIds())){
            String[] sdIds = ratplateAlertDTO.getSdIds().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            for(String sdId:sdIds){
                ratplateAlertDTO.setSdId(Integer.parseInt(sdId));
                String dataSourceName = "";
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdId))){
                    siteData = JSON.parseObject(jobj.get(sdId).toString(),SiteData.class);
                }
                ratplateAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = ratplateAlertMapper.findRatplateAlertTotal(ratplateAlertDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                ratplateAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                ratplateAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
                logger.info("--"+siteData.getDateSourceName()+"--");
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                ratplateAlertDTO.setStart(startEndNums.get(i).get(0));
                ratplateAlertDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                ratplateAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                ratplateAlertDTO.setSiteName(siteData.getName());
                ratplateAlertDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = ratplateAlertMapper.findRatplateAlertList(ratplateAlertDTO);
                mainLPLs.addAll(mainLPList);
            }
            DynamicDataSourceContextHolder.setDataSourceType(null);
        }else{
            List<SiteVO> userSites = siteMapper.findUserSites(ratplateAlertDTO.getUserId());
            StringBuffer stringBuffer = new StringBuffer();
            for(SiteVO siteVO:userSites){
                ratplateAlertDTO.setSdId(siteVO.getSdId());
                String dataSourceName = "";
                String sdIdStr = siteVO.getSdId().toString();
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdIdStr))){
                    siteData = JSON.parseObject(jobj.get(sdIdStr).toString(),SiteData.class);
                }
                ratplateAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = ratplateAlertMapper.findRatplateAlertTotal(ratplateAlertDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                logger.info("--"+siteData.getDateSourceName()+"--");
                ratplateAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
                ratplateAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                ratplateAlertDTO.setStart(startEndNums.get(i).get(0));
                ratplateAlertDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                ratplateAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                ratplateAlertDTO.setSiteName(siteData.getName());
                ratplateAlertDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = ratplateAlertMapper.findRatplateAlertList(ratplateAlertDTO);
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
}
