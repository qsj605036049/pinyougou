app.service('specificationService',function ($http) {
    // 条件查询
   this.search=function (entity,pageNum,pageSize) {
       return $http.post('../specification/search.do?pageNum='+pageNum+'&pageSize='+pageSize,entity);
   }
    //增加规格
   this.add= function (entity) {
       return $http.post('../specification/add.do',entity);
   }
   // 根据ID查询
    this.findOneById= function (id) {
        return $http.get('../specification/findOneById.do?id='+id);
    }
    //修改规格
    this.update=function (entity) {
        return  $http.post('../specification/update.do',entity);
    }
    //删除规格
    this.dele=function (ids) {
        return  $http.get('../specification/delete.do?ids='+ids);
    }
    //查询规格列表
    this.findSpecList=function () {
        return $http.get('../specification/findSpecList.do');
    }
});