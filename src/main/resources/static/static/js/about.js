
layui.use(['element', 'jquery', 'form', 'layedit'], function () {
    var element = layui.element();
    var form = layui.form();
    var $ = layui.jquery;
    var layedit = layui.layedit;


    /**
     * 填充评论列表
     */
    function putInMessage(data) {
        $(".blog-comment").empty();
        var comment = $(".blog-comment");
        $.each(data,function (index,obj) {
            var li = $(' <li id="p' + obj['id'] + '" class="animated zoomIn"></li>');
            var cp = $('<div class="comment-parent">'+
                '<img src="'+obj['avatarImgUrl']+'" alt="不落阁" />'+
                '<div class="info">'+
                '<span class="username">'+obj['answerer']+'</span>'+'<span class="master" style="display: none">'+obj['master']+'</span>'+
                '</div>'+
                '<div class="content">'
                + obj['leaveMessageContent']+
                '</div>'+
                '<p class="info info-footer">'+
                '<span class="time">'+'<i class="fa fa-clock-o" aria-hidden="true"></i>&nbsp;'+obj['leaveMessageDate']+'</span>'+'&nbsp;&nbsp;<span class="time">'+'<i class="fa fa-map-marker"></i>&nbsp;'+obj['location']+'</span>'+
                '<a class="btn-reply" href="javascript:;" onclick="btnReplyClick(this)">回复</a>'+
                '</p>'+
                '</div>');
            var rep = $('<div class="replycontainer layui-hide">'+
                '<form class="layui-form" action="">'+
                '<div class="layui-form-item">'+
                '<textarea name="replyContent" lay-verify="replyContent" placeholder="请输入回复内容" class="layui-textarea" style="min-height:80px;"></textarea>'+
                '</div>'+
                '<div class="layui-form-item">'+
                '<button class="layui-btn" lay-submit="formReply" lay-filter="formReply">提交</button>'+
                '</div>'+
                '</form>'+
                '</div>');
            comment.append(li);
            li.append(cp);
            li.append(rep);

            var reLen = obj['replies'].length;// 回复的数量

            if(reLen>0){
                $.each(obj['replies'],function (index,obj) {

                    var child = $('<hr/>'+'<div id="p' + obj['id'] + '" class="comment-child">'+
                        '<img src="'+obj['avatarImgUrl']+'" alt="Absolutely" />'+
                        '<div class="info">'+
                        '<span class="username">'+'<span class="username1">'+obj['answerer']+'</span>'+'<span class="master" style="display: none">'+obj['master']+'</span>' +'<font style="color: black;">回复&nbsp;</font>'+'@'+obj['respondent'] +'</span>'+
                        '<span>'+obj['leaveMessageContent']+'</span>'
                        +'</div>'+
                        '<p class="info info-footer">'+
                        '<span class="time">'+'<i class="fa fa-clock-o" aria-hidden="true"></i>&nbsp;'+obj['leaveMessageDate']+'</span>'+'&nbsp;&nbsp;<span class="time">'+'<i class="fa fa-map-marker"></i>&nbsp;'+obj['location']+'</span>'+
                        '<a class="btn-reply" href="javascript:;" onclick="btnReplyClick1(this)">回复</a>'+'</p>'+

                        + '</div>');

                    li.append(child)
                    li.append(rep)
                })
            }



        })
    }


    /**
     * 评论查询
     */
    $.ajax({
        type: "GET",
        url: "/getPageLeaveMessage",
        // contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        data: {
            pageName: window.location.pathname.substring(1)
        },
        success: function (data) {
            if (data.data != "" && data.data.length != 0) {
                putInMessage(data.data);

                $(".master").each(function () {
                    var $this=$(this);
                    if($this.html()=='1'){
                        $this.prev().after('<span class="article_is_zz">博主</span>&nbsp;')
                    }
                })

            } else {
                putInNotMessage();
            }
        },
        error: function () {
        }
    });

    function putInNotMessage() {
        $(".blog-comment").empty();
        var comment = $(".blog-comment");
        var  center=
            $( '<li class="animated zoomIn">'+
                '<div class="comment-parent">' +
                '暂无评论，抢个沙发吧' +
                '</div>'+
                '</li>');
        comment.append(center);
    }



    //评论和留言的编辑器
    var editIndex = layedit.build('remarkEditor', {
        height: 150,
        tool: ['face', '|', 'left', 'center', 'right', '|', 'link'],
    });
    //评论和留言的编辑器的验证
    layui.form().verify({
        content: function (value) {
            value = $.trim(layedit.getText(editIndex));
            if (value == "") return "至少得有一个字吧";
            layedit.sync(editIndex);
        }
    });

    //Hash地址的定位
    var layid = location.hash.replace(/^#tabIndex=/, '');
    var layid1 = location.hash.replace(/^#p/, '');
    if (layid == "") {
        element.tabChange('tabAbout', 1);
    }else if( layid1 != ""){
        element.tabChange('tabAbout', 4);
    }
    element.tabChange('tabAbout', layid);

    element.on('tab(tabAbout)', function (elem) {
        location.hash = 'tabIndex=' + $(this).attr('lay-id');
    });
    var cname = returnCitySN.cname;
    //监听留言提交
    form.on('submit(formLeaveMessage)', function (data) {

        var loadIndex = '';

        $.ajax({
            type:'post',
            url:'/publishLeaveMessage',
            dataType:'json',
            data:{
                leaveMessageContent:data.field.editorContent,
                pageName:window.location.pathname.substring(1),
                location:cname
            },
            beforeSend: function () {
                loadIndex = layer.load(1, {
                    shade: [0.1, '#fff']
                });
            },
            complete: function () {
                layer.close(loadIndex);
            },
            success:function (data) {
                if(data.status==200){
                    $('#remarkEditor').val('');
                    editIndex = layui.layedit.build('remarkEditor', {
                        height: 150,
                        tool: ['face', '|', 'left', 'center', 'right', '|', 'link'],
                    });
                    layer.msg("发表评论成功！",{icon:6});
                    putInMessage(data.data)

                    $(".master").each(function () {
                        var $this=$(this);
                        if($this.html()=='1'){
                            $this.prev().after('<span class="article_is_zz">博主</span>&nbsp;')
                        }
                    })
                }else if(data.status==502){
                    layer.msg("请先登录哦~", {icon: 5});
                }else {
                    layer.msg("回复失败~", {icon: 5});
                }
            },
            error: function () {
                layer.msg("发表评论失败！");
            }
        })


        return false;
    });

    //监听留言回复提交
    form.on('submit(formReply)', function (data) {
        //var index = layer.load(1);
        var loadIndex = '';
        var $this = $(this);
        var replyContent = $this.parent().parent().find($('.layui-textarea')).val();
        var pId = $this.parent().parent().parent().parent().attr("id");
        if(replyContent == ""){
            layer.msg("我没看清你要回复啥吖！", {icon: 5});
        } else {
            $.ajax({
                type: 'post',
                url: '/publishLeaveMessageReply',
                dataType: 'json',
                data: {
                    leaveMessageContent: replyContent,
                    pageName: window.location.pathname.substring(1),
                    parentId: pId,
                    respondent: respondent,
                    location:cname
                },
                beforeSend: function () {
                    loadIndex = layer.load(1, {
                        shade: [0.1, '#fff']
                    });
                },
                complete: function () {
                    layer.close(loadIndex);
                },
                success: function (data) {
                    if (data.status == 200) {
                        layer.msg("回复成功！", {icon: 6});
                        putInMessage(data.data);
                        $(".master").each(function () {
                            var $this=$(this);
                            if($this.html()=='1'){
                                $this.prev().after('<span class="article_is_zz">博主</span>&nbsp;')
                            }
                        })
                    } else if(data.status==502){
                        layer.msg("请先登录哦~", {icon: 5});
                    }else {
                        layer.msg("回复失败~", {icon: 5});
                    }
                },
                error: function () {
                    layer.msg("回复失败！", {icon: 5});
                }

            })
        }

        return false;
    });
});
var respondent = "";
function btnReplyClick(elem) {
    var $ = layui.jquery;
   respondent = $(elem).parent('p').parent('.comment-parent').find($('.username')).html();
   //layer.msg(respondent);
    $(elem).parent().parent().parent().find($('.layui-textarea')).attr('placeholder','@'+respondent);
    $(elem).parent('p').parent('.comment-parent').siblings('.replycontainer').toggleClass('layui-hide');
    if ($(elem).text() == '回复') {
        $(elem).text('收起')
    } else {
        $(elem).text('回复')
    }
}
function btnReplyClick1(elem) {
    var $ = layui.jquery;
    respondent = $(elem).parent('p').parent('.comment-child').find($('.username1')).html();
    $(elem).parent().parent().parent().find($('.layui-textarea')).attr('placeholder','@'+respondent);
    //layer.msg(respondent);
    $(elem).parent('p').parent('.comment-child').siblings('.replycontainer').toggleClass('layui-hide');
    if ($(elem).text() == '回复') {
        $(elem).text('收起')
    } else {
        $(elem).text('回复')
    }
}
systemTime();

function systemTime() {
    //获取系统时间。
    var dateTime = new Date();
    var year = dateTime.getFullYear();
    var month = dateTime.getMonth() + 1;
    var day = dateTime.getDate();
    var hh = dateTime.getHours();
    var mm = dateTime.getMinutes();
    var ss = dateTime.getSeconds();

    //分秒时间是一位数字，在数字前补0。
    mm = extra(mm);
    ss = extra(ss);

    //将时间显示到ID为time的位置，时间格式形如：19:18:02
    document.getElementById("time").innerHTML = year + "-" + month + "-" + day + " " + hh + ":" + mm + ":" + ss;
    //每隔1000ms执行方法systemTime()。
    setTimeout("systemTime()", 1000);
}

//补位函数。
function extra(x) {
    //如果传入数字小于10，数字前补一位0。
    if (x < 10) {
        return "0" + x;
    }
    else {
        return x;
    }
}