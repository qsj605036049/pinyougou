//定义控制器 参数一：控制器名称  参数二：控制器要做的事情
//$scope 可以理解为：全局的作用域对象 作用：js与html数据交换的桥梁
app.controller("brandController", function ($scope,$controller,brandService) {

    //完成控制器继承  参数一：继承的父controller名称  参数二：共享$scope变量，完成控制器继承操作
    $controller("baseController",{$scope:$scope});

    //查询所有品牌列表
    $scope.findAll=function () {
        //请求成功后success回调函数， 参数：回调函数接收响应结果
        //http://localhost:8081/brand/findAll.do
        brandService.findAll().success(function (response) {
            $scope.list=response;
        });
    }

    //分页查询
    $scope.findPage=function (pageNum,pageSize) {
        brandService.findPage(pageNum,pageSize).success(function (response) {
            //当前页数据列表
            $scope.list=response.rows;
            //满足条件总记录数据
            $scope.paginationConf.totalItems=response.total;
        });
    }

    //初始化查询条件对象
    $scope.searchEntity={};

    //条件分页查询
    $scope.search=function (pageNum,pageSize) {
        brandService.search($scope.searchEntity,pageNum,pageSize).success(function (response) {
            //当前页数据列表
            $scope.list=response.rows;
            //满足条件总记录数据
            $scope.paginationConf.totalItems=response.total;
        });
    }


    //保存品牌
    $scope.save=function () {
        var method=null;
        if($scope.entity.id!=null){
            //品牌id不为空时，执行修改操作
            method=brandService.update($scope.entity);
        }else {
            method=brandService.add($scope.entity);
        }
        //$scope.entity页面组装的品牌实体对象
        method.success(function (response) {
            if(response.success){
                //重新加载列表数据
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        });
    }

    //根据id查询品牌数据
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        });
    }

    //批量删除操作
    $scope.dele=function () {
        if(confirm("您确定要删除吗？")){
            brandService.dele($scope.selectIds).success(function (response) {
                if(response.success){
                    //重新加载列表数据
                    $scope.reloadList();
                    //清空selectIds
                    $scope.selectIds=[];
                }else {
                    alert(response.message);
                }
            });
        }
    }


});
