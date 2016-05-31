(function(angular) {

  'use strict';

  var _controllerName = 'pictureview.controller';

  angular.module('module.picturebox.pictureview').controller(_controllerName, Controller);

  function Controller($state, $stateParams,$location, resourceService) {
    var self = this;
    self.files = undefined;
    console.log('start controller ', _controllerName, $stateParams);
    console.log('folderX: ', $stateParams.folderX);

    (function _init() {
      self.fullScreen = false;

      self.index = $stateParams.index;
      var url = "rest/api/";
      if(self.folderPath){
        url = url +'?path='+self.folderPath;
      }
      resourceService.getFolder($stateParams.folderId).then(function(response) {
        self.folder = response;
        self.image = self.folder.pictures[self.index];
//        self.hasParent = response.path || false;
//        self.parentFolderText = response.path;
        
//        self.files = response.children.filter(function(res) {
//          return !res.folder;
//        }).map(function(res) {
//          return res;
//        });
        
//        self.image = self.files[self.index];
        
      });
     
    
    })();
    
    // callback
    self.navigateFolder = function(){
      $state.go('folderState', {
        id : self.folder.id
      });
    };
    self.handleImgClick = function(){
      console.log('toggle fullscreen');
      self.fullScreen = !self.fullScreen;
    };
    self.handleIndexUpdated = function(index){
      console.log('index updated!',index);
      self.index = index;
      self.image = self.folder.pictures[self.index];
      $location.search('index', index)
    };
    self.handleRotateLeft = function(){
      self.folder.pictures[self.index].rotation -= 90;
    };
    self.handleRotateRight = function(){
      self.folder.pictures[self.index].rotation +=90;
      // TODO backend call
    };
    self.handleHide= function(){
      
    };
  }
  
  Controller.$inject = [ '$state', '$stateParams', '$location','resource.service' ]
})(angular);