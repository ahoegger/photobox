(function(angular) {

  'use strict';

  var _controllerName = 'pictureview.controller';

  angular.module('module.picturebox.pictureview').controller(_controllerName, Controller);

  function Controller($scope, $state, $stateParams, $location, $filter, resourceService, overlayService) {
    var self = this;
    self.files = undefined;

    (function _init() {
      self.actions = {
      getFullScreen : function() {
        return self.fullScreen;
      },
      setFullScreen : function(fullScreen) {
        self.fullScreen = fullScreen;
      },
      setCurrentIndex : function(index){
        self.index = index;
        if (angular.isDefined(index)) {
          $location.search('index', index);
        }
      }
      }

      self.fullScreen = false;

      self.index = $stateParams.index;
      var url = "rest/api/";
      if (self.folderPath) {
        url = url + '?path=' + self.folderPath;
      }

      resourceService.getFolder($stateParams.folderId).then(function(response) {
        self.folder = response;
        self.images = $filter('photoboxImageFilter')(self.folder.pictures, {
        hide : _hide,
        rotateLeft : _rotateLeft,
        rotateRight : _rotateRight
        });
      });

    })();

    function _hide(image) {
      var overlayId;

      var yesCallback = function() {
        overlayService.close(overlayId);
        var updateImg = {
        id : image.id,
        active : false
        };
        self.images = self.images.filter(function(file, index) {
          return file.imageData.id !== image.id;
        });
        resourceService.postImage(updateImg);
      };
      var noCallback = function() {
        overlayService.close(overlayId);
      };

      overlayId = overlayService.showConfirmation('Are U sure to hide this photo?', yesCallback, noCallback, $scope, [ 'hide-image-confirmation-overlay' ]);
    }

    function _rotateLeft(image) {
      var updateImg = {
      id : image.id,
      rotation : image.rotation - 90
      };
      image.rotation = updateImg.rotation;
      resourceService.postImage(updateImg);
    }
    function _rotateRight(image) {
      var updateImg = {
      id : image.id,
      rotation : image.rotation + 90
      };
      image.rotation = updateImg.rotation;
      resourceService.postImage(updateImg);
    }

    // callback
    self.navigateFolder = function() {
      $state.go('folderState', {
      id : self.folder.id,
      itemId : self.folder.pictures[self.index].id
      });
    };

    self.handleRotateLeft = function() {
      var img = self.folder.pictures[self.index];
      _rotateLeft(img);
    };
    self.handleRotateRight = function() {
      var img = self.folder.pictures[self.index];
      _rotateRight(img);
    };
    self.handleHide = function() {
      var img = self.folder.pictures[self.index];
      _hide(img);
    };

  }

  Controller.$inject = [ '$scope', '$state', '$stateParams', '$location', '$filter', 'resource.service', 'overlay.service' ]
})(angular);