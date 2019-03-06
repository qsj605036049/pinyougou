 //控制层 
app.controller('indexController' ,function($scope,$controller   ,contentService){
	
	$controller('baseController',{$scope:$scope});//继承

    //初始化广告列表
    $scope.contentList=[];
    /**
	 * 基于广告分类查询广告列表
     */
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
			//接收广告分类列表
			$scope.contentList[categoryId]=response;
        })
    }
    
});	
