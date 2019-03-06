//定义服务层  参数一：服务层名称  参数二：服务层要做的事情
app.service("brandService",function ($http) {
    //查询所有
    this.findAll=function () {
        return $http.get("../brand/findAll.do");
    }
    //分页查询
    this.findPage=function (pageNum,pageSize) {
        return $http.get("../brand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    }

    //条件分页查询
    this.search=function (searchEntity,pageNum,pageSize) {
        return $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);
    }

    //新增
    this.add=function (entity) {
        return $http.post("../brand/add.do",entity);
    }

    //修改
    this.update=function (entity) {
        return $http.post("../brand/update.do",entity);
    }

    //基于id查询
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    }

    //批量删除
    this.dele=function (ids) {
        return  $http.get("../brand/delete.do?ids="+ids);
    }
    //查询品牌列表
    this.findBrandList= function () {
        return $http.get("../brand/findBrandList.do");
    }

});