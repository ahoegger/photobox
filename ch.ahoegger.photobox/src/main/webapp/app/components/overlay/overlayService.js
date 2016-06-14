(function(angular) {

  'use strict';

  angular.module('modulePictureboxComponents').service('overlay.service', OverlayService);

  function OverlayService($document, $window, $compile, $timeout) {
    var idCounter = 0;
    var bodyElement;

    (function _init() {
      bodyElement = $document.find('body').eq(0);
    })();

    function showConfirmation(text, yesCallback, noCallback, scope, classesRaw) {
      var model = {
      text : text,
      yesCallback : yesCallback,
      noCallback : noCallback
      };
      var classes = ['confirmation-overlay'];
      if (Array.isArray(classesRaw)) {
        classesRaw.forEach(function(clazz) {
          classes.push(clazz);
        });
      }
      return showDialog('app/components/overlay/confirmationTemplate.html', model, scope, classes);
    }

    function showDialog(templateUrl, model, scope, classes) {
      var sourceClasses = [];
      if (Array.isArray(classes)) {
        classes.forEach(function(clazz) {
          sourceClasses.push(clazz);
        });
      }
      return _show(templateUrl, model, scope, true, sourceClasses, false);
    }

    function _show(templateUrl, model, scope, isDialog, classes, closeOnGlasspaneClick) {
      var id = _nextId();
      var $overlayContainer = angular.element($document.find('#overlay-container'));

      var htmlSource = _buildHtml(templateUrl, isDialog, id, classes);
      var localScope = scope.$new();
      model.overlayId = id;
      localScope.model = model;
      $overlayContainer.append($compile(htmlSource)(localScope));
      var ngPopupElement = angular.element($overlayContainer.find('#' + id));

      $timeout(function() {
        var overlayElement = ngPopupElement.find('.overlay-padding')[0];
        var ngOverlayElement = angular.element(overlayElement);

        ngPopupElement.css({
          opacity : 1
        });

      });

      return id;
    }

    function _attachCloseOnScrollListener(ngElement, popupId) {
      if (ngElement) {
        ngElement.bind('scroll', function() {
          close(popupId);
        });
        _attachCloseOnScrollListener(ngElement.parent(), popupId);
      }
    }

    function close(id) {
      var ngPopupElement = angular.element($document.find('#' + id));
      if (ngPopupElement) {
        ngPopupElement.css('opacity', 0);
        $timeout(function() {
          ngPopupElement.remove();
        }, 180);

      }
    }

    function _nextId() {
      var nextId = 'photobox-overlay-id-' + idCounter++;
      return nextId;
    }

    function _buildHtml(templateUrl, isDialog, id, classes) {
      var source = '<photobox-overlay id="' + id + '" class="' + classes.join(' ') + '" template-url="' + templateUrl + '"></photobox-overlay>';
      return source;
    }

    return {
    showConfirmation : showConfirmation,
    showDialog : showDialog,
    close : close
    };
  }

  OverlayService.$inject = [ '$document', '$window', '$compile', '$timeout' ];
})(angular);