$(function () {
    $.ajax({
        method: 'post',
        url: '/admin/getHistoricalViews',
        success: function (data) {
            var chart = echarts.init(document.getElementById("viewChart"));
            var option = {
                title: {
                    text: '历史访客量'
                },
                tooltip: {},
                legend: {
                    data:['浏览量']
                },
                xAxis: {
                    data: data.times,
                    axisLabel: {
                        interval:0,
                        rotate:0
                    }
                },
                yAxis: {},
                series: [{
                    name: '浏览量',
                    type: 'line',
                    data: data.views
                }]
            };
            chart.setOption(option)
        }
    });
})