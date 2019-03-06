//服务层
app.service('secKillService', function ($http) {
    //查询秒杀商品列表
    this.findList = function () {
        return $http.get("secKill/findList.do");
    }
    //查询秒杀商品详情
    this.findOne = function (secKillGoodId) {
        return $http.get("secKill/findOne.do?secKillGoodId="+secKillGoodId);
    }

    //对秒杀商品下单
    this.saveOrder = function (secKillGoodId) {
        return $http.get("secKill/saveOrder.do?secKillGoodId="+secKillGoodId)
    }


});
