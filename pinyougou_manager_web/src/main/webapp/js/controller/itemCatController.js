//控制层
app.controller('itemCatController', function ($scope, $controller, itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = itemCatService.update($scope.entity); //修改
        } else {
            if ($scope.grade > 2) {
                $scope.entity.parentId = $scope.entity_2.id;
            } else if ($scope.grade > 1 && $scope.grade < 3) {
                $scope.entity.parentId = $scope.entity_1.id;
            }else {
                $scope.entity.parentId = 0;
            }

            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    if ($scope.grade > 2) {
                        $scope.findByParentId($scope.entity_2.id);//刷新列表
                    } else if ($scope.grade > 1 && $scope.grade < 3) {
                        $scope.findByParentId($scope.entity_1.id);
                    }else {
                        $scope.findByParentId(0);
                    }
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        if ( confirm("是否删除?")) {
            //获取选中的复选框
            itemCatService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        if ($scope.grade > 2) {
                            $scope.findByParentId($scope.entity_2.id);//刷新列表
                        } else if ($scope.grade > 1 && $scope.grade < 3) {
                            $scope.findByParentId($scope.entity_1.id);
                        }else {
                            $scope.findByParentId(0);
                        }

                        $scope.selectIds = [];
                    }else {
                        alert(response.message);
                    }
                }
            );
        }
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //根据父类ID查询
    $scope.findByParentId = function (id) {
        itemCatService.findByParentId(id).success(
            function (response) {
                $scope.list = response;
            }
        )
    }
    //设置等级

    $scope.grade = 1;
    $scope.setGrade = function (num) {
        $scope.grade = num;
    }
    $scope.setEntity = function (entity) {
        if ($scope.grade === 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 2) {
            $scope.entity_1 = entity;
            $scope.entity_2 = null;
        }
        if ($scope.grade == 3) {
            $scope.entity_2 = entity;
        }
    }
    $scope.typeTemplateList={data:[]};
    //查询模版列表
    $scope.findTypeTemplateList = function () {
        typeTemplateService.findTypeTemplateList().success(
            function (response) {
                $scope.typeTemplateList.data = response;
            }
        )
    }

});	
