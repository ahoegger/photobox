(function(angular) {

  'use strict';

  var _controllerName = 'folderview.controller';

  angular.module('module.picturebox.folderview').controller(_controllerName, Controller);

  function Controller($document, $state, $stateParams, smoothScroll, $timeout, resourceService) {
    var self = this;
    console.log('start controller ', _controllerName, $stateParams);

    (function _init() {
      var url = "rest/api/";
      if ($stateParams.id) {
        url = url + $stateParams.id;
      }

      resourceService.getResources(url).then(function(response) {
        self.title = response.name;
        self.folder = response;
        if ($stateParams.itemId) {
          $timeout(function() {
            _highlight($stateParams.itemId);
          });

        }
        // self.directories = response.children.filter(function(res) {
        // return res.folder;
        // });
        // self.files = response.children.filter(function(res) {
        // return !res.folder;
        // }).map(function(res) {
        // res.path = res.name;
        // return res;
        // });
        console.log(response);
      });

    })();

    function _highlight(itemId) {
      var $highlightedElement = $document.find('#photobox-id-' + itemId);
      if($highlightedElement){
        var options = {
            duration: 700,
            easing: 'easeInQuad',
            offset: 100,
            callbackBefore: function(element) {
                console.log('about to scroll to element', element);
            },
            callbackAfter: function(element) {
                console.log('scrolled to element', element);
            }
        }

        smoothScroll($highlightedElement[0], options);

        $highlightedElement.addClass('highlight');
      }


    }

    // callback
    self.onImageSelection = function(image) {
      console.log('Image selected: ' + image.name);
      // $state.go('folderState',{path:image.path});
      $state.go('picture', {
      folderId : self.folder.id,
      index : self.folder.pictures.indexOf(image)

      });
    };
    self.onFolderSelection = function(folder) {
      console.log('Folder selected: ' + folder.name);
      $state.go('folderState', {
        id : folder.id,
        itemId : undefined
      });
    };
    self.navigateParent = function() {
      $state.go('folderState', {
        id : self.folder.parent.id,
        itemId : self.folder.id
        
      });
    }
  }

  Controller.$inject = [ '$document', '$state', '$stateParams', 'smoothScroll', '$timeout', 'resource.service' ]

})(angular);