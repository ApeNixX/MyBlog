
var bloggerReward = "张张张张先森";

var deleteArticleId="";
var friendLinkId="";

$('.superAdminList .superAdminClick').click(function () {
    var flag = $(this).attr('class').substring(16);
    $('#statistics,#articleManagement,#articleThumbsUp,#articleCategories,#friendLink,#rewardManagement,#userFeedback,#privateWord').css("display","none");
    $("#" + flag).css("display","block");
});


//填充点赞信息
function putInArticleThumbsUp(data) {
    var msgContent = $('.msgContent');
    msgContent.empty();
    if(data['result'].length == 0){
        msgContent.append($('<div class="noNews">' +
            '这里空空如也' +
            '</div>'));
    } else {
        msgContent.append($('<div class="msgReadTop">' +
            '未读消息：<span class="msgIsReadNum">' + data['msgIsNotReadNum'] + '</span>'+
            '<a class="msgIsRead">全部标记为已读</a>' +
            '</div>'));
        $.each(data['result'], function (index, obj) {
            var msgRead = $('<div class="msgRead" id="p' + obj['id'] + '"></div>');
            if(obj['isRead'] == 1){
                msgRead.append($('<span class="msgReadSign"></span>'));
            }
            msgRead.append($('<span class="am-badge msgType">点赞</span>'));
            msgRead.append($('<span class="msgHead"><a class="msgPerson">' + obj['praisePeople'] + '</a>点赞了你的博文</span>'));
            msgRead.append($('<div class="msgTxt">' +
                '<span><a class="articleTitle" href="/article/' + obj['articleId'] + '" target="_blank">' + obj['articleTitle'] + '</a></span>' +
                '<span class="msgDate">' + obj['likeDate'] + '</span>' +
                '</div>' +
                '<hr>'));
            msgContent.append(msgRead);
        });
        msgContent.append($('<div class="my-row" id="thumbsUpPage">' +
            '<div class="thumbsUpPagination">' +
            '</div>' +
            '</div>'))
    }

    //已读一条消息
    $('.articleTitle').click(function () {
        var parent = $(this).parent().parent().parent();
        var isRead = true;
        var num = $('.msgIsReadNum').html();
        if(parent.find($('.msgReadSign')).length != 0){
            isRead = false;
        }
        if(isRead == false){
            var id = parent.attr('id').substring(1);
            $.ajax({
                type:'get',
                url:'/user/readThisThumbsUp',
                dataType:'json',
                data:{
                    id:id,
                },
                success:function (data) {
                },
                error:function () {
                }
            })
            //去掉未读红点
            parent.find($('.msgReadSign')).removeClass('msgReadSign');
            //未读消息减1
            $('.msgIsReadNum').html(--num);

            // 去掉左侧栏未读消息
            if(num == 0){
                $('.articleThumbsUpNum').remove();
            } else {
                $('.articleThumbsUpNum').html(num);
            }
        }
    })

    //全部标记为已读
    $('.msgIsRead').click(function () {
        var num = $('.msgIsReadNum').html();
        if(num != 0){
            $.ajax({
                type:'get',
                url:'/user/readAllThumbsUp',
                dataType:'json',
                data:{
                },
                success:function (data) {
                    if(data['status'] == 101){
                        $.get("/toLogin",function(data,status,xhr){
                            window.location.replace("/login");
                        });
                    }else if (data['status'] == 202){
                        dangerNotice("已读所有文章点赞失败")
                    } else{
                        $('.msgIsReadNum').html(0);
                        $('.msgContent').find($('.msgReadSign')).removeClass('msgReadSign');

                        $('.articleThumbsUpNum').remove();
                    }
                },
                error:function () {
                }
            })
        }

    })
}


//获得文章点赞信息
function getArticleThumbsUp(currentPage) {
    $.ajax({
        type:'post',
        url:'/user/getArticleThumbsUp',
        dataType:'json',
        data:{
            rows:10,
            pageNum:currentPage
        },
        success:function (data) {
            if(data['status'] == 101){
                $.get("/toLogin",function(data,status,xhr){
                    window.location.replace("/login");
                });
            } if(data['status'] == 503){
                layer.msg("你没有权限哦~",{icon:5})
            }
            putInArticleThumbsUp(data['data']);
            scrollTo(0,0);//回到顶部

            //分页
            $(".thumbsUpPagination").paging({
                rows:data['data']['pageInfo']['pageSize'],//每页显示条数
                pageNum:data['data']['pageInfo']['pageNum'],//当前所在页码
                pages:data['data']['pageInfo']['pages'],//总页数
                total:data['data']['pageInfo']['total'],//总记录数
                callback:function(currentPage){
                    getArticleThumbsUp(currentPage);
                }
            });
        },
        error:function () {
            alert("获取文章点赞信息失败");
        }
    });
}


//点击点赞管理
$('.articleThumbsUp').click(function () {
    getArticleThumbsUp(1);
});

