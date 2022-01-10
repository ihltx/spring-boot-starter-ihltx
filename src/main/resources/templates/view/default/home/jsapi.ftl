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
    var jsApiList = ['checkJsApi', 'onMenuShareTimeline',
        'onMenuShareAppMessage', 'onMenuShareQQ',
        'onMenuShareWeibo', 'hideMenuItems',
        'showMenuItems', 'hideAllNonBaseMenuItem',
        'showAllNonBaseMenuItem', 'translateVoice',
        'startRecord', 'stopRecord', 'onRecordEnd',
        'playVoice', 'pauseVoice', 'stopVoice',
        'uploadVoice', 'downloadVoice', 'chooseImage',
        'previewImage', 'uploadImage', 'downloadImage',
        'getNetworkType', 'openLocation', 'getLocation',
        'hideOptionMenu', 'showOptionMenu', 'closeWindow',
        'scanQRCode', 'chooseWXPay',
        'openProductSpecificView', 'addCard', 'chooseCard',
        'openCard'];
    function getJsApiSignature(){
        axios.get('/weixin/jsApiSignature?url=' + encodeURIComponent("http://c4tgap.natappfree.cc/home/testhtml"))
            .then(function (response) {
                wx.config({
                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                    appId: response.data.appId, // 必填，公众号的唯一标识
                    timestamp: response.data.timestamp, // 必填，生成签名的时间戳
                    nonceStr: response.data.nonceStr, // 必填，生成签名的随机串
                    signature: response.data.signature,// 必填，签名
                    jsApiList: jsApiList // 必填，需要使用的JS接口列表
                });

            })
            .catch(function (error) {

            });
    }


    getJsApiSignature();

    var appId = null;
    var timeStamp = null;
    var nonceStr = null;
    var package = null;
    var appId = null;


    function pay(){
        axios.get('/weixin/jsApiPayUnifiedOrder?out_trade_no=${out_trade_no}')
            .then(function (response) {
                if(response.data.return_code =="SUCCESS" &&  response.data.result_code =="FAIL"){
                    alert(response.data.err_code_des);
                }else{
                    wx.chooseWXPay({
                        timestamp: response.data.timeStamp, // 支付签名时间戳，注意微信jssdk中的所有使用timestamp字段均为小写。但最新版的支付后台生成签名使用的timeStamp字段名需大写其中的S字符
                        nonceStr: response.data.nonceStr, // 支付签名随机串，不长于 32 位
                        package: response.data.package, // 统一支付接口返回的prepay_id参数值，提交格式如：prepay_id=\*\*\*）
                        signType: "MD5", // 微信支付V3的传入RSA,微信支付V2的传入格式与V2统一下单的签名格式保持一致
                        paySign: response.data.paySign, // 支付签名
                        success: function (res) {
                            // 支付成功后的回调函数
                            location.reload();
                        },
                        // 支付取消回调函数
                        cancel: function (res) {
                            alert("用户取消支付");
                            //'用户取消支付'
                        },
                        // 支付失败回调函数
                        fail: function (res) {
                            alert("支付失败");
                            //支付失败
                        }
                    });
                }
            })
            .catch(function (error) {

            });


    }


</script>
<body>
    订单【编号：${out_trade_no}】支付状态：
    <#if paied>
            已支付
        <#else>
            未支付
            <button onclick="pay()">微信支付</button>
    </#if>
</body>
</html>