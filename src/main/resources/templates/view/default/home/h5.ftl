<!DOCTYPE html>
<html lang="cn">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>js-sdk</title>
    <script src="https://res.wx.qq.com/open/js/jweixin-1.6.0.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/vue@2"></script>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
</head>
<script type="text/javascript">

    function pay(){
        axios.get('/weixin/h5PayUnifiedOrder?out_trade_no=${out_trade_no}')
            .then(function (response) {
                if(response.data!=null){
                    if( response.data.return_code=="SUCCESS" && response.data.result_code=="SUCCESS"){
                        location= response.data.mweb_url;
                    }else{
                        alert(response.data.err_code_des);
                    }
                }else{
                    alert("未知错误，微信H5支付失败");
                }

            })
            .catch(function (error) {

            });


    }

    var intervalHandle= null;
    function checkPaied(){
        axios.get('/weixin/orderQuery?out_trade_no=${out_trade_no}')
            .then(function (response) {
                if(response.data!=null){
                    if( response.data.return_code=="SUCCESS" && response.data.result_code=="SUCCESS"){
                        if(response.data.trade_state=="SUCCESS"){
                            document.getElementById("spanPay").innerHTML="已支付";
                            clearInterval(intervalHandle);
                        }else if(response.data.trade_state=="REFUND"){
                            document.getElementById("spanPay").innerHTML="已支付(有退款)";
                            clearInterval(intervalHandle);
                        }
                    }
                }
            })
            .catch(function (error) {

            });
    }

    intervalHandle = setInterval(checkPaied,5000);



</script>
<body>
    订单【编号：${out_trade_no}】支付状态：
    <span id="spanPay">
    <#if paied>
        已支付
    <#else>
        未支付
        <button onclick="pay()">微信H5支付</button>
    </#if>
    </span>
</body>
</html>