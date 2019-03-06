app.service('uploadService',function ($http) {
   this.uploadFile = function () {
       var formData = new FormData();
       formData.append('file',file.files[0]);
       return $http({
          method:'post',
          data:formData,
          url:'../upload/uploadFile.do',
           // 文件格式默认json
           headers:{'Content-type':undefined},
           // 对表单二进制序列化
           transformRequest:angular.identity
       });
   }
});