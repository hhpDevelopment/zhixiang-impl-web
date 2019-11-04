var isShow = true;
var allPage;

var limCount = 12;
var allDataTol;

var initPageplugins = true;

var curPag = 1;

var sdId;
var isZx;

var mainCategoryIdArray = [];
var smallArray = [];

var isManySite = false;

var lpJson = {};

var ajaxSCJCXXSearchFormOption = {
    beforeSubmit: showSCJCXXSearchRequest,
    success: showSCJCXXSearchResponse,
    url: "/libraryChange/getLibraryChangeList",
    type: "post",
    data:{page:curPag,limit:limCount},
    dataType: "json",
    timeout: 12000
}

$(function(){

    sdId = $("#currentUserSdId").text();
    isZx = $("#currentUserIsZX").text();

    var admName = $("#teShiro").html();
    $("#showLAdm").html(admName);
    var s_time = dateFormat($("#teShiro2").html(),'yyyy-MM-dd HH:mm:ss');
    $("#headAdmDate").html(s_time+" 注册");

    var leftMenuStorage = localStorage.getItem("leftMenuStorage");

    $("#permUl").html(leftMenuStorage);
    loadCurrentPageStyle("采购仓储","食材核准记录");

    loadUserList();

    $("#topShowLp").css("display","none");

    var fixHelperModified = function(e, tr) {
            var $originals = tr.children();
            var $helper = tr.clone();
            $helper.children().each(function(index) {
                $(this).width($originals.eq(index).width())
            });
            return $helper;
        },
        updateIndex = function(e, ui) {
            $('td.index', ui.item.parent()).each(function(i) {
                $(this).html(i + 1);
            });
        };
    $("#sort tbody").sortable({
        helper: fixHelperModified,
        stop: updateIndex
    }).disableSelection();

    $("#searchFormI").click(function () {
        $("#scjcxxSearchForm").ajaxSubmit(ajaxSCJCXXSearchFormOption);
        return false;
    });

    var allUserSites = getUserSites();
    if(allUserSites.result&&allUserSites.result.length>1){
        $("#form-group-sdId").css("display","block");
        //$("#userSites").css("display","block");
        $.each(allUserSites.result,function (ind,val) {
            var curSId = val.sdId;
            var sPho = allUserSites.obj[curSId].photo;
            $("#userSites").append("<option value='"+val.sdId+"' data-subtitle='"+val.address+"' data-left='<img src="+sPho+">'>"+val.name+"</option>");
        });
        isManySite = true;
    }else{
        $("#sdId").val(sdId);
        //$("#userSites").css("display","none");
    }
    $("#userSites").selectator({
        labels: {
            search: '选择站点'
        },
        showAllOptionsOnFocus: true
    });
    $("#userSites").next().css("width","100%");
    $("#userSites").next().css("min-height","3em");
    $("#userSites").next().find(".selectator_input").css("background","#1b1b1b");
    $("#userSites").on("change",function (e) {
        $(".search--open").css("background","rgba(0, 0, 0, 0.68)");
    });
    loadClassification();

    $("#changeMode").selectator({
        labels: {
            search: '选择核准类型'
        },
        showAllOptionsOnFocus: true
    });
    $("#changeMode").next().css("width","100%");
    $("#changeMode").next().find(".selectator_input").css("background","#1b1b1b");

    $(window).scroll(function () {
        if($(window).scrollTop() >= 100){
            $('.toTopRocket').fadeIn(300);
        }else{
            $('.toTopRocket').fadeOut(300);
        }
    });

    $(".selectator_options").css("z-index","999");
    $(".selectator_input").css("z-index","999");

    //初始化时间选择插件
    $("#dtBox").DateTimePicker({
        language:'zh-CN',
        defaultDate: new Date(),
        animationDuration:200,
        buttonsToDisplay: [ "SetButton", "ClearButton"],
        clearButtonContent: "取消"
    });
});

function showSCJCXXSearchRequest(formData, jqForm, options){
    return true;
}
function showSCJCXXSearchResponse(data, status){
    initDateTableData(data);
}
function setErrorStyle() {
    setTimeout(function () {
        /*$("#ingredientId").prev().css("top","0px");
        $("#ingredientId").prev().css("left","15.9em");*/
    },1000);
}
function pageChange(i) {
    curPag = accAdd(i,1);
    loadUserList();
    Pagination.Page($(".ht-page"), i, allDataTol, limCount);
}
function loadUserList(){
    if($("#userSites").val()){
        $("#alertSdId").val($("#userSites").val().toString());
    }
    ajaxSCJCXXSearchFormOption.data={page:curPag,limit:limCount};
    $("#scjcxxSearchForm").ajaxSubmit(ajaxSCJCXXSearchFormOption);
    return false;
}
function reloadMyDateTable() {
    if($("#userSites").val()){
        $("#alertSdId").val($("#userSites").val().toString());
    }else{
        $("#alertSdId").val("");
    }
    if($("#userSites").val()!=null&&$("#userSites").val()!=''){
        if($("#userSites").val().length==1){
            if($("#userSites").val()[0]!=$("#ingredientBaseId").find("option:selected").attr("data-siteId")){
                createJBox('yellow',['right','bottom'],'提示:系统检测到选择的站点与食材所属站点不一致，系统已默认搜寻食材所属站点',4600);
            }
        }else{
            createJBox('yellow',['right','bottom'],'提示:系统检测到您已选择多个站点，系统以食材所属站点为您努力搜寻',4600);
        }
    }
    $("#ingSdId").val($("#ingredientBaseId").find("option:selected").attr("data-siteId"));
    curPag = 1;
    ajaxSCJCXXSearchFormOption.data={page:curPag,limit:limCount};
    $("#scjcxxSearchForm").ajaxSubmit(ajaxSCJCXXSearchFormOption);
    return false;
}
function showRequest(formData, jqForm, options){
    var img = '';
    $.each($(".myImgFirst"),function(ind,val){
        var dataImg = $(val).attr("data-img");
        if(dataImg!=undefined&&dataImg!=null&&dataImg!=''){
            if(img==''){
                img = dataImg;
            }else{
                img = img+","+dataImg;
            }
        }
    });
    $("#snapPicture").val(img);

    if($("#selectSites").val()!=undefined&&$("#selectSites").val()!=null&&$("#selectSites").val()!=''){
        $("#sdIds").val($("#selectSites").val().join(","));
    }else{
        $("#sdIds").val("");
    }
    return true;
};
function showResponse(status, form,data, options){
    if(!isKickOut(data)){
        if(data.resultCode==100){
            loadUserList();
            myResetForm();
            $("#comInfo-modal").modal("hide");
            showJqueryConfirmWindow("#icon-jinlingyingcaitubiao14",data.msg);
        }else if(data.resultCode==101){
            showJqueryConfirmWindow("#icon-shangxin1","操作失败！深感抱歉，请联系管理人员为您加急处理");
        }else{
            showJqueryConfirmWindow("#icon-tishi1",data.msg);
        }
    }
}
function initDateTableData(data){
    if(!isKickOut(data)){
        if(data.resultCode==100){
            if(data.result!=null){
                $("#userListUl").html("");
                $.each(data.result,function (ind,val) {
                    if(val){
                        var comInfoTopStr = '';
                        var cgId = '';
                        if(val.id){
                            cgId = 'JD'+val.id;
                        }
                        var ingredientName = '';
                        if(val.ingredientName&&val.ingredientName!=''){
                            ingredientName = "-"+val.ingredientName;
                        }
                        var changePeople = '';
                        if(val.changePeople){
                            changePeople = "-由"+val.changePeople;
                        }
                        var changeTime = "";
                        if(val.changeTime){
                            changeTime = "于"+val.changeTime;
                        }
                        var changeMode = "";
                        switch (val.changeMode){
                            case "1":
                                changeMode = "核准库存增加";
                                break;
                            case "2":
                                changeMode = "核准库存减少";
                                break;
                        }
                        var changeNumber = "";
                        if(val.changeNumber){
                            changeNumber = val.changeNumber;
                        }
                        var meteringName = '';
                        if(val.meteringName&&val.meteringName!=''){
                            meteringName = val.meteringName;
                        }
                        var changeRemarks = '';
                        if(val.changeRemarks){
                            changeRemarks = "原因:"+val.changeRemarks;
                        }

                        comInfoTopStr = cgId+ingredientName+changePeople+changeTime+changeMode+changeNumber+meteringName;

                        var ibImgPath = getManyPicture(val.scspImgPath,val.sitePhoto,val.siteName,val.picturePrefix);

                        $("#userListUl").append("<div class='col-md-3 col-sm-3 col-xs-12' onclick='checkPurchase(this)'>" +
                            "<div class='tpl-table-images-content' style='position: relative;overflow: hidden;'>" +
                            "<div class='tpl-table-images-content-i-time-hhp' style='height: 4.4em;-webkit-line-clamp:3;' data-toggle2='tooltip' title='"+comInfoTopStr+"' data-original-title='"+comInfoTopStr+"'>"+comInfoTopStr+"</div>" +
                            /*"<div class='tpl-i-title-hhpHeight' style='height: 6.5em;' data-toggle='tooltip' title='' data-original-title=''>" +
                            /!*"<div>"+registration+"</div>"+
                            "<div>"+legalRepresentative+"</div>"+
                            "<div>"+topStr+"</div>"+
                            "<div>"+address+"</div>"+
                            "<div>"+deadline+"</div>"+*!/
                            "</div>" +*/

                            ibImgPath+
                            "<div class='tpl-table-images-content-block'>" +
                            "<div class='tpl-i-font' style='height: 3em;' data-toggle2='tooltip' title='"+changeRemarks+"' data-original-title='"+changeRemarks+"'>" + changeRemarks +
                            "</div>" +
                            "<div class='tpl-i-more'>" +
                            /*"<ul>" +
                            "<li><span class='am-icon-qq am-text-warning'> 100+</span></li>" +
                            "<li><span class='am-icon-weixin am-text-success'> 235+</span></li>" +
                            "<li><span class='am-icon-github font-green'> 600+</span></li>" +
                            "</ul>" +*/
                            "</div>" +
                            "<div class='am-btn-toolbar'>" +
                            "<div class='am-btn-group am-btn-group-xs tpl-edit-content-btn'>" +
                            "<button type='button' class='am-btn am-btn-default am-btn-secondary' style='width: 50% !important;' onclick='updateRole("+val.id+","+val.sdId+")'><i class='iconfont iconbianji3'></i> 编辑</button>" +
                            "<button type='button' class='am-btn am-btn-default am-btn-danger' style='width: 50% !important;' onclick='delRole("+val.id+","+val.sdId+")'><i class='iconfont iconshanchutishi'></i> 删除</button>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "</div>");

                        //只取了部分，不合适
                        //$("#areaId").find("option[value='"+val.areaId+"'][data-siteId='"+val.sdId+"']").attr("data-right","已提交实景");
                    }
                });
                allDataTol = data.total;
                if(initPageplugins){
                    /*
                     * 初始化插件
                     * @param  object:翻页容器对象
                     * @param  function:回调函数
                     * */
                    Pagination.init($(".ht-page"), pageChange);

                    /*
                    * 首次调用
                    * @param  object:翻页容器对象
                    * @param  number:当前页
                    * @param  number:总页数
                    * @param  number:每页数据条数
                    * */
                    Pagination.Page($(".ht-page"), 0, allDataTol, limCount);
                    initPageplugins = false;
                }else{
                    Pagination.Page($(".ht-page"), curPag-1, allDataTol, limCount);
                }
                $("#userListUl").viewer('update');
                $('#userListUl').viewer({
                    url: 'data-original',
                });

                $("[data-toggle='tooltip']").tooltip();
                $("[data-toggle2='tooltip']").tooltip({
                    placement:"bottom"
                });
            }else{
                showJqueryConfirmWindow("#icon-tishi1","未获取到核准明细");
            }
            isShow = true;
        }else if(data.resultCode==101){
            showJqueryConfirmWindow("#icon-shangxin1","获取核准明细信息异常！深感抱歉，请联系管理人员为您加急处理");
        }else{
            showJqueryConfirmWindow("#icon-tishi1",data.msg);
        }
    }
}
function delRole(id,delSdId,editOrDel,name) {
    if(null!=id&&id!=""){
        var url = '';
        var contentMsg = '';
        var resultMsg = '';
        if(editOrDel=='1'){
            url = "/libraryChange/setLibraryPurchase";
            contentMsg = "您确定要作废"+name+"单吗？";
            resultMsg = "作废";
        }else if(editOrDel=='4'){
            url = "/libraryChange/delLibraryPurchase";
            contentMsg = "您确定要删除"+name+"单吗？";
            resultMsg = "删除";
        }
        $.confirm({
            icon: '#icon-tishi1',
            theme: 'modern',
            title: '提示',
            content: contentMsg,
            animation: 'news',//动画
            closeAnimation: 'news',//关闭动画
            /*autoClose: 'showMyMsg2|3000',*/
            buttons: {
                showMyMsg2: {
                    text: '确定',
                    action: function () {
                        $.post(url,{"id":id,"sdId":delSdId,"purchStatus":4},function(data){
                            if(!isKickOut(data)){
                                //没有被踢出
                                if(data.resultCode==100){
                                    //刷新数据
                                    loadUserList();
                                    showJqueryConfirmWindow("#icon-jinlingyingcaitubiao14",resultMsg+name+'单成功！');
                                }else if(data.resultCode==101){
                                    showJqueryConfirmWindow("#icon-shangxin1","操作失败！深感抱歉，请联系管理人员为您加急处理");
                                }else{
                                    showJqueryConfirmWindow("#icon-tishi1",data.msg);
                                }
                            }
                        });
                    }
                },
                cancel: {
                    text: "取消"
                }
            }
        });
    }
}
function loadClassification() {
    $.post("/ingredientBase/getAll",function(data){
        if(!isKickOut(data)){
            //没有被踢出
            if(data.resultCode==100){
                $("#ingredientBaseId").html("");
                $("#ingredientBaseId").append("<option value=''>选择食材</option>");
                var whichImg = "";
                $.each(data.result,function (ind,val) {
                    if(val.scspImgPath	&&val.scspImgPath!=''){
                        var allImg = val.scspImgPath.split(",");
                        if(allImg[0].indexOf("http:")!=-1){
                            whichImg = allImg[0];
                        }else{
                            whichImg = val.picturePrefix+allImg[0];
                        }
                    }else{
                        whichImg = val.sitePhoto;
                    }
                    $("#ingredientBaseId").append("<option value='"+val.id+"' data-siteId='"+val.sdId+"' data-subtitle='"+val.siteName+"' data-left='<img src="+whichImg+">'>"+val.ingredientName+"</option>");
                    mainCategoryIdArray.push(val);
                });
                $("#ingredientBaseId").selectator({
                    labels: {
                        search: '选择食材'
                    },
                    showAllOptionsOnFocus: true
                });
                $("#ingredientBaseId").next().css("width","100%");
                $("#ingredientBaseId").next().find(".selectator_input").css("background","#1b1b1b");
            }
        }
    });
}
function exportSingle() {
    if($.isEmptyObject(lpJson)){
        showJqueryConfirmWindow("#icon-tishi1","请至少选择一条记录导出");
        return;
    }
    var purchPeople = '';
    $.each(lpJson,function (ind,val) {
        if(purchPeople==""){
            purchPeople = ind+"_"+val.name;
        }else{
            purchPeople = purchPeople+"-"+ind+"_"+val.name;
        }
    });
    window.location.href="/libraryChange/writeLibraryPurchases?purchPeople="+purchPeople;
    lpJson = {};
    //刷新数据
    loadUserList();
}
function showOpenColor() {
    $(".search").css("background","rgba(0, 0, 0, 0.68)");
    $(".search").css("overflow-y","scroll");
}
function setOpenColor() {
    $(".search").css("background","none");
    $(".search").css("overflow-y","hidden");
}
function myResetForm(){
    //清空权限数组
    $("#id").val("");
    $("#imgPath").val("");
    $("#sdId").val("");
    $("#sdIds").val("");

    $('#comInfoForm').resetForm();
    $('#comInfoForm').clearForm();
}