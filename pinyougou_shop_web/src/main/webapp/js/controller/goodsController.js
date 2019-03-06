//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, itemCatService, typeTemplateService, uploadService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            $scope.entity.goodsDesc.introduction = editor.html();
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.entity = {};
                    editor.html('');
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    $scope.entity = {
        goods: {
            isEnableSpec: "1"
        },
        goodsDesc: {
            itemImages: [],
            customAttributeItems: [],
            specificationItems: []
        },
        items: []
    }

    // 查询商品分类列表
    //category1
    $scope.findItemCatList = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCatList1 = response;
            }
        )
    };
    //category2
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList2 = response;
                $scope.itemCatList3 = [];
            }
        )
    });
    //category3
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCatList3 = response;
            }
        )
    });
    //模版ID
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        )

    });
    //模版ID查询品牌列表
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.brandList = JSON.parse(response.brandIds);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
            }
        );
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList = response;
            }
        )
    });
    $scope.imageEntity = {};
    //上传文件
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.imageEntity.url = response.message;
                } else {
                    alter(response.message);
                }
            }
        )
    }


    //保存图片
    $scope.saveImage = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.imageEntity);
    }
    //删除图片
    $scope.removeImage = function ($index) {
        $scope.entity.goodsDesc.itemImages.splice($index, 1);
    }

    //绑定specificationItems 列表
    $scope.updateSpecificationItems = function ($event, specName, specOption) {
        let obj = $scope.searchSameValue($scope.entity.goodsDesc.specificationItems, 'attributeName', specName);
        if (obj == null) {
            $scope.entity.goodsDesc.specificationItems.push({
                'attributeName': specName,
                'attributeValue': [specOption]
            });
        } else {
            if ($event.target.checked) {
                obj.attributeValue.push(specOption);
            } else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(specOption), 1);
                if (obj.attributeValue.length === 0) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj), 1);
                }
            }
        }
    }

    //创建sku
    $scope.createItems = function () {
        $scope.entity.items = [{spec: {}, price: 0, num: 99999, status: "1", isDefault: "0"}];
        let specificationItems = $scope.entity.goodsDesc.specificationItems;
        if(specificationItems.length===0){
            $scope.entity.items = [];
        }
        for (let i = 0; i < specificationItems.length; i++) {
            $scope.entity.items = addItem($scope.entity.items, specificationItems[i].attributeName, specificationItems[i].attributeValue);
        }
    }
    let addItem = function (list, attributeName, attributeValue) {
        let newList = [];
        for (let j = 0; j < list.length; j++) {
            let oldItem = list[j];
            for (let k = 0; k < attributeValue.length; k++) {
                let newItem = JSON.parse(JSON.stringify(oldItem));
                newItem.spec[attributeName] = attributeValue[k];
                newList.push(newItem);
            }
        }
        return newList;
    }

    //跳转新增页面
    $scope.toAddGoods = function () {
        location.href = "good_edit.html";
    }
    $scope.allItemCatList = [];
    //查询所有商品分类列表
    $scope.findAllItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (let i = 0; i < response.length; i++) {
                    $scope.allItemCatList[response[i].id] = response[i].name;
                }
            }
        )
    }
    $scope.auditStatusList = ['未申请', '已申请', '已审核', '已驳回'];
    //提交审核
    $scope.setAuditStatus = function (status) {
        goodsService.setAuditStatus($scope.selectIds, status).success(
            function (response) {
                if (response.success) {
                    alert(response.message);
                    $scope.reloadList();
                    $scope.selectIds = [];
                } else {
                    alert(response.message);
                }
            }
        )
    }
    $scope.isMarketableList = ['下架','上架'];
    //商品上下架
    $scope.setIsMarketable = function (isMarketable) {
        goodsService.setIsMarketable($scope.selectIds, isMarketable).success(
            function (response) {
                if (response.success) {
                    alert(response.message);
                    $scope.reloadList();
                    $scope.selectIds = [];
                } else {
                    alert(response.message);
                }
            }
        )
    }


});
