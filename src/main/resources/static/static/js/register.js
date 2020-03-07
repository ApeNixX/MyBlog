layui.use(['form','jquery','layer'], function () {
    var form   = layui.form;
    var $      = layui.jquery;
    var layer  = layui.layer;
    var phone = $('#phone');
    var username = $('#username');
    var password = $('#password');
    var rpwd = $('#rpwd');


    //添加表单失焦事件

    //验证手机号
    $('#phone').blur(function () {
        var pattren = /^1[345789]\d{9}$/;
        if(!$(this).val().match(pattren)){
            $('#swr').removeAttr('hidden');
            $('#sri').attr('hidden','hidden');
            layer.msg('请输入合法手机号', {icon: 5});

        }else {
            // $('#sri').removeAttr('hidden');
            // $('#swr').attr('hidden','hidden');

            $.ajax({
                type:'post',
                url:'/phoneCheck',
                dataType:'json',
                data:{phone:$(this).val()},
                success:function (data) {
                    if(data.status==200){
                        $('#sri').removeAttr('hidden');
                        $('#swr').attr('hidden','hidden');
                        phone.addClass("phone_success");
                    }else {
                        $('#swr').removeAttr('hidden');
                        $('#sri').attr('hidden','hidden');
                        phone.removeClass("phone_success");
                        layer.msg('当前手机号已被注册! ', {icon: 5})
                    }
                }
            })
        }

    });

    //验证用户名
    $('#username').blur(function() {
        var user = $(this).val();

        //alert(user);
        $.ajax({
            url:'/usernameCheck',
            type:'post',
            dataType:'json',
            data:{username:user},
            //验证用户名是否可用
            success:function(data){
                if (data.status == 200) {
                    $('#ri').removeAttr('hidden');
                    $('#wr').attr('hidden','hidden');

                } else if(data.status == 4){
                    $('#wr').removeAttr('hidden');
                    $('#ri').attr('hidden','hidden');
                    layer.msg('用户名不能为空', {icon: 5})

                } else {
                    $('#wr').removeAttr('hidden');
                    $('#ri').attr('hidden','hidden');
                    layer.msg('当前用户名已被占用! ', {icon: 5})
                }

            }
        })

    });

    // you code ...
    // 为密码添加正则验证
    $('#password').blur(function() {
        var reg = /^[\w]{6,12}$/;
        if(!($('#password').val().match(reg))){
            //layer.msg('请输入合法密码');
            $('#pwr').removeAttr('hidden');
            $('#pri').attr('hidden','hidden');
            layer.msg('请输入合法密码', {icon: 5});
            password.removeClass("password_success");
        }else {
            $('#pri').removeAttr('hidden');
            $('#pwr').attr('hidden','hidden');
            password.addClass("password_success");

        }
    });

    //验证两次密码是否一致
    $('#rpwd').blur(function() {
        if($('#password').val() != $('#rpwd').val()){
            $('#rpwr').removeAttr('hidden');
            $('#rpri').attr('hidden','hidden');
            layer.msg('两次输入密码不一致!', {icon: 5});
            rpwd.removeClass("rpwd_success");
        }else {
            $('#rpri').removeAttr('hidden');
            $('#rpwr').attr('hidden','hidden');
            rpwd.addClass("rpwd_success");
        };
    });




    // 定义发送时间间隔(s)
    var msg_btn = $("#msg_btn");
    var my_interval;
    my_interval = 60;
    var timeLeft = my_interval;
    //重新发送计时函数
    var timeCount = function() {
        window.setTimeout(function() {
            if(timeLeft > 0) {
                timeLeft -= 1;
                msg_btn.html(timeLeft + "秒重新发送");
                timeCount();
            } else {
                msg_btn.html("重新发送");
                timeLeft=60;
                msg_btn.attr('disabled',false);
            }
        }, 1000);
    };
    //获取验证码
    msg_btn.click(function(){
        msg_btn.attr('disabled',true);
        if(phone.hasClass("phone_success")) {
            //layer.msg("go");
            var index;
            $.ajax({
                type:'post',
                url:'/getCode',
                dataType:'json',
                data:{
                    "phone":phone.val(),
                    sign:"register"
                },
                beforeSend:function(){
                    index = layer.msg('正在获取验证码，请稍候',{icon: 16,time:false,shade:0.8});
                },
                success:function (data) {
                    if(parseInt(data['status']) == 200){
                        layer.close(index);
                        layer.msg("短信验证码发送成功", {icon: 6});
                        timeCount();
                    } else {
                        layer.msg("短信验证码发送异常", {icon: 5});
                    }

                },
                error:function () {
                    layer.msg("网络异常", {icon: 5});
                }
            })
        }else {
            layer.msg("请先填写正确的手机号！", {icon: 5});
            msg_btn.attr('disabled',false);
        }

    })

    $("#registerFormBtn").click(function () {
        if(password.hasClass("password_success") && rpwd.hasClass("rpwd_success")) {
            $.ajax({
                type: 'post',
                url: '/register',
                dataType: 'json',
                data: {
                    phone: $('#phone').val(),
                    username: $('#username').val(),
                    password: $('#password').val(),
                    authCode: $('#auth_code').val(),
                },
                success: function (data) {
                    if (data.status == 200) {
                        layer.msg('注册成功', {icon: 6});
                        setTimeout(function () {
                            location.href = "/user/login";
                        }, 1000)
                    } else if (data.status == 44) {
                        layer.msg('验证码错误', {icon: 5});
                    } else if (data.status == 55) {
                        layer.msg('用户名重复', {icon: 5});
                    } else {
                        layer.msg('手机号错误', {icon: 5});
                    }
                }
            })
        }else {
            layer.msg("请先输入正确的密码及相关信息！", {icon: 5});
        }

    })


});