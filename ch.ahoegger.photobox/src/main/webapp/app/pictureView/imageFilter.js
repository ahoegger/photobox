(function(angular) {
  'use strict';

  angular.module('module.picturebox.pictureview').filter('photoboxImageFilter', ImageFilter);

  function ImageFilter() {

    return function(images, controller) {
      if(!images){
        return [];
      }
      return images.map(function(imgData){
        return {
          imageData : imgData,
          hide : function(){
            controller.hide(imageData);
          },
          rotateLeft : function(){
            controller.rotateLeft(imgData);
          },
          rotateRight : function(){
            controller.rotateRight(imgData);
          }          
        }
      });
    };
  }

})(angular); 