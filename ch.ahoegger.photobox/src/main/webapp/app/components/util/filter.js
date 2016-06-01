(function(angular) {
  'use strict';
  angular.module('modulePictureboxComponents').filter('imageSizeFilter', ImageLinkFilter);

  function ImageLinkFilter($window) {

    return function(imageId, compBounds) {
      if(!imageId || !compBounds){
        return;
      }
      
      var w = compBounds.width;
      var h = compBounds.height;
      console.log('filter w:'+w+' h:'+h);
      var edgeLength;
      if(w > h){
        edgeLength = Math.min(4*h/3, w);
      }else{
        edgeLength  = Math.min(4*w/3, h);
      }
      
      var link = 'rest/picture/'+imageId;
      if(edgeLength  < 400){
        link += '?size=s';
      }
      else if(edgeLength  < 800){
        link += '?size=m';
      }else{
        link += '?size=l';
      }
      return link;
    };
  }
  ImageLinkFilter.$inject = [ '$window' ];
  
})(angular);