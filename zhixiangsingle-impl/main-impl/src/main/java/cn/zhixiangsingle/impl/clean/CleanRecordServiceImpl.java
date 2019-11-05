package cn.zhixiangsingle.impl.clean;

import cn.zhixiangsingle.config.DynamicDataSourceContextHolder;
import cn.zhixiangsingle.dao.clean.CleanRecordMapper;
import cn.zhixiangsingle.dao.site.SiteMapper;
import cn.zhixiangsingle.entity.base.IsEmptyUtils;
import cn.zhixiangsingle.entity.base.site.SiteData;
import cn.zhixiangsingle.entity.base.site.vo.SiteVO;
import cn.zhixiangsingle.entity.clean.cleanRecord.dto.CleanRecordDTO;
import cn.zhixiangsingle.pagination.MyStartEndUtil;
import cn.zhixiangsingle.service.clean.CleanRecordService;
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
 * @description 清洗记录业务逻辑
 * @date 2019/11/5 14:03
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Service(version = "1.0.0", interfaceClass = CleanRecordService.class)
public class CleanRecordServiceImpl implements CleanRecordService {
    private static final Logger logger = LoggerFactory
            .getLogger(CleanRecordServiceImpl.class);
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private CleanRecordMapper cleanRecordMapper;

    @Override
    public ResultBean findCleanRecordList(CleanRecordDTO cleanRecordDTO) throws Exception {
        ResultBean resultBean = new ResultBean();
        if(IsEmptyUtils.isEmpty(cleanRecordDTO.getPage())){
            cleanRecordDTO.setPage(1);
        }
        if (IsEmptyUtils.isEmpty(cleanRecordDTO.getLimit())){
            cleanRecordDTO.setLimit(10);
        }
        Integer page = cleanRecordDTO.getPage();
        Integer limit = cleanRecordDTO.getLimit();
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
        cleanRecordDTO.setId(-1);
        cleanRecordDTO.setStartTime("s");
        cleanRecordDTO.setCleanType(-1);
        if(IsEmptyUtils.isEmpty(cleanRecordDTO.getCzrName())){
            cleanRecordDTO.setCzrName("c");
        }
        if(IsEmptyUtils.isEmpty(cleanRecordDTO.getStatus())){
            cleanRecordDTO.setStatus("s");
        }
        cleanRecordDTO.setGdWater("g");
        cleanRecordDTO.setCgqBh("c");
        cleanRecordDTO.setStopYy("s");
        cleanRecordDTO.setGdTime("g");
        cleanRecordDTO.setSjTime("s");
        cleanRecordDTO.setImgs("i");
        if(!IsEmptyUtils.isEmpty(cleanRecordDTO.getSdIds())){
            String[] sdIds = cleanRecordDTO.getSdIds().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            for(String sdId:sdIds){
                cleanRecordDTO.setSdId(Integer.parseInt(sdId));
                String dataSourceName = "";
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdId))){
                    siteData = JSON.parseObject(jobj.get(sdId).toString(),SiteData.class);
                }
                cleanRecordDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = cleanRecordMapper.findCleanRecordTotal(cleanRecordDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                cleanRecordDTO.setSdId(Integer.parseInt(sourceNames[i]));
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                cleanRecordDTO.setPicturePrefix(siteData.getPicturePrefix());
                logger.info("--"+siteData.getDateSourceName()+"--");
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                cleanRecordDTO.setStart(startEndNums.get(i).get(0));
                cleanRecordDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                cleanRecordDTO.setSdId(Integer.parseInt(sourceNames[i]));
                cleanRecordDTO.setSiteName(siteData.getName());
                cleanRecordDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = cleanRecordMapper.findCleanRecordList(cleanRecordDTO);
                mainLPLs.addAll(mainLPList);
            }
            DynamicDataSourceContextHolder.setDataSourceType(null);
        }else{
            List<SiteVO> userSites = siteMapper.findUserSites(cleanRecordDTO.getUserId());
            StringBuffer stringBuffer = new StringBuffer();
            for(SiteVO siteVO:userSites){
                cleanRecordDTO.setSdId(siteVO.getSdId());
                String dataSourceName = "";
                String sdIdStr = siteVO.getSdId().toString();
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdIdStr))){
                    siteData = JSON.parseObject(jobj.get(sdIdStr).toString(),SiteData.class);
                }
                cleanRecordDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = cleanRecordMapper.findCleanRecordTotal(cleanRecordDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                logger.info("--"+siteData.getDateSourceName()+"--");
                cleanRecordDTO.setPicturePrefix(siteData.getPicturePrefix());
                cleanRecordDTO.setSdId(Integer.parseInt(sourceNames[i]));
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                cleanRecordDTO.setStart(startEndNums.get(i).get(0));
                cleanRecordDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                cleanRecordDTO.setSdId(Integer.parseInt(sourceNames[i]));
                cleanRecordDTO.setSiteName(siteData.getName());
                cleanRecordDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = cleanRecordMapper.findCleanRecordList(cleanRecordDTO);
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
