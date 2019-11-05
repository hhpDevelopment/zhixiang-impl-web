package cn.zhixiangsingle.impl.fromWall;

import cn.zhixiangsingle.config.DynamicDataSourceContextHolder;
import cn.zhixiangsingle.dao.fromWallAlert.FromWallAlertMapper;
import cn.zhixiangsingle.dao.site.SiteMapper;
import cn.zhixiangsingle.entity.base.IsEmptyUtils;
import cn.zhixiangsingle.entity.base.site.SiteData;
import cn.zhixiangsingle.entity.base.site.vo.SiteVO;
import cn.zhixiangsingle.entity.fromWall.fromWallAlert.dto.FromWallAlertDTO;
import cn.zhixiangsingle.pagination.MyStartEndUtil;
import cn.zhixiangsingle.service.fromWall.FromWallAlertService;
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
 * @description 三离报警信息业务层
 * @date 2019/11/5 13:41
 * @Copyright: 2019 www.zhixiangyun.net Inc. All rights reserved.
 * 注意：本内容仅限于浙江智飨科技内部传阅，禁止外泄以及用于其他的商业目
 */
@Service(version = "1.0.0", interfaceClass = FromWallAlertService.class)
public class FromWallAlertServiceImpl implements FromWallAlertService {
    private static final Logger logger = LoggerFactory
            .getLogger(FromWallAlertServiceImpl.class);
    @Autowired
    private SiteMapper siteMapper;
    @Autowired
    private FromWallAlertMapper fromWallAlertMapper;
    /**
     *@描述
     *@参数 [fromWallAlertDTO]
     *@返回值 cn.zhixiangsingle.web.responsive.ResultBean
     *@创建人 hhp
     *@创建时间 2019/11/5
     *@修改人和其它信息
     */
    @Override
    public ResultBean findFromWallAlertList(FromWallAlertDTO fromWallAlertDTO) throws Exception {
        ResultBean resultBean = new ResultBean();
        if(IsEmptyUtils.isEmpty(fromWallAlertDTO.getPage())){
            fromWallAlertDTO.setPage(1);
        }
        if (IsEmptyUtils.isEmpty(fromWallAlertDTO.getLimit())){
            fromWallAlertDTO.setLimit(10);
        }
        Integer page = fromWallAlertDTO.getPage();
        Integer limit = fromWallAlertDTO.getLimit();
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
        fromWallAlertDTO.setId(-1);
        fromWallAlertDTO.setRatplateStartTime("r");
        fromWallAlertDTO.setFromwallSensor("f");
        if(IsEmptyUtils.isEmpty(fromWallAlertDTO.getFromwallArea())){
            fromWallAlertDTO.setFromwallArea("f");
        }
        if(IsEmptyUtils.isEmpty(fromWallAlertDTO.getRatplateStatus())){
            fromWallAlertDTO.setRatplateStatus("r");
        }
        fromWallAlertDTO.setRatplateRultionsTime("r");
        fromWallAlertDTO.setRatplateDescription("r");
        if(!IsEmptyUtils.isEmpty(fromWallAlertDTO.getSdIds())){
            String[] sdIds = fromWallAlertDTO.getSdIds().split(",");
            StringBuffer stringBuffer = new StringBuffer();
            for(String sdId:sdIds){
                fromWallAlertDTO.setSdId(Integer.parseInt(sdId));
                String dataSourceName = "";
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdId))){
                    siteData = JSON.parseObject(jobj.get(sdId).toString(),SiteData.class);
                }
                fromWallAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = fromWallAlertMapper.findFromWallAlertTotal(fromWallAlertDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                fromWallAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                fromWallAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
                logger.info("--"+siteData.getDateSourceName()+"--");
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                fromWallAlertDTO.setStart(startEndNums.get(i).get(0));
                fromWallAlertDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                fromWallAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                fromWallAlertDTO.setSiteName(siteData.getName());
                fromWallAlertDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = fromWallAlertMapper.findFromWallAlertList(fromWallAlertDTO);
                mainLPLs.addAll(mainLPList);
            }
            DynamicDataSourceContextHolder.setDataSourceType(null);
        }else{
            List<SiteVO> userSites = siteMapper.findUserSites(fromWallAlertDTO.getUserId());
            StringBuffer stringBuffer = new StringBuffer();
            for(SiteVO siteVO:userSites){
                fromWallAlertDTO.setSdId(siteVO.getSdId());
                String dataSourceName = "";
                String sdIdStr = siteVO.getSdId().toString();
                SiteData siteData = null;
                if(!IsEmptyUtils.isEmpty(jobj.get(sdIdStr))){
                    siteData = JSON.parseObject(jobj.get(sdIdStr).toString(),SiteData.class);
                }
                fromWallAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
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
                    Integer totalStatus = fromWallAlertMapper.findFromWallAlertTotal(fromWallAlertDTO);
                    total = total + totalStatus;
                    totalAll.add(totalStatus);
                }
            }
            ArrayList<ArrayList<Integer>> startEndNums = MyStartEndUtil.getStartEndArray(totalAll,startNum,endNum);
            String[] sourceNames = stringBuffer.toString().split(",");
            for(int i=0;i<startEndNums.size();i++){
                SiteData siteData = JSON.parseObject(jobj.get(sourceNames[i]).toString(),SiteData.class);
                logger.info("--"+siteData.getDateSourceName()+"--");
                fromWallAlertDTO.setPicturePrefix(siteData.getPicturePrefix());
                fromWallAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                DynamicDataSourceContextHolder.setDataSourceType(siteData.getDateSourceName());
                fromWallAlertDTO.setStart(startEndNums.get(i).get(0));
                fromWallAlertDTO.setEnd(startEndNums.get(i).get(1)-startEndNums.get(i).get(0));
                fromWallAlertDTO.setSdId(Integer.parseInt(sourceNames[i]));
                fromWallAlertDTO.setSiteName(siteData.getName());
                fromWallAlertDTO.setSitePhoto(siteData.getPhoto());

                List<Map<String,Object>> mainLPList = fromWallAlertMapper.findFromWallAlertList(fromWallAlertDTO);
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
