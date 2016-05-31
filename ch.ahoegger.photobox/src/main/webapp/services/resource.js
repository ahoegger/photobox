/**
 * Service for article functionality
 * @class angular_services.ArticleServiceModule
 * @memberOf angular_services
 * @author Andy Hoegger
 * @since 15.09.2014
 */
var modServicesResource = angular.module('module.picturebox');

/**
 * @description Service factory for interaction with the backend with regards to articles, comments, etc.
 * @function ArticleService
 * @memberOf angular_services.ArticleServiceModule
 */
modServicesResource.factory('resourceService', ['$http',
    function ($http) {
        var GLOBAL_TIMEOUT = 5000;
        var GLOBAL_JSON_TYPE = 'application/json';
        
        var getResourcesInternal = function(path){
          var url = "rest/resources/";
          if(path){
            url = url +path;
          }
          console.log('call resources for '+url+'.');
          return $http.get(url,
              {
                  headers: {'Accept': GLOBAL_JSON_TYPE},
                  timeout: GLOBAL_TIMEOUT
              }
          );
        };

        return {
            getResources: getResourcesInternal
        };
    }]);