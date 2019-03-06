 //控制层 
app.controller('secKillController' ,function($scope,$controller,$location ,$interval  ,secKillService){
	
	$controller('baseController',{$scope:$scope});//继承

	//查询秒杀商品列表
	$scope.findList = function () {
		secKillService.findList().success(
			function (response) {
				$scope.secKillGoodsList = response;
			}
		)
	}

	//查询秒杀商品详情
	$scope.findOne = function () {
		var id = getId();
		secKillService.findOne(id).success(
			function (response) {
				$scope.secKillGood = response;
				var nowDate = new Date().getTime();
				var endDate = new Date(response.endTime).getTime();
				var time = Math.floor((endDate-nowDate)/1000);
				var t = $interval(function () {
					if (time>0){
						time--;
						$scope.timeString = $scope.convertTimeString(time);
					}else {
						$interval.cancel(t);
					}
				},1000);

			}
		)
	}

	let getId = function () {
		return $location.search()["secKillGoodId"];
	}

	//秒杀倒计时
	//时间格式化方法
	$scope.convertTimeString=function (allseconds) {
		//计算天数
		var days = Math.floor(allseconds/(60*60*24));

		//小时
		var hours =Math.floor( (allseconds-(days*60*60*24))/(60*60) );

		//分钟
		var minutes = Math.floor( (allseconds-(days*60*60*24)-(hours*60*60))/60 );

		//秒
		var seconds = allseconds-(days*60*60*24)-(hours*60*60)-(minutes*60);

		//拼接时间
		var timString="";
		if(days>0){
			timString=days+"天:";
		}

		if(hours<10){
			hours="0"+hours;
		}
		if(minutes<10){
			minutes="0"+minutes;
		}
		if(seconds<10){
			seconds="0"+seconds;
		}
		return timString+=hours+":"+minutes+":"+seconds;
	}

	//秒杀商品下单
	$scope.saveOrder = function () {
		secKillService.saveOrder(getId()).success(
			function (response) {
				if (response.success){
					alert(response.message);
				} else {
					alert(response.message);
				}
			}
		)
	}

    
});	
