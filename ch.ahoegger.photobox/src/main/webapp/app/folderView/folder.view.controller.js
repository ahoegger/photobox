(function(angular) {

  'use strict';

  var _controllerName = 'folderview.controller';

  angular.module('module.picturebox.folderview').controller(_controllerName, Controller);

  function Controller($state, $stateParams, resourceService) {
    var self = this;
    console.log('start controller ', _controllerName, $stateParams);

    (function _init() {
      var url = "rest/api/";
      if($stateParams.id){
        url = url +$stateParams.id;
      }
      resourceService.getResources(url).then(function(response) {
        self.title = response.name;
        self.folder = response;
        
//        self.directories = response.children.filter(function(res) {
//          return res.folder;
//        });
//        self.files = response.children.filter(function(res) {
//          return !res.folder;
//        }).map(function(res) {
//          res.path = res.name;
//          return res;
//        });
        console.log(response);
      });

    })();

    // callback
    self.onImageSelection = function(image) {
      console.log('Image selected: ' + image.name);
      // $state.go('folderState',{path:image.path});
      $state.go('picture',{
        folderId: self.folder.id,
        index: self.folder.pictures.indexOf(image)
        
      });
    };
    self.onFolderSelection = function(folder) {
      console.log('Folder selected: ' + folder.name);
      $state.go('folderState', {
        id : folder.id
      });
    };
    self.navigateParent = function(){
      $state.go('folderState', {
        id : self.folder.parent.id
      });
    }
  }

  Controller.$inject = [ '$state', '$stateParams', 'resource.service' ]

})(angular);