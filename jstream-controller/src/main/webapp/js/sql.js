$(function (){


    $("#sinkType").bind('click',function(){
        sinkType = $(this).val()
        if (sinkType=='console'){
            $(".console").show();
            $(".mysql").hide();
        }else if(sinkType=='mysql'){
            $('.console').hide();
            $(".mysql").show();
        }else{
            $(".error li").html(sinkType+"暂时不支持，请关注后续版本");
            $(".notice").html($(".error").html());
            $(".console").hide();
        }
    })

    $("#save").click(function(){

        // var queryArray = $("#aform").serializeArray();
        // var jsonString= '{';
        // for (var i = 0; i < queryArray.length; i++) {
        //     jsonString+= JSON.stringify(queryArray[i].name) + ':' + JSON.stringify(queryArray[i].value) + ',';
        // }
        // jsonString= jsonString.substring(0, (jsonString.length - 1));
        // jsonString+= '}';
        // var data = JSON.parse(jsonString);
        // console.log(data)



        url="/sys/job/save"
        $.ajax({
            type: "post",
            url: url,
            contentType: "application/x-www-form-urlencoded",
            data:$("#aform").serialize(),
            dataType: "json",
            success : function(data){
                alert(data.data)
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus+errorThrown)
                alert("查询失败请稍后再试:"+errorThrown)
            }
        });

    })

    var jobId = window.location.href.split("=")[1]
    if (jobId!='' && typeof(jobId) != "undefined"){
        url="/sys/job/getJob?jobId="+jobId
        $.ajax({
            type: "get",
            url: url,
            contentType: "application/x-www-form-urlencoded",
            dataType: "json",
            success : function(data){
                $.each(data.data,function(name,value){
                    $("body [name='"+name+"']").val(value);
                })
                $("input[name='id']").val(jobId)

                //对于sink进行事件触发
                $("#sinkType").trigger('click');
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus+errorThrown)
                alert("查询失败请稍后再试:"+errorThrown)
            }
        });



    }

})
