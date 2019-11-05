package cn.zhixiangsingle.impl.detection;

import cn.zhixiangsingle.config.DynamicDataSourceContextHolder;
import cn.zhixiangsingle.dao.detection.DetectionMapper;
import cn.zhixiangsingle.dao.site.SiteMapper;
import cn.zhixiangsingle.entity.base.IsEmptyUtils;
import cn.zhixiangsingle.entity.base.site.SiteData;
import cn.zhixiangsingle.entity.base.site.vo.SiteVO;
import cn.zhixiangsingle.entity.detection.dto.DetectionDTO;
import cn.zhixiangsingle.pagination.MyStartEndUtil;
import cn.zhixiangsingle.service.detection.DetectionService;
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
 * @description AI识别业务逻辑层
 * @date 2019/11/5 9:40
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Service(version = "1.0.0", interfaceClass = DetectionService.class)
public class DetectionServiceImpl implements DetectionService {
    private static final Logger logger = LoggerFactory
            .getLogger(DetectionServiceImpl.class);
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private DetectionMapper detectionMapper;

    @Override
    public ResultBean findDetectionList(DetectionDTO detectionDTO) throws Exception {
        ResultBean resultBean = new ResultBean();
        if(IsEmptyUtils.isEmpty(detectionDTO.getPage())){
            detectionDTO.setPage(1);
        }
        if (IsEmptyUtils.isEmpty(detectionDTO.getLimit())){
            detectionDTO.setLimit(10);
        }
        Integer page = detectionDTO.getPage();
        Integer limit = detectionDTO.getLimit();
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
        detectionDTO.setId(-1);
        detectionDTO.setDetectionTime("d");
        if(IsEmptyUtils.isEmpty(detectionDTO.getWithMask())){
            detectionDTO.setWithMask("w");
        }
        if(IsEmptyUtils.isEmpty(detectionDTO.getWithHat())){
            detectionDTO.setWithHat("w");
        }
        if(IsEmptyUtils.isEmpty(detectionDTO.getWithCloth())){
            detectionDTO.setWithCloth("w");
        }
        if(IsEmptyUtils.isEmpty(detectionDTO.getWithMouse())){
            detectionDTO.setWithMouse("w");
        }
        if(IsEmptyUtils.isEmpty(detectionDTO.getStut())){
            detectionDTO.setStut("s");
        }
        detectionDTO.setKitchenId("k");
        detectionDTO.setCameraId("c");
        detectionDTO.setImageFile("i");
        if(!IsEmptyUtils.isEmpty(detectionDTO.getSdIds())){
            String[] sdIds = detectionDTO.getSdIds().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            for(String sdId:sdIds){
                detectionDTO.setSdId(Integer.parseInt(sdId));
                String dataSourceName = "";
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdId))){
                    siteData = JSON.parseObject(jobj.get(sdId).toString(),SiteData.class);
                }
                detectionDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = detectionMapper.findDetectionTotal(detectionDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                detectionDTO.setSdId(Integer.parseInt(sourceNames[i]));
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                detectionDTO.setPicturePrefix(siteData.getPicturePrefix());
                logger.info("--"+siteData.getDateSourceName()+"--");
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                detectionDTO.setStart(startEndNums.get(i).get(0));
                detectionDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                detectionDTO.setSdId(Integer.parseInt(sourceNames[i]));
                detectionDTO.setSiteName(siteData.getName());
                detectionDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = detectionMapper.findDetectionList(detectionDTO);
                mainLPLs.addAll(mainLPList);
            }
            DynamicDataSourceContextHolder.setDataSourceType(null);
        }else{
            List<SiteVO> userSites = siteMapper.findUserSites(detectionDTO.getUserId());
            StringBuffer stringBuffer = new StringBuffer();
            for(SiteVO siteVO:userSites){
                detectionDTO.setSdId(siteVO.getSdId());
                String dataSourceName = "";
                String sdIdStr = siteVO.getSdId().toString();
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdIdStr))){
                    siteData = JSON.parseObject(jobj.get(sdIdStr).toString(),SiteData.class);
                }
                detectionDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = detectionMapper.findDetectionTotal(detectionDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                logger.info("--"+siteData.getDateSourceName()+"--");
                detectionDTO.setPicturePrefix(siteData.getPicturePrefix());
                detectionDTO.setSdId(Integer.parseInt(sourceNames[i]));
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                detectionDTO.setStart(startEndNums.get(i).get(0));
                detectionDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                detectionDTO.setSdId(Integer.parseInt(sourceNames[i]));
                detectionDTO.setSiteName(siteData.getName());
                detectionDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = detectionMapper.findDetectionList(detectionDTO);
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
