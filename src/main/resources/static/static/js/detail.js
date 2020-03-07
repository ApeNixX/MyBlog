

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
// prettyPrint();
layui.use(['form', 'layedit'], function () {
    var form = layui.form();
    var $ = layui.jquery;
    var layedit = layui.layedit;
    /**
     * 填充评论列表
     */
    function putInComment(data) {
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
               + obj['commentContent']+
                '</div>'+
               '<p class="info info-footer">'+
               '<span class="time">'+'<i class="fa fa-clock-o" aria-hidden="true"></i>&nbsp;'+obj['commentDate']+'</span>&nbsp;'+'<i class="fa fa-map-marker"></i>&nbsp;'+obj['location']+'</span>'+
               '&nbsp;&nbsp;<a class="btn-reply" href="javascript:;" onclick="btnReplyClick(this)">回复</a>'+
               '</p>'+
                '</div>');
            var rep = $('<div class="replycontainer layui-hide">'+
                '<form class="layui-form" action="">'+
                '<div class="layui-form-item">'+
                '<textarea name="replyContent" lay-verify="replyContent" placeholder="请输入回复内容" class="layui-textarea" style="min-height:80px;"></textarea>'+
                '</div>'+
                '<div class="layui-form-item">'+
                '<button class="layui-btn" lay-submit="formReply1" lay-filter="formReply1">提交</button>'+
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
                         '<span class="username">'+'<span class="username1">'+obj['answerer']+'</span>'+'<span class="master" style="display: none">'+obj['master']+'</span>'+'<font style="color: black;">回复&nbsp;</font>'+'@'+obj['respondent'] +'</span>'+
                         '<span>'+obj['commentContent']+'</span>'
                    +'</div>'+
                     '<p class="info info-footer">'+'<i class="fa fa-clock-o" aria-hidden="true"></i>&nbsp;'+'<span class="time">'+obj['commentDate']+'</span>&nbsp;' +'<i class="fa fa-map-marker"></i>&nbsp;'+obj['location']+'</span>'+
                         '&nbsp;&nbsp;<a class="btn-reply" href="javascript:;" onclick="btnReplyClick1(this)">回复</a>'+'</p>'+

                    + '</div>');

                     li.append(child)
                    li.append(rep)
                })
            }



        })
    }
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
var articleId = "";
    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url : window.location.href,
        async:false,
        success:function (data, status, xhr) {
            articleId = xhr.getResponseHeader("articleId");
        }
    });

    /**
     * 评论查询
     */
    $.ajax({
        type: "POST",
        url: "/getAllComment",
        // contentType: "application/x-www-form-urlencoded",
        dataType: "json",
        data: {
            articleId: articleId
        },
        success: function (data) {
            if (data.data!="") {
                putInComment(data.data);
                $(".master").each(function () {
                    var $this=$(this);
                    if($this.html()=='1'){
                        $this.prev().after('<span class="article_is_zz">博主</span>&nbsp;')
                    }
                })
            } else {
                putInNotComment();
            }
        },
        error: function () {
        }
    });
    var cname = returnCitySN.cname;

    //监听评论回复提交
    form.on('submit(formReply1)', function (data) {
        var loadIndex = '';
        var $this = $(this);
        var replyContent = $this.parent().parent().find($('.layui-textarea')).val();
        var pId = $this.parent().parent().parent().parent().attr("id");
        if(replyContent == ""){
            layer.msg("我没看清你要回复啥吖！", {icon: 5});
        } else {
            $.ajax({
                type: 'post',
                url: '/publishReply',
                dataType: 'json',
                data: {
                    commentContent: replyContent,
                    articleId: articleId,
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
                        putInComment(data.data);
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
                error:function () {
                    layer.msg("回复失败！", {icon: 5});
                }

            })
        }
        return false;
    });

function putInNotComment() {
    $(".blog-comment").empty();
    var comment = $(".blog-comment");
      var  center=
      $( '<li class="animated zoomIn">'+
        '<div class="comment-parent">' +'<div class="layui-flow-more">'+
          '暂无评论，抢个沙发吧' +'</div>'+
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

    //监听评论提交
    form.on('submit(formRemark)', function (data) {
        var loadIndex = '';

        $.ajax({
            type:'post',
            url:'/publishComment',
            dataType:'json',
            data:{
                commentContent:data.field.editorContent,
                articleId:articleId,
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
                    putInComment(data.data);
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
});


//选中所有需放大的图片加上data-src属性
$("#wordsView img").each(function(index){
    if(!$(this).hasClass("emoji")){
        var a=$(this).attr('src');
        $(this).attr("data-src",a);

        $(this).addClass("enlargePicture");
    }
});
//放大图片框架
lightGallery(document.getElementById('wordsView'));


