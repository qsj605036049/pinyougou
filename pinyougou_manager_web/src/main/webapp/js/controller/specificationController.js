app.controller("specificationController", function ($scope, $controller, specificationService) {
    $controller("baseController", {$scope: $scope});

    $scope.searchEntity = {};
    //条件查询
    $scope.search = function (pageNum, pageSize) {
        specificationService.search($scope.searchEntity, pageNum, pageSize).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;
            })
    }
    $scope.entity = {
        specification: {},
        specificationOptions: []
    }
    // 保存规格
    $scope.save = function () {
        var method = null;
        if ($scope.entity.specification.id != null) {
            method = specificationService.update($scope.entity);
        } else {
            method = specificationService.add($scope.entity);
        }
        method.success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                } else {
                    alter(response.message);
                }
            })
    }
    //添加规格选项
    $scope.addSpecificationOptionsRows = function () {
        $scope.entity.specificationOptions.push({});
    }

    //删除规格选项
    $scope.deleSpecificationOptionsRows = function (index) {
        $scope.entity.specificationOptions.splice(index, 1);
    }
    // 根据ID查询
    $scope.findOneById = function (id) {
        specificationService.findOneById(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    }
    //删除规格
    $scope.dele = function () {
        if (confirm("是否删除")) {
            specificationService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();
                    } else {
                        alter(response.message);
                    }
                }
            )

        }

    }
});