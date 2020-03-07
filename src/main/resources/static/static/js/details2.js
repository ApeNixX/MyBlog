var shareIndex, $;
layui.use(['layer', 'form'], function () {
    $ = layui.jquery;
    var form = layui.form
        , device = layui.device();

    var articleId = "";
    $.ajax({
        type: 'HEAD', // 获取头信息，type=HEAD即可
        url : window.location.href,
        async:false,
        success:function (data, status, xhr) {
            articleId = xhr.getResponseHeader("articleId");
        }
    });

    var events = {
        //分享
        share: function () {
            shareIndex = layer.open({
                type: 1,
                shade: 0.6,
                offset: '30%',
                shadeClose: true,
                area: ['auto', '50px'],
                resize: false,
                skin: 'share',
                closeBtn: 0,
                anim: 1,
                title: false, //不显示标题
                content: $('.bdsharebuttonbox')
            });
        }

        //点赞
        , praise: function () {
            // var localdata = layui.data('blog')
            //     , articleId = $('#articleId').val()
            //     , self = this;
            // if (localdata['praise' + articleId]) {
            //     layer.tips('你已点过赞了，若收获颇大，可打赏作者！^_^', self, { tips: 1, time: 2000 });
            //     return;
            // }
            $.get('/article/addArticleLike', { articleId: articleId,shareType : 1 }, function (res) {
                if (res.status === 200) {
                    // layui.data('blog', {
                    //     key: 'praise' + $('#articleId').val()
                    //     , value: true
                    // });
                    //点赞+1
                    var cnt = Number($('#praiseCnt').text()) + 1;
                    $('#praiseCnt').text(cnt);
                    layer.msg('Thank you ^_^', { icon: 1, time: 2000 });
                } else if(res.status == 500){
                    layer.msg('你已点过赞了，若收获颇大，可打赏作者！^_^',  {
                        icon: 1,
                        time: 2000 //2秒关闭（如果不配置，默认是3秒）
                    } );
                }else {
                    // layer.msg('点赞出错啦！0.0');
                    layer.msg('请先登录哦！',{icon:5});

                }
            });
        }

        //打赏
        , reword: function () {
            layer.tab({
                area: ['330px', '373px'],
                offset: '30%',
                shade: 0.6,
                tab: [{
                    title: '微信',
                    content: '<img style="width:330px;height:330px;" src="https://apenixx-oss.oss-cn-shenzhen.aliyuncs.com/public/BlogInfo/wechat.png" />'
                }, {
                    title: '支付宝',
                    content: '<img style="width:330px;height:330px;" src="https://apenixx-oss.oss-cn-shenzhen.aliyuncs.com/public/BlogInfo/ali.PNG" />'
                }]
            });

        }
    };

    $('*[blog-event]').on('click', function () {
        var eventName = $(this).attr('blog-event');
        events[eventName] && events[eventName].call(this);
    });

    $('*[blog-event="reword"]').on('mouseover', function () {
        layer.tips('一元足以感动我 ^_^', this, { tips: 1, time: 2000 });
    });
});