
init("");
function init(resourceTypeName){
    var div = document.getElementById("resource-main");
    div.innerHTML = '';
    layui.use('flow', function(){
        var $ = layui.jquery; //不用额外加载jQuery，flow模块本身是有依赖jQuery的，直接用即可。
        var flow = layui.flow;
        flow.load({
            elem: '#resource-main' //指定列表容器
            ,isAuto: true //到底页面底端自动加载下一页，设为false则点击'加载更多'才会加载
            ,end:"没有更多资源啦，期待博主后续发布~"
            ,done: function(page, next){ //到达临界点（默认滚动触发），触发下一页
                var lis = [];
                //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
                $.ajax({
                    url : '/getAllResource',
                    type : 'post',
                    data : {pageIndex : page, resourceTypeName : resourceTypeName},
                    success : function(item){
                        var resourceSharings = item.resourceSharings;
                        for(var i = 0;i < resourceSharings.length;i++){
                            lis.push('<div class="resource shadow  animated rotateIn">'
                                +  '<div class="resource-cover">'
                                +  '<img src="'+ resourceSharings[i].imgSrc +'" alt="'+ resourceSharings[i].resourceName +'" />'
                                +  '</div>'
                                +'<h1 class="resource-title">'+'<a href="javascript:" target="_blank">'+ resourceSharings[i].resourceName +'</a>'+'</h1>'
                                +'<p class="resource-abstract">'+ resourceSharings[i].resourceDescribe +'</p>'
                                +'<div class="resource-info">'
                                +  '<span class="category"><a href="javascript:;" onclick="init('+"'"+resourceSharings[i].resourceTypeName+"'"+');"><i class="fa fa-tags fa-fw"></i>&nbsp;'+ resourceSharings[i].resourceTypeName +'</a></span>'
                                +  '<span class="author"><i class="fa fa-user fa-fw"></i>'+ resourceSharings[i].resourceUserName +'</span>'
                                +'<div class="clear"></div>'
                                +'</div>'
                                +'<div class="resource-footer">'
                                +  '<a class="layui-btn layui-btn-small layui-btn-primary" href="'+ resourceSharings[i].resourcePath +'" target="_blank"><i class="fa fa-eye fa-fw"></i>演示</a>'
                                +  '<a class="layui-btn layui-btn-small layui-btn-primary" href="'+ resourceSharings[i].resourcePath +'" target="_blank"><i class="fa fa-download fa-fw"></i>下载</a>'
                                +'</div>'
                                +'<div class="clear"></div>'
                                +'</div>'
                            );
                        }
                        if(item.currPage == item.pages){
                            lis.push('<div class="clear"></div>');
                        }
                        next(lis.join(''), page < item.pages);//pages是后台返回的总页数
                    }
                });
            }
        });
    });
}