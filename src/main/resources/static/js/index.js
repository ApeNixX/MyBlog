
    //网站最后更新时间（版本更新需更改）
    var siteLastUpdateTime = '2020年02月14日19点';

    //网站开始时间
    var siteBeginRunningTime = '2020-02-14 20:00:00';

    // 广告上下滚动
    // function getStyle(obj,name){
    //     if(obj.currentStyle)
    //     {
    //         return obj.currentStyle[name];
    //     }
    //     else
    //     {
    //         return getComputedStyle(obj,false)[name];
    //     }
    // }
    // function startMove(obj,json,doEnd){
    //     clearInterval(obj.timer);
    //     obj.timer=setInterval(function(){
    //         var oStop=true;
    //         for(var attr in json)
    //         {
    //             var cur=0;
    //             if(attr=='opacity')
    //             {
    //                 cur=Math.round(parseFloat(getStyle(obj,attr))*100);
    //             }
    //             else
    //             {
    //                 cur=parseInt(parseInt(getStyle(obj,attr)));
    //             }
    //             var speed=(json[attr]-cur)/6;
    //             speed=speed>0?Math.ceil(speed):Math.floor(speed);
    //             if(cur!=json[attr])
    //             {
    //                 oStop=false;
    //             }
    //             if(attr=='opacity')
    //             {
    //                 obj.style.filter='alpha(opacity:'+(speed+cur)+')';
    //                 obj.style.opacity=(speed+cur)/100;
    //             }
    //             else
    //             {
    //                 obj.style[attr]=speed+cur+'px';
    //             }
    //         }
    //         if(oStop)
    //         {
    //             clearInterval(obj.timer);
    //             if(doEnd) doEnd();
    //         }
    //     },30);
    // }
    // window.onload=function(){
    //     var oDiv=document.getElementsByClassName('roll')[0];
    //     var oUl=oDiv.getElementsByTagName('ul')[0];
    //     var aLi=oUl.getElementsByTagName('li');
    //
    //     var now=0;
    //     for(var i=0;i<aLi.length;i++)
    //     {
    //         aLi[i].index=i;
    //     }
    //
    //     function next(){
    //         now++;
    //         if(now==aLi.length)
    //         {
    //             now=0;
    //         }
    //         startMove(oUl,{top:-26*now})
    //     }
    //     //设置广播滚动时间
    //     var timer=setInterval(next,3000);
    //     oDiv.onmouseover=function(){
    //         clearInterval(timer);
    //     };
    //     oDiv.onmouseout=function(){
    //         timer=setInterval(next,3000);
    //     }
    // };


    //获得访客量，除文章显示界面外其他界面访客量通用
    var pageName = window.location.pathname + window.location.search;
    $.ajax({
        type:'get',
        url:'/getVisitorNumByPageName',
        dataType:'json',
        data:{
            pageName:pageName.substring(1)
        },
        success:function (data) {
            if(data['status'] == 103){
                $("#totalVisitors").html(0);
                $("#visitorVolume").html(0);
            } else {
                $("#totalVisitors").html(data['data']['totalVisitor']);
                $("#visitorVolume").html(data['data']['pageVisitor']);
            }
        },
        error:function () {
        }
    });

    //添加标签云
    // function putInTagsCloud(data){
    //     var tagCloud = $('.tag-cloud');
    //     tagCloud.empty();
    //     // tagCloud.append($('<h3 class="widget-title">标签云</h3>'));
    //     var widgetTagCloud = $('<div class="widget-tag-cloud" id="wrap"></div>');
    //     $.each(data, function (index, obj) {
    //         widgetTagCloud.append($('<a href="index/1?tag=' + obj['tagName'] + '" style="font-size:' + obj['tagSize'] + 'px">' + obj['tagName'] + '</a>'));
    //     });
    //     tagCloud.append(widgetTagCloud);
    // }



    //点击扫描二维码时获取二维码图片
    $('.myCardBtn').click(function () {
        $('.qq_code').attr("src","https://zhy-myblog.oss-cn-shenzhen.aliyuncs.com/static/img/qq_code.png");
        $('.weixin_code').attr("src","https://zhy-myblog.oss-cn-shenzhen.aliyuncs.com/static/img/weixin_code.png");
    });



    //标签云
    // $.ajax({
    //     type: 'GET',
    //     url: '/findTagsCloud',
    //     dataType: 'json',
    //     data: {
    //     },
    //     success: function (data) {
    //         if(data['data'].length == 0){
    //             var tagCloud = $('.tag-cloud');
    //             tagCloud.empty();
    //             // tagCloud.append($('<h3 class="widget-title">标签云</h3>'));
    //             var widgetTagCloud = $('<div class="widget-tag-cloud"><span>暂无标签</span></div>');
    //             tagCloud.append(widgetTagCloud);
    //             $('#right').append(tagCloud);
    //         } else {
    //             putInTagsCloud(data['data']);
    //         }
    //
    //     },
    //     error: function () {
    //     }
    // });


    //网站运行时间
    //beginTime为建站时间的时间戳
    function siteRunningTime(time) {
        var theTime;
        var strTime = "";
        if (time >= 86400){
            theTime = parseInt(time/86400);
            strTime += theTime + "天";
            time -= theTime*86400;
        }
        if (time >= 3600){
            theTime = parseInt(time/3600);
            strTime += theTime + "时";
            time -= theTime*3600;
        }
        if (time >= 60){
            theTime = parseInt(time/60);
            strTime += theTime + "分";
            time -= theTime*60;
        }
        strTime += time + "秒";

        $('.siteRunningTime').html(strTime);
    }

    var nowDate = new Date().getTime();
    //网站开始运行日期
    var oldDate = new Date(siteBeginRunningTime.replace(/-/g,'/'));
    var time = oldDate.getTime();
    var theTime = parseInt((nowDate-time)/1000);
    setInterval(function () {
        siteRunningTime(theTime);
        theTime++;
    },1000);