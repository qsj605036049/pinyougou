 //控制层 
app.controller('cartController' ,function($scope,$controller   ,cartService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    $scope.findCartList =function () {
		cartService.findCartList().success(
			function (response) {
				$scope.cartList = response;
				sum();
			}
		)
	}

	$scope.addItemToCartList = function (itemId,num) {
		cartService.addItemToCartList(itemId,num).success(
			function (response) {

				if (response.success){
				//添加成功
					$scope.findCartList();
				} else{
					//添加失败
					alter(response.message);
				}
			}
	)
	}

	let sum = function () {
    	$scope.total = 0;
    	$scope.totalPrice = 0;
    	let cartList = $scope.cartList;
		for(let i = 0;i<cartList.length;i++){
			let orderItemList = cartList[i].orderItemList;
			for (let j = 0;j<orderItemList.length;j++){
				$scope.total += orderItemList[j].num;
				$scope.totalPrice += orderItemList[j].totalFee;
			}
		}
	}
    
});	
