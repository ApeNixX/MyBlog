layui.use(['layer'], function () {
    var layer = layui.layer
//获得登录用户未读消息
    $.ajax({
        type:'post',
        url:'/user/getUserNews',
        dataType:'json',
        data:{
        },
        success:function (data) {
            var thisPageName = window.location.pathname + window.location.search;
            var news = $('.news');
            if(data['status'] != 101 && data['data']['result'] != 0){
                news.append($('<span class="layui-badge">' + data['data']['result']['allNewsNum'] + '</span>'));
                if(thisPageName === "/user/user"){
                    if(data['data']['result']['commentNum'] !== 0){
                        $('.commentMessage').find('a').append($('<span class="commentNotReadNum am-margin-right am-fr am-badge am-badge-danger am-round">' + data['data']['result']['commentNum'] + '</span>'));
                    }
                    if(data['data']['result']['leaveMessageNum'] !== 0){
                        $('.leaveWord').find('a').append($('<span class="leaveMessageNotReadNum am-margin-right am-fr am-badge am-badge-danger am-round">' + data['data']['result']['leaveMessageNum'] + '</span>'));
                    }
                }
            }
        },
        error:function () {
        }
    });
    $('.userList .clickLi').click(function () {
        var flag = $(this).attr('class').substring(8);
        $('#personalDate,#basicSetting,#commentMessage,#leaveMessage,#leaveWord,#privateWord,#articleThumbsUp').css("display", "none");
        $("#" + flag).css("display", "block");
    });

    $('.basicSetting').click(function () {
        $('#phone').val("");
        $('#authCode').val("");
        $('#password').val("");
        $('#surePassword').val("");
    });

//获得个人信息
    function getUserPersonalInfo() {
        $.ajax({
            type: 'post',
            url: '/user/getUserPersonalInfo',
            dataType: 'json',
            data: {},
            success: function (data) {
                if (data['status'] == 101) {
                    $.get("/toLogin", function (data, status, xhr) {
                        window.location.replace("/login");
                    });
                } else {
                    $('#username').attr("value", data['data']['username']);
                    var personalPhone = data['data']['phone'];
                    $('#personalPhone').html(personalPhone.substring(0, 3) + "****" + personalPhone.substring(7));
                    $('#trueName').attr("value", data['data']['trueName']);
                    $('#birthday').attr("value", data['data']['birthday']);
                    var gender = data['data']['gender'];
                    if (gender == "male") {
                        $('.genderTable input').eq(0).attr("checked", "checked");
                    } else {
                        $('.genderTable input').eq(1).attr("checked", "checked");
                    }
                    $('#email').attr("value", data['data']['email']);
                    $('#personalBrief').val(data['data']['personalBrief']);

                    $('#headPortrait').attr("src", data['data']['avatarImgUrl']);
                }
            },
            error: function () {
            }
        });
    }

//保存个人资料
    var savePersonalDateBtn = $('#savePersonalDateBtn');
    var username = $('#username');
    var trueName = $('#trueName');
    var birthday = $('#birthday');
    var gender = $('.genderTable input');
    var email = $('#email');
    var personalBrief = $('#personalBrief');
    savePersonalDateBtn.click(function () {
        var usernameValue = username.val();
        var genderValue = "male";
        if (usernameValue.length === 0) {
            layer.msg("昵称不能为空");
        } else if (!gender[0].checked && !gender[1].checked) {
            layer.msg("性别不能为空");
        } else {
            if (gender[0].checked) {
                genderValue = "male";
            } else {
                genderValue = "female";
            }
            $.ajax({
                type: 'post',
                url: '/user/savePersonalDate',
                dataType: 'json',
                data: {
                    username: username.val(),
                    trueName: trueName.val(),
                    birthday: birthday.val(),
                    gender: genderValue,
                    email: email.val(),
                    personalBrief: personalBrief.val()
                },
                success: function (data) {
                    if (data['status'] == 101) {
                        $.get("/toLogin", function (data, status, xhr) {
                            window.location.replace("/login");
                        });
                    } else {
                        if (data['status'] == 503) {
                            layer.msg("更改成功,重新登录后生效", {icon: 6});
                            setTimeout(function () {
                                location.reload();
                            }, 3000);
                        } else if (data['status'] == 1) {
                            layer.msg("更改失败，昵称太长啦", {icon: 5});
                        } else if (data['status'] == 2) {
                            layer.msg("更改失败，昵称不能为空", {icon: 5});
                        } else if (data['status'] == 504) {
                            layer.msg("更改个人信息成功", {icon: 6});
                        } else if (data['status'] == 3) {
                            layer.msg("该昵称已被占用", {icon: 5});
                        } else {
                            layer.msg("更改个人信息失败", {icon: 5});
                        }
                    }
                },
                error: function () {
                }
            });
        }
    });

    var phone = $('#phone');
    var authCode = $('#authCode');
    var password = $('#password');
    var surePassword = $('#surePassword');

    phone.blur(function () {
        var pattren = /^1[345789]\d{9}$/;
        var phoneValue = phone.val();
        if (pattren.test(phoneValue)) {
            phone.removeClass("wrong");
            phone.addClass("right");
        } else {
            phone.removeClass("right");
            phone.addClass("wrong");
        }
    });
    phone.focus(function () {
        $('.notice').css("display", "none");
    });

// 定义发送时间间隔(s)
    var my_interval;
    my_interval = 60;
    var timeLeft = my_interval;
//重新发送计时函数
    var timeCount = function () {
        window.setTimeout(function () {
            if (timeLeft > 0) {
                timeLeft -= 1;
                $('#authCodeBtn').html(timeLeft + "秒重新发送");
                timeCount();
            } else {
                $('#authCodeBtn').html("重新发送");
                timeLeft = 60;
                $("#authCodeBtn").attr('disabled', false);
            }
        }, 1000);
    };
//发送短信验证码
    $('#authCodeBtn').click(function () {
        $('.notice').css("display", "none");
        $('#authCodeBtn').attr('disabled', true);
        var phoneLen = phone.val().length;
        if (phoneLen == 0) {
            layer.msg("手机号不能为空",{icon:5});
            $('#authCodeBtn').attr('disabled', false);
        } else {
            if (phone.hasClass("right")) {
                $.ajax({
                    type: 'post',
                    url: '/getCode',
                    dataType: 'json',
                    data: {
                        phone: $('#phone').val(),
                        sign: "changePassword"
                    },
                    success: function (data) {
                        if (parseInt(data['status']) == 200) {
                            layer.msg("短信验证码发送成功",{icon:6});
                            timeCount();
                        } else {
                            layer.msg("短信验证码发送异常",{icon:5})
                        }
                    },
                    error: function () {
                    }
                });
            } else {
                layer.msg("手机号不正确");
                $('#authCodeBtn').attr('disabled', false);
            }
        }

    });

//修改密码
    $('#changePasswordBtn').click(function () {
        $('.notice').css("display", "none");
        if (phone.val().length === 0) {
            layer.msg("手机号不能为空",{icon:5});
        } else if (phone.hasClass("wrong")) {
            layer.msg("手机号不正确",{icon:5});
        } else if (authCode.val().length === 0) {
            layer.msg("验证码不能为空",{icon:5});
        } else if (password.val().length === 0) {
            layer.msg("新密码不能为空",{icon:5});
        } else if (surePassword.val().length === 0) {
            layer.msg("确认密码不能为空",{icon:5});
        } else {
            if (password.val() !== surePassword.val()) {
                layer.msg("确认密码不正确",{icon:5});
            } else {
                $.ajax({
                    type: 'post',
                    url: '/user/changePassword',
                    dataType: 'json',
                    data: {
                        phone: phone.val(),
                        authCode: authCode.val(),
                        newPassword: password.val()
                    },
                    success: function (data) {
                        if (data['status'] == 44) {
                            layer.msg("验证码不正确")
                        } else if (data['status'] == 55) {
                            layer.msg("手机号不存在")
                        } else if (data['status'] == 500) {
                            layer.msg("手机号不正确");
                        } else {
                            layer.msg("密码修改成功,请重新登录",{icon:6});
                            setTimeout(function () {
                                location.reload();
                            }, 3000);
                        }
                    },
                    error: function () {
                        layer.msg("修改密码失败");
                    }
                })
            }
        }
    });

//填充用户评论
    function putInCommentInfo(date) {
        var msgContent = $('.msgContent');
        msgContent.empty();
        if (date['result'].length == 0) {
            msgContent.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
        } else {
            msgContent.append($(' <div class="msgReadTop">' +
                '未读消息：<span class="msgIsReadNum">' + date['msgIsNotReadNum'] + '</span>' +
                '<a class="msgIsRead">全部标记为已读</a>' +
                '</div>'));
            $.each(date['result'], function (index, obj) {
                var msgRead = $('<div class="msgRead" id="p' + obj['id'] + '"></div>')
                var msgReadSign = $('<span class="msgReadSign"></span>');
                if (obj['isRead'] == 1) {
                    msgRead.append(msgReadSign);
                }
                msgRead.append($('<span class="am-badge msgType">评论</span>'));
                if (obj['pId'] == 0) {
                    msgRead.append($('<span class="msgHead">' +
                        '<a class="msgPerson">' + obj['answerer'] + '</a>评论了你的博文' +
                        '</span>'));
                } else {
                    msgRead.append($('<span class="msgHead">' +
                        '<a class="msgPerson">' + obj['answerer'] + '</a>回复了你的评论' +
                        '</span>'));
                }
                msgRead.append($('<div class="msgTxt">' +
                    '<span><a class="articleTitle" href="/article/details/' + obj['articleId'] + '#p' + obj['id'] + '" target="_blank">' + obj['articleTitle'] + '</a></span>' +
                    '<span class="msgDate">' + obj['commentDate'] + '</span>' +
                    '</div><hr>'));
                msgContent.append(msgRead);
            })
            msgContent.append($('<div class="my-row" id="commentPageFather">' +
                '<div id="commentPagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '<li class="am-disabled"><a href="#">&laquo; 上一页</a></li>' +
                '<li class="am-active"><a href="#">1</a></li>' +
                '<li><a href="#">2</a></li>' +
                '<li><a href="#">3</a></li>' +
                '<li><a href="#">4</a></li>' +
                '<li><a href="#">5</a></li>' +
                '<li><a href="#">下一页 &raquo;</a></li>' +
                '</ul>' +
                '</div>' +
                '</div>'));
        }

        //已读一条消息
        $('.articleTitle').click(function () {
            var parent = $(this).parent().parent().parent();
            var isRead = true;
            var num = $('.msgIsReadNum').html();
            if (parent.find($('.msgReadSign')).length != 0) {
                isRead = false;
            }
            if (isRead == false) {
                var id = parent.attr('id').substring(1);
                $.ajax({
                    type: 'get',
                    url: '/user/readThisMsg',
                    dataType: 'json',
                    data: {
                        id: id,
                        msgType: 1
                    },
                    success: function (data) {
                    },
                    error: function () {
                    }
                })
                //去掉未读红点
                parent.find($('.msgReadSign')).removeClass('msgReadSign');
                //未读消息减1
                $('.msgIsReadNum').html(--num);

                // 去掉左侧栏未读消息
                if (num == 0) {
                    $('.commentNotReadNum').remove();
                } else {
                    $('.commentNotReadNum').html(num);
                }

                // 去掉导航栏下拉框未读消息
                var newsNum = $('.newsNum').html();
                --newsNum;
                if (newsNum == 0) {
                    $('.newsNum').remove();
                } else {
                    $('.newsNum').html(newsNum);
                }
            }
        })

        //全部标记为已读
        $('.msgIsRead').click(function () {
            var num = $('.msgIsReadNum').html();
            if (num != 0) {
                $.ajax({
                    type: 'get',
                    url: '/user/readAllMsg',
                    dataType: 'json',
                    data: {
                        msgType: 1
                    },
                    success: function (data) {
                        if (data['status'] == 101) {
                            $.get("/toLogin", function (data, status, xhr) {
                                window.location.replace("/login");
                            });
                        } else {
                            $('.msgIsReadNum').html(0);
                            $('.msgContent').find($('.msgReadSign')).removeClass('msgReadSign');

                            $('.commentNotReadNum').remove();
                            var allNum = $('.newsNum').html();
                            if (allNum == num) {
                                $('.newsNum').remove();
                            } else {
                                $('.newsNum').html(allNum - num);
                            }
                        }
                    },
                    error: function () {
                    }
                })
            }
        })
    }

//填充用户留言
    function putInLeaveWordInfo(data) {
        var msgContent = $('.msgContent');
        msgContent.empty();
        if (data['result'].length == 0) {
            msgContent.append($('<div class="noNews">' +
                '这里空空如也' +
                '</div>'));
        } else {
            var msgTop = $('<div class="msgReadTop">未读消息：<span class="msgIsReadNum">' + data['msgIsNotReadNum'] + '</span><a class="msgIsRead">全部标记为已读</a></div>');
            msgContent.append(msgTop);
            $.each(data['result'], function (index, obj) {
                var msgRead = $('<div id="p' + obj['id'] + '" class="msgRead"></div>');
                var msgReadSign = $('<span class="msgReadSign"></span>');
                if (obj['isRead'] == 1) {
                    msgRead.append(msgReadSign);
                }
                msgRead.append($('<span class="am-badge msgType">留言</span>'));
                var msgHead = $('<span class="msgHead"></span>');
                if (obj['pId'] == 0) {
                    msgHead.append($('<span class="msgHead">' +
                        '<a class="msgPerson">' + obj['answerer'] + '</a>给你留言啦！' +
                        '</span>'));
                } else {
                    msgHead.append($('<span class="msgHead">' +
                        '<a class="msgPerson">' + obj['answerer'] + '</a>回复了你的留言！' +
                        '</span>'));
                }
                msgHead.append($('<a href="/' + obj['pageName'] + '#p' + obj['id'] + '" target="_blank" class="msgHref">戳这里去看看</a>'));
                msgRead.append(msgHead);
                msgRead.append($('<span class="msgDate">' + obj['leaveMessageDate'] + '</span><hr>'));
                msgContent.append(msgRead);
            })
            msgContent.append($('<div class="my-row" id="leaveWordPage">' +
                '<div class="leaveWordPagePagination">' +
                '</div>' +
                '</div>'))
        }

        //已读一条消息
        $('.msgHref').click(function () {
            var parent = $(this).parent().parent();
            var isRead = true;
            var num = $('.msgIsReadNum').html();
            if (parent.find($('.msgReadSign')).length != 0) {
                isRead = false;
            }
            if (isRead == false) {
                var id = parent.attr('id').substring(1);
                $.ajax({
                    type: 'get',
                    url: '/user/readThisMsg',
                    dataType: 'json',
                    data: {
                        id: id,
                        msgType: 2
                    },
                    success: function (data) {
                    },
                    error: function () {
                    }
                })
                //去掉未读红点
                parent.find($('.msgReadSign')).removeClass('msgReadSign');
                //未读消息减1
                $('.msgIsReadNum').html(--num);

                // 去掉左侧栏未读消息
                if (num == 0) {
                    $('.leaveMessageNotReadNum').remove();
                } else {
                    $('.leaveMessageNotReadNum').html(num);
                }

                // 去掉导航栏下拉框未读消息
                var newsNum = $('.newsNum').html();
                --newsNum;
                if (newsNum == 0) {
                    $('.newsNum').remove();
                } else {
                    $('.newsNum').html(newsNum);
                }
            }
        })

        //全部标记为已读
        $('.msgIsRead').click(function () {
            var num = $('.msgIsReadNum').html();
            if (num != 0) {
                $.ajax({
                    type: 'get',
                    url: '/user/readAllMsg',
                    dataType: 'json',
                    data: {
                        msgType: 2
                    },
                    success: function (data) {
                        if (data['status'] == 101) {
                            $.get("/toLogin", function (data, status, xhr) {
                                window.location.replace("/login");
                            });
                        } else {
                            $('.msgIsReadNum').html(0);
                            $('.msgContent').find($('.msgReadSign')).removeClass('msgReadSign');

                            $('.leaveMessageNotReadNum').remove();
                            var allNum = $('.newsNum').html();
                            if (allNum == num) {
                                $('.newsNum').remove();
                            } else {
                                $('.newsNum').html(allNum - num);
                            }
                        }
                    },
                    error: function () {
                    }
                })
            }

        })
    }

//填充悄悄话内容
    function putInPrivateWord(data) {
        var yesterdayContent = $('.yesterdayContent');
        yesterdayContent.empty();
        if (data['result'].length == 0) {
            yesterdayContent.append($('<div class="noYesterday">' +
                '你的曾今我好像未曾参与耶' +
                '</div>'));
        } else {
            var says = $('<div class="says"></div>');
            $.each(data['result'], function (index, obj) {
                var say = $('<div class="say"></div>');
                var youSay = $('<div class="youSay"></div>');
                youSay.append($('<div class="youSayTime">' +
                    obj['publisherDate'] +
                    '</div>'));
                youSay.append($('<div class="you">' +
                    '<span>' + obj['publisher'] + '</span>：' + obj['privateWord'] +
                    '</div>'));
                if (obj['replyContent'] !== "") {
                    youSay.append($('<div class="me">' +
                        obj['replyContent'] + '：' + obj['replier'] +
                        '</div>'));
                } else {
                    youSay.append($('<div class="me">' +
                        '<span class="noReply">暂未收到回复</span>' +
                        '</div>'));
                }
                say.append(youSay);
                says.append(say);
            });
            says.append($('<div class="my-row" id="page-father">' +
                '<div id="privateWordPagination">' +
                '<ul class="am-pagination  am-pagination-centered">' +
                '<li class="am-disabled"><a href="#">&laquo; 上一页</a></li>' +
                '<li class="am-active"><a href="#">1</a></li>' +
                '<li><a href="#">2</a></li>' +
                '<li><a href="#">3</a></li>' +
                '<li><a href="#">4</a></li>' +
                '<li><a href="#">5</a></li>' +
                '<li><a href="#">下一页 &raquo;</a></li>' +
                '</ul>' +
                '</div>' +
                '</div>'));
            yesterdayContent.append(says);
        }
    }

//获得评论
    function getUserComment(currentPage) {
        $.ajax({
            type: 'post',
            url: '/user/getUserComment',
            dataType: 'json',
            data: {
                rows: "10",
                pageNum: currentPage
            },
            success: function (data) {
                if (data['status'] == 101) {
                    $.get("/toLogin", function (data, status, xhr) {
                        window.location.replace("/login");
                    });
                }
                putInCommentInfo(data['data']);
                scrollTo(0, 0);//回到顶部

                //分页
                $("#commentPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function (currentPage) {
                        getUserComment(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取评论信息失败");
            }
        })
    }

//获得留言
    function getUserLeaveWord(currentPage) {
        $.ajax({
            type: 'post',
            url: '/user/getUserLeaveWord',
            dataType: 'json',
            data: {
                rows: "10",
                pageNum: currentPage
            },
            success: function (data) {
                if (data['status'] == 101) {
                    $.get("/toLogin", function (data, status, xhr) {
                        window.location.replace("/login");
                    });
                }
                putInLeaveWordInfo(data['data']);
                scrollTo(0, 0);//回到顶部

                //分页
                $(".leaveWordPagePagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function (currentPage) {
                        getUserLeaveWord(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取留言信息失败");
            }
        })
    }

//获得悄悄话内容
    function getPrivateWordByPublisher(currentPage) {
        $.ajax({
            type: 'post',
            url: '/getPrivateWordByPublisher',
            dataType: 'json',
            data: {
                rows: "5",
                pageNum: currentPage
            },
            success: function (data) {
                if (data['status'] == 101) {
                    $.get("/toLogin", function (data, status, xhr) {
                        window.location.replace("/login");
                    });
                }
                putInPrivateWord(data['data']);

                //分页
                $("#privateWordPagination").paging({
                    rows: data['data']['pageInfo']['pageSize'],//每页显示条数
                    pageNum: data['data']['pageInfo']['pageNum'],//当前所在页码
                    pages: data['data']['pageInfo']['pages'],//总页数
                    total: data['data']['pageInfo']['total'],//总记录数
                    callback: function (currentPage) {
                        getPrivateWordByPublisher(currentPage);
                    }
                });
            },
            error: function () {
                alert("获取悄悄话失败");
            }
        })
    }

// 点击评论管理
    $('.commentMessage').click(function () {
        getUserComment(1);
    });
//点击留言管理
    $('.leaveWord').click(function () {
        getUserLeaveWord(1);
    });

//发布悄悄话
    $('.userSayBtn').click(function () {
        var userSay = $('#userSay').val();
        userSay = $.trim(userSay);
        if (userSay == "") {
            layer.msg("你还没说两句呢");
        } else {
            $.ajax({
                type: 'post',
                url: '/sendPrivateWord',
                dataType: 'json',
                data: {
                    privateWord: userSay
                },
                success: function (data) {
                    if (data['status'] == 101) {
                        $.get("/toLogin", function (data, status, xhr) {
                            window.location.replace("/login");
                        });
                    } else {
                        if (data['status'] == 0) {
                            successNotice("发布悄悄话成功");
                        }
                        $('#userSay').val("");
                        getPrivateWordByPublisher(1);
                    }
                },
                error: function () {
                    alert("发布悄悄话失败");
                }
            })
        }
    });
//点击悄悄话
    $('.privateWord').click(function () {
        getPrivateWordByPublisher(1);
    });

    getUserPersonalInfo();
})

