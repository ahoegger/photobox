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
      var img = self.folder.pictures[self.index];
      var updateImg = {
          id : img.id,
          rotation: self.folder.pictures[self.index].rotation - 90
      };
      img.rotation = updateImg.rotation;
      resourceService.postImage(updateImg);
    };
    self.handleRotateRight = function(){
      var img = self.folder.pictures[self.index];
      var updateImg = {
          id : img.id,
          rotation: img.rotation + 90
      };
      img.rotation = updateImg.rotation;
      resourceService.postImage(updateImg);
    };
    self.handleHide= function(){
      var img = self.folder.pictures[self.index];
      var updateImg = {
          id : img.id, 
          active : false
      };
      self.folder.pictures = self.folder.pictures.filter(function(picture, index){
        return picture.id !== img.id;
      });
      resourceService.postImage(updateImg);
    };
    

  }
  
  Controller.$inject = [ '$state', '$stateParams', '$location','resource.service' ]
})(angular);