/**
 * Created by jpmartinez on 08/05/16.
 */
(function () {
    'use strict';
    angular.module('usbus').controller('RegisterController', RegisterController);
    RegisterController.$inject = ['RegisterTenantResource','RegisterUserResource', '$scope', '$mdDialog', '$location', '$window', 'localStorage'];
    /* @ngInject */
    function RegisterController(RegisterTenantResource,RegisterUserResource, $scope, $mdDialog, $location, $window, localStorage) {
        $scope.register = register;
        $scope.showAlert = showAlert;
        $scope.validate = validate;
        $scope.isValid = true;
        $scope.user = {};
        $scope.user.password = '';
        $scope.passwordVerification = '';

        localStorage.clear();

        localStorage.setData('showMenu', false);

        function register(tenant,user) {
            var ok = true;
            tenant.tenantId = 0;
            user.active = true;

            if ($scope.gender == 0) {
                user.gender = 'MALE'
            }
            else if ($scope.gender == 1) {
                user.gender = 'FEMALE';
            }
            else {
                user.gender = 'OTHER';
            }

            $scope.user.password = user.password;

            RegisterTenantResource.save(tenant,function (resp) {
                ok = true;
                console.log(resp);
				localStorage.setData('tenantId', tenant.tenantId);
            }, function (error) {
                ok = false;
                console.log(error);
                showAlert('Error!', 'Ocurrió un error al registrar el TENANT');

            } );
            if (ok){
                RegisterUserResource.save(user,function (respU) {
                    var token = respU;
                    console.log(token);
                    localStorage.setData('token', token);
                    showAlert('Exito!', 'Se ha creado su empresa virtual de forma exitosa');
					localStorage.setData('userName', user.username);
                    localStorage.setData('tenantName', tenant.name);
                    $window.location.href = $location.$$absUrl + 'tenant/' + tenant.name;
                },function (error) {
                    console.log(error);
					localStorage.setData('tenantId', '');
					localStorage.setData('userName', '');
                    showAlert('Error!', 'Ocurrió un error al registrar el USUARIO');
                });
            }
        }

        function showAlert(title,content) {
            $mdDialog
                .show($mdDialog
                    .alert()
                    .parent(
                        angular.element(document
                            .querySelector('#popupContainer')))
                    .clickOutsideToClose(true)
                    .title(title)
                    .content(content)
                    .ariaLabel('Alert Dialog Demo').ok('Cerrar'));

        };

        function validate() {
            $scope.isValid = $scope.user.password == $scope.passwordVerification;
        }
    }
})();
