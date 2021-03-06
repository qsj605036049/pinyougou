//父controller定义
app.controller("baseController",function ($scope) {
    //分页配置
    $scope.paginationConf = {
        currentPage:1,  				//当前页
        totalItems:10,					//总记录数
        itemsPerPage:10,				//每页记录数
        perPageOptions:[10,20,30,40,50], //分页选项，下拉选择一页多少条记录
        onChange:function(){			//页面变更后触发的方法
            $scope.reloadList();		//启动就会调用分页组件
        }
    };

    $scope.reloadList=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    //记录品牌列表勾选的id数组
    $scope.selectIds=[];

    //数据列表复选框勾选和取消勾选组装数据
    $scope.updateSelection=function ($event,id) {
        //判断复选框选中状态  $event.target事件源对象 其实就是复选框对象
        if($event.target.checked){
            //选中状态
            $scope.selectIds.push(id);
        }else{
            //取消勾选，移除取消勾选id值
            var index= $scope.selectIds.indexOf(id);
            ////参数一：移除位置的元素的索引值  参数二：从该位置移除几个元素
            $scope.selectIds.splice(index,1);
        }
    }

    //复选框是否勾选的方法
    $scope.isSelected=function (id) {
        //alert($scope.selectIds.indexOf(id))
        if($scope.selectIds.indexOf(id)!=-1){
            return true;
        }else {
            return false;
        }
    }

    //解析json格式字符串，获取json数组中json对象的属性值，做字符串拼接操作
    $scope.parseJSONString=function (jsonString,key) {

        var value="";

        //解析json格式字符串
        //[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        var jsonArr = JSON.parse(jsonString);
        for(var i=0;i<jsonArr.length;i++){
            var jsonObj =jsonArr[i];

            if(i>0){
                //基于json对象属性名或者属性值的方式
                //方式一  json对象.属性名 或者 json对象[变量名] 注意：属性名是确定值
                //方式二 注意：当属性名是变量时，获取方式 json对象[变量名]
                value+=","+jsonObj[key];
            }else {
                value+=jsonObj[key];
            }
        }
        return value;

    }


})