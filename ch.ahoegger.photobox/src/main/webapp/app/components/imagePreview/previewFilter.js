(function(angular) {
  'use strict';
  angular.module('modulePictureboxComponents').filter('imageLinkFilter', ImageLinkFilter);

  function ImageLinkFilter($window) {

    return function(input, resolution) {
      if(!input){
        return;
      }
      var windowWidth = $window.innerWidth;
      var previewLink;
      input.some(function(link){
        if(link.rel === resolution){
          previewLink = link.url;
          return true;
        }
      })

      console.log('screen with = '+windowWidth);
      return previewLink;
    };
  }
  ImageLinkFilter.$inject = [ '$window' ];
  
})(angular);