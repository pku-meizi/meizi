angular.module('hello', [ 'ngRoute' ]).config(function($routeProvider, $httpProvider) {

	$routeProvider.when('/', {
		templateUrl : 'home.html',
		controller : 'home',
		controllerAs: 'controller'
	}).when('/login', {
		templateUrl : 'signin.html',
		controller : 'navigation',
		controllerAs: 'controller'
	}).when("/indent",{
		templateUrl : 'indent.html',
		controller : 'indent',
		controllerAs:'controller',
	}).when("/end",{
		templateUrl : 'end.html',
		controller : 'end',
		controllerAs:'controller',
	}).otherwise('/');

	$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

	//$httpProvider.default.headers.post = {
	//	'Content-Type': 'application/json'
	//}

}).controller('navigation',

		function($rootScope, $http, $location, $route) {
			
			var self = this;

			self.tab = function(route) {
				return $route.current && route === $route.current.controller;
			};

			var authenticate = function(credentials, callback) {

                var data = credentials ? {
                    "username": credentials.username,
                    "password": credentials.password
                } : {
					"username": "",
					"password": ""
				};

				$http.get('authenticate', {
					params : data
				}).then(function(response) {
					if (response.data.status == true) {
						$rootScope.authenticated = true;
					} else {
						$rootScope.authenticated = false;
					}
					callback && callback($rootScope.authenticated);
				}, function() {
					$rootScope.authenticated = false;
					callback && callback(false);
				});

			}

			self.credentials = {};

			authenticate(self.credentials, function(authenticated){
				if(authenticated){
					console.log("Login succeeded")
					$location.path("/");
					$rootScope.authenticated = true;
				}
			});


			self.login = function() {
				authenticate(self.credentials, function(authenticated) {
					if (authenticated) {
						console.log("Login succeeded")
						$location.path("/");
						flag = true;
						self.error = false;
						$rootScope.authenticated = true;
					} else {
                        alert("Username or password error, please try again.")
						console.log("Login failed")
						$location.path("/login");
						flag = false;
						self.error = true;
						$rootScope.authenticated = false;
					}
				})
			};

			self.logout = function() {
				$http.post('logout', {}).finally(function() {
					$rootScope.authenticated = false;
					$location.path("/");
				});
			}

		}).controller('home', function($http, $rootScope) {
			var self = this;

		}).controller('indent', function($http, $rootScope){

		}).controller('end', function($http, $rootScope){
			//alert("move to end.html");
});

