//控制层
app.controller('payController', function ($scope, $controller,$location, payService) {

    $controller('baseController', {$scope: $scope});//继承

    //生成二维码
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.out_trade_no=response.out_trade_no;
                $scope.total_fee=(response.total_fee/100).toFixed(2);
                //基于qrious插件生成支付二维码
                var qr = window.qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 300,
                    value: response.code_url,
                    level: 'H'
                })
                $scope.queryStatus();
            }
    )
    }

    //查询订单状态
    $scope.queryStatus = function () {
        payService.queryStatus($scope.out_trade_no).success(
            function (response) {
                if (response.success){
                    location.href = "paysuccess.html#?money="+$scope.total_fee;
                }else {
                    location.href = "payfail.html";
                }
            }
        )
    }

    //查询金额
    $scope.findMoney = function () {
       $scope.money =  $location.search()["money"];
    }

});	
