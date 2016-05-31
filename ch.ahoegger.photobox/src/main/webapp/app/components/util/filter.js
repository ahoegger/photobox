(function(angular) {
  'use strict';
  angular.module('modulePictureboxComponents').filter('imageSizeFilter', ImageLinkFilter);

  function ImageLinkFilter($window) {

    return function(imageId, compWidth) {
      if(!imageId){
        return;
      }
      var link = 'rest/picture/'+imageId;
      if(compWidth < 400){
        link += '?size=s';
      }
      else if(compWidth < 800){
        link += '?size=m';
      }else{
        link += '?size=l';
      }
      return link;
    };
  }
  ImageLinkFilter.$inject = [ '$window' ];
  
})(angular);