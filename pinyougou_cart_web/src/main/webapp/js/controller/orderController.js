 //控制层 
app.controller('orderController' ,function($scope,$controller   ,orderService,addressService,cartService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		orderService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		orderService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		orderService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		$scope.order.receiver = $scope.address.contact;
		$scope.order.receiverAreaName = $scope.address.address;
		$scope.order.receiverMobile = $scope.address.mobile;
		orderService.add($scope.order).success(
			function (response) {
				if (response.success){
					location.href="pay.html";
				} else {
					alert(response.message);
				}
			}
		)
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		orderService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		orderService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//查询订单地址
	$scope.findByUserId = function () {
		addressService.findByUserId().success(
			function (response) {
				$scope.addressList = response;
				for (let i = 0;i<$scope.addressList.length;i++){
					if ( $scope.addressList[i].isDefault=="1"){
						$scope.address = $scope.addressList[i];
					}
				}
				if ($scope.address==null){
					$scope.address = $scope.addressList[0];
				}
			}
		)
	}

	//选择地址
	$scope.isSelected = function (address) {
		$scope.address = address;
	}

	$scope.order = {paymentType:"1"};

	//查询购物车列表
	$scope.findCartList =function () {
		cartService.findCartList().success(
			function (response) {
				$scope.cartList = response;
				sum();
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
