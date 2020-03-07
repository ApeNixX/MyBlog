$(function () {
    initTimeLine();
})
function initTimeLine(){
    $.ajax({
        type:"post",
        url:"/getTimeLine",
        dataType:"json",
        success:function (data) {
            if(data){
                // var item = eval("("+data+")");
                var item = data.data;
                var timelineStr = $("#timelineId");
                var y = "";
                var m = "";
                var str = "";
                for(var i = 0 ; i < item.length ; i++){
                    var year = item[i].publishDate.split("-")[0];
                    var month = item[i].publishDate.split("-")[1];
                    var content = item[i].content;
                    // var bid = item[i].b_id;
                    var bct = item[i].publishDate;
                    var yearStr = "<div class=\"timeline-year\">"+
                        "<h2><a class=\"yearToggle\" href=\"javascript:;\">"+year+"年</a><i class=\"fa fa-caret-down fa-fw\"></i></h2>";
                    var monthStr = "<div class=\"timeline-month\">"+
                        "<h3 class=\" animated fadeInLeft\"><a class=\"monthToggle\" href=\"javascript:;\">"+month+"月</a><i class=\"fa fa-caret-down fa-fw\"></i></h3>";
                    var ymdStr = "<li class=\" \">"+"<div class=\"h4  animated fadeInLeft\">"+
                        "<p class=\"date\">"+bct+"</p>"+
                        "</div>"+
                        "<p class=\"dot-circle animated \"><i class=\"fa fa-dot-circle-o\"></i></p>"+
                        "<div class=\"content animated fadeInRight\"><a style=\"color:#fff\" >"+content+"</a></div>"+
                        "<div class=\"clear\"></div></li>";

                    if(i==0){
                        str += yearStr+monthStr+"<ul>"+ymdStr;
                        y = year;
                        m = month;
                    }else{
                        if(year == y && month == m){
                            str += ymdStr;
                        }
                        if(year == y && month != m){
                            str += "</ul></div>";
                            str += monthStr+"<ul>"+ymdStr;
                            m = month;
                        }
                        if(year != y){
                            str += "</div>";
                            str += yearStr+monthStr+"<ul>"+ymdStr;
                            y = year;
                        }
                    }
                }

                timelineStr.html(str);
                $('.monthToggle').click(function () {
                    $(this).parent('h3').siblings('ul').slideToggle('slow');
                    $(this).siblings('i').toggleClass('fa-caret-down fa-caret-right');
                });
                $('.yearToggle').click(function () {
                    $(this).parent('h2').siblings('.timeline-month').slideToggle('slow');
                    $(this).siblings('i').toggleClass('fa-caret-down fa-caret-right');
                });
            }else {
                alert("获取时间轴出错");
            }
        }
    })



}