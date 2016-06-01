(function () {
    'use strict';
    angular
        .module('usbus')
        .config(config);
    config.$inject = ['$routeProvider'];
    /* @ngInject */
    function config($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'templates/register.html',
                controller: 'RegisterController'
            })
            .when('/tenant/:tenantName', {
                templateUrl: 'templates/home.html',
                controller: 'HomeController'
            })
			.when('/tenant/:tenantName/admin', {
                templateUrl: 'templates/admin.html',
                controller: 'HomeController'
            })
			.when('/tenant/:tenantName/tickets', {
                templateUrl: 'templates/tickets.html',
                controller: 'HomeController'
            })
			.when('/tenant/:tenantName/boxes', {
                templateUrl: 'templates/boxes.html',
                controller: 'HomeController'
            })
			.when('/tenant/:tenantName/workshop', {
                templateUrl: 'templates/workshop.html',
                controller: 'HomeController'
            })
			.when('/tenant/:tenantName/accountant', {
                templateUrl: 'templates/accountant.html',
                controller: 'HomeController'
            })
			.when('/tenant/:tenantName/humanResources', {
                templateUrl: 'templates/humanResources.html',
                controller: 'HomeController'
            })
            .otherwise({
                redirectTo: '/'
            });
    };
})();
