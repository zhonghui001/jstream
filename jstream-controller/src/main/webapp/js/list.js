$(function (){


    url="/sys/job/list"
    $.ajax({
        type: "get",
        url: url,
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        success : function(data){
            list = data.data;
            for (var i = 0; i < list.length; i++) {
                var jobId = list[i].id;
                var jobState = list[i].jobState
                var applicationId = list[i].applicationId
                var status = list[i].applicationState;
                var appName = list[i].appName
                var button = '';
                switch (jobState) {
                    case 'INITED':
                        button = '<button class="btn btn-default start" data-id="'+jobId+'">上线</button>'+
                            '<button class="btn btn-default edit" data-id="'+jobId+'">编辑</button>'+
                            '<button class="btn btn-default delete" data-id="'+jobId+'">删除</button>';
                        break;
                    case 'RUNNING':
                        button = '<button class="btn btn-default stop" data-id="'+jobId+'">下线</button>'+
                            '<button class="btn btn-default edit" data-id="'+jobId+'">编辑</button>'+
                            '<button class="btn btn-default logs" data-id="'+jobId+'">日志</button>';
                        break;
                    case 'KILLED':
                        button = '<button class="btn btn-default start" data-id="'+jobId+'">上线</button>'+
                            '<button class="btn btn-default edit" data-id="'+jobId+'">编辑</button>'+
                            '<button class="btn btn-default delete" data-id="'+jobId+'">删除</button>'+
                            '<button class="btn btn-default logs" data-id="'+jobId+'">日志</button>';
                        break;
                    case 'FAILED':
                        button = '<button class="btn btn-default start" data-id="'+jobId+'">上线</button>'+
                            '<button class="btn btn-default edit" data-id="'+jobId+'">编辑</button>'+
                            '<button class="btn btn-default delete" data-id="'+jobId+'">删除</button>'+
                            '<button class="btn btn-default logs" data-id="'+jobId+'">日志</button>';
                        break;
                    default:  //未知状态
                    //status = '未知状态:';
                }
                // if (yarnId != null && yarnId != '') {
                //     yarnId = '<a href="' + app_url + '" target="_blank">' + yarnId + '</a>';
                // }
                var tmp =
                    '<div class="row">' +
                    '<div class="col-md-1">' + jobId + '</div>' +
                    '<div class="col-md-1">' + appName + '</div>' +
                    '<div class="col-md-2">' + jobState + '</div>' +

                    '<div class="col-md-2">' + applicationId + '</div>' +
                    '<div class="col-md-1">' + status + '</div>' +
                    '<div class="col-md-4" jobId="' + jobId + '">' + button + '</div>' +
                    '</div>';
                $('#rowHead').after(tmp);
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            console.log(textStatus+errorThrown)
            alert("查询失败请稍后再试:"+errorThrown)
        }
    });

    $("body").delegate(".start",'click',function(){
        var id = $(this).attr("data-id")
        url="/sys/job/start?jobId="+id;
        $.ajax({
            type: "get",
            url: url,
            dataType: "json",
            success : function(data){
                alert(data.data)
                window.location.reload()
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus+errorThrown)
                alert("查询失败请稍后再试:"+errorThrown)
            }
        });
    })

    $("body").delegate(".logs",'click',function(){
        var id = $(this).attr("data-id")
        window.location.href="/logs/JSTREAM_JOB_"+id;
    })

    $("body").delegate(".delete",'click',function(){
        var id = $(this).attr("data-id")
        url="/sys/job/del?jobId="+id;
        $.ajax({
            type: "get",
            url: url,
            dataType: "json",
            success : function(data){
                alert(data.data)
                window.location.reload()
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus+errorThrown)
                alert("查询失败请稍后再试:"+errorThrown)
            }
        });
    })


    $("body").delegate(".edit",'click',function(){
        var id = $(this).attr("data-id")

        window.location.href="sql.html?jobId="+id
    })

    $("body").delegate(".stop",'click',function(){
        var id = $(this).attr("data-id")
        url="/sys/job/stop?jobId="+id;
        $.ajax({
            type: "get",
            url: url,
            dataType: "json",
            success : function(data){
                alert(data.data)
                window.location.reload()
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus+errorThrown)
                alert("查询失败请稍后再试:"+errorThrown)
            }
        });
    })




})
