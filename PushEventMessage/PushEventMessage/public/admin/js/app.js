var app = angular.module("PushAdmin", ['ui.router', 'ngTagsInput', 'angularFileUpload', 'angular-click-outside','ngSanitize']);

var ADMIN_SERVICE = APP_DOMAIN + "/admin-service";
console.log(APP_DOMAIN);

var Events = {
    invalidRequest: "invalidRequest",
    logginRequired: "logginRequired",
    loggedIn: "loggedIn",
    loggedOut: "loggedOut",
    noConnection: "noConnection",
    backendError: "backendError"
};

function showNoti($rootScope, $timeout, msg) {
    console.log("showNoti : " + msg);

    $rootScope.noti.push({
        msg: msg
    });
    
    $timeout(function () {
        $rootScope.noti.shift();
    }, 3000);
}

function getMaxPageNumber(count, size) {
    if (size === 0) {
        return 0;
    }
    var maxPageNumber = Math.ceil(count / size);
    return maxPageNumber;
}

app.config(function ($interpolateProvider, $httpProvider) {
    $interpolateProvider.startSymbol('[[').endSymbol(']]');
    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=utf-8';
    $httpProvider.defaults.transformRequest = function (obj) {
        var str = [];
        for (var p in obj) {
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        }
        return str.join("&");
    };
});

app.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise("/dashboard")
    $stateProvider
            .state('dashboard', {
                url: "/dashboard",
                templateUrl: APP_DOMAIN + "/public/admin/html/dashboard.html"
            })
            .state('login', {
                url: "/login",
                templateUrl: APP_DOMAIN + "/public/admin/html/login.html"
            })
            .state('test', {
                url: "/test",
                templateUrl: APP_DOMAIN + "/public/admin/html/test.html"
            })
            .state('push', {
                url: "/push/{id:int}",
                templateUrl: APP_DOMAIN + "/public/admin/html/push.html",
                controller: function ($scope, $stateParams, $rootScope) {
                    $scope.params = $stateParams;
                    if ($rootScope.selectedApp && $rootScope.selectedApp.id == $stateParams.id) {
                    } else {
                        $rootScope.selectedApp = $stateParams;
                    }
                    console.log($rootScope.selectedApp);
                }
            })
            .state('appInfo', {
                url: "/appInfo/{id:int}",
                templateUrl: APP_DOMAIN + "/public/admin/html/appInfo.html",
                controller: function ($scope, $stateParams, $rootScope) {
                    $scope.params = $stateParams;
                    if ($rootScope.selectedApp && $rootScope.selectedApp.id == $stateParams.id) {
                    } else {
                        $rootScope.selectedApp = $stateParams;
                    }
                    console.log($rootScope.selectedApp);
                }
            })
            .state('setting', {
                url: "/setting/{id}",
                templateUrl: APP_DOMAIN + "/public/admin/html/setting.html",
                controller: function ($scope, $stateParams, $rootScope) {
                    console.log($rootScope.selectedApp);
                    $scope.params = $stateParams;
                    if ($rootScope.selectedApp && $rootScope.selectedApp.id == $stateParams.id) {
                    } else {
                        $rootScope.selectedApp = $stateParams;
                    }
                    console.log($rootScope.selectedApp);
                }
            });
});

app.filter('highlight', function($sce) {
    return function(text, phrase) {
      if (phrase) text = text.replace(new RegExp('('+phrase+')', 'gi'),
        '<span class="highlighted">$1</span>')

      return $sce.trustAsHtml(text)
    }
  });


app.factory("eventbus", function () {

    return {
        _bus: {},
        on: function (busName, handler) {
            if (!this._bus[busName]) {
                this._bus[busName] = [];
            }
            this._bus[busName].push(handler);
        },
        fire: function (busName, data) {
            if (this._bus[busName]) {
                var listeners = this._bus[busName];
                listeners.forEach(function (e, i) {
                    e(data);
                });
            }
        }
    };

});

app.factory("push", function ($http, eventbus) {
    function checkLowCode(r) {
        if (r.status === -1) {
            eventbus.fire(Events.noConnection);
            return;
        } else if (r.status === 200) {
            switch (r.data.code) {
                case -1:
                    eventbus.fire(Events.invalidRequest, r.data.desc);
                    break;
                case -2:
                    eventbus.fire(Events.logginRequired);
                    break;
                case -13:
                    eventbus.fire(Events.backendError);
                    break;
            }
        }
    }
    return {
        login: function (userId, pass, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
                if (r.status === 200 && r.data.code === 0) {
                    eventbus.fire(Events.loggedIn);
                }
            }
            $http({
                url: ADMIN_SERVICE + "/login",
                method: "POST",
                data: {
                    userId: userId,
                    password: pass
                }
            }).then(handler, handler);
        },
        logout: function (callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
                if (r.status === 200 && r.data.code === 0) {
                    eventbus.fire(Events.loggedOut);
                }
            }
            $http({
                url: ADMIN_SERVICE + "/logout",
                method: "POST",
                data: {
                }
            }).then(handler, handler);
        },
        createPush: function (appId, platform, filter, time, title, msg, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/createPush",
                method: "POST",
                data: {
                    appId: appId,
                    platform: platform,
                    filter: JSON.stringify(filter),
                    time: time,
                    title: title,
                    msg: msg
                }
            }).then(handler, handler);
        },
        deleteSchedule: function (taskId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/deleteSchedule",
                method: "POST",
                data: {
                    taskId: taskId
                }
            }).then(handler, handler);
        },
        countDevice: function (appId, platform, filter, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/countDevice",
                method: "POST",
                data: {
                    appId: appId,
                    platform: platform,
                    filter: JSON.stringify(filter)
                }
            }).then(handler, handler);
        },
        getProfile: function (callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getProfile",
                method: "GET",
                params: {
                }
            }).then(handler, handler);
        },
        getApps: function (callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getApps",
                method: "GET",
                params: {
                }
            }).then(handler, handler);
        },
        getDevicePage: function (appId, platform, offset, size, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getDevicePage",
                method: "GET",
                params: {
                    appId: appId,
                    platform: platform,
                    offset: offset,
                    size: size
                }
            }).then(handler, handler);
        },
        getScheduledPage: function (appId, offset, size, callback) {

            function handler(r) {
                callback(r);
                checkLowCode(r);
            }

            $http({
                url: ADMIN_SERVICE + "/getScheduledPage",
                method: "GET",
                params: {
                    appId: appId,
                    offset: offset,
                    size: size
                }
            }).then(handler, handler);
        },
        getScheduledCount: function (appId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getScheduledCount",
                method: "GET",
                params: {
                    appId: appId
                }
            }).then(handler, handler);
        },
        getHistoryPage: function (appId, offset, size, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getHistoryPage",
                method: "GET",
                params: {
                    appId: appId,
                    offset: offset,
                    size: size
                }
            }).then(handler, handler);
        },
        getHistoryCount: function (appId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getHistoryCount",
                method: "GET",
                params: {
                    appId: appId
                }
            }).then(handler, handler);
        },
        getPush: function (appId, requestId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getPush",
                method: "GET",
                params: {
                    appId: appId,
                    requestId: requestId
                }
            }).then(handler, handler);
        },
        getApp: function (appId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getApp",
                method: "GET",
                params: {
                    appId: appId
                }
            }).then(handler, handler);
        },
        getGcmKey: function (appId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getGcmKey",
                method: "GET",
                params: {
                    appId: appId
                }
            }).then(handler, handler);
        },
        getApnsKey: function (appId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/getApnsKey",
                method: "GET",
                params: {
                    appId: appId
                }
            }).then(handler, handler);
        },
        hasApnsSslKey: function (appId, callback) {
            function handler(r) {
                callback(r);
                checkLowCode(r);
            }
            $http({
                url: ADMIN_SERVICE + "/hasApnsSslKey",
                method: "GET",
                params: {
                    appId: appId
                }
            }).then(handler, handler);
        }
    };
});// end factory push



app.controller("main", function ($scope, eventbus, push, $state, $rootScope, $timeout) {

    $scope.appss = [];

    function reload() {
        push.getApps(function (r) {
            if (r.data && r.data.code === 0) {
                $scope.appss = r.data.value;
//                $scope.$apply();
            }
        });
    }
    reload();

    $rootScope.dropdown = {
        active : false
    };

    $rootScope.change = function(){
        $rootScope.dropdown.active = !$rootScope.dropdown.active;
    };

    $rootScope.noti = [];
    $scope.showUserMenu = false;
    $scope.user = null;
    $scope.app_domain = APP_DOMAIN;
    $scope.logoutUrl = LOGOUT_URL;
    function loadProfile() {
        push.getProfile(function (r) {
            if (r.data && r.data.code === 0) {
                console.log("Logged in");
                $rootScope.user = r.data.value;
                console.log("user : " + $rootScope.user.name);
            }
        });
    }
    eventbus.on(Events.loggedIn, function (data) {
        loadProfile();
    });
    eventbus.on(Events.loggedOut, function (data) {
        $scope.user = null;
    });
    eventbus.on(Events.logginRequired, function (data) {
        window.location.href = APP_DOMAIN + "/admin/login";
//        $scope.user = null;
//        $state.go("login");
    });
    eventbus.on(Events.noConnection, function () {
        showNoti($rootScope, $timeout, "No connection!");
    });
    eventbus.on(Events.backendError, function () {
        showNoti($rootScope, $timeout, "Got a Backend Error");
    });
    eventbus.on(Events.invalidRequest, function (desc) {
        showNoti($rootScope, $timeout, desc);
    });
    loadProfile();

});

app.controller("login", function ($scope, push, $state) {
    $scope.login = function () {
        push.login("123", "123", function (r) {
            if (r.data && r.data.code === 0) {
                $state.go("dashboard");
            }
        });
    };
});

app.controller("dashboard", function ($scope, push, eventbus, $state) {
//     $scope.apps = [];

//     function reload() {
//         push.getApps(function (r) {
//             if (r.data && r.data.code === 0) {
//                 $scope.apps = r.data.value;
// //                $scope.$apply();
//             }
//         });
//     }
//     $scope.getApps = function () {
//         console.log("getApps now");
//         reload();
//     };
// //    eventbus.on(Events.loggedIn, function () {
// //        console.log("getApps");
// //        reload();
// //    });

//     reload();
});

app.controller("appInfo", function ($scope, $rootScope, push) {

//    $scope.params= [];
    $scope.tab = 'devices';
    push.getApp($scope.params.id, function (r) {
        if (r.data && r.data.value) {
            $rootScope.selectedApp = r.data.value;
        }
    });
});

app.controller("setting", function ($scope, $rootScope, push, FileUploader) {
    $scope.tab = "gcm";
    $scope.gcm = {
        formChanged: false
    };
    $scope.apns = {
        hasSslKey: true
    };
    $scope.backFN = function(){
         //if it was the first page
        window.history.back();
        $scope.$apply();
    }

    var uploader = $scope.uploader = new FileUploader({
        url: ADMIN_SERVICE + "/setApnsSslFile?appId=" + $scope.params.id,
        onCompleteAll: function () {
            reload();
        }
    });

    function reload() {
        push.getGcmKey($scope.params.id, function (r) {
            if (r.data && r.data.value) {
                $scope.gcm.key = r.data.value;
            }
        });
        push.getApnsKey($scope.params.id, function (r) {
            if (r.data && r.data.value) {
                $scope.apns.key = r.data.value;
            }
        });
        push.hasApnsSslKey($scope.params.id, function (r) {
            if (r.data) {
                $scope.apns.hasSslKey = r.data.value;
            }
        });

        if ($rootScope.selectedApp && $rootScope.selectedApp.id && !$rootScope.selectedApp.name) {
            push.getApp($rootScope.selectedApp.id, function (r) {
                if (r.data && r.data.value) {
                    $rootScope.selectedApp = r.data.value;
                }
            });
        }
    }

    reload();
});

app.controller("createPush", function ($scope, $rootScope, push, $timeout) {
    $scope.PROPERTIES = {
        app_version: "App Version",
        os_version: "OS Version",
        sdk_version: "SDK Version",
        package_name: "Package Name",
        zalo_id: "Zalo Id"
    };
    $scope.data = {
        platform: 'android',
        pushNow: true,
        useFilter: false
    };
    $scope.deviceCount = 0;

    $scope.filter = {};

    $scope.filterOptions = ["app_version", "os_version", "sdk_version", "package_name", "zalo_id"];

    $scope.backFN = function(){
         //if it was the first page
        window.history.back();
        $scope.$apply();
    }
    $scope.addFilter = function (p) {

        var index = $scope.filterOptions.indexOf(p);

        if (index >= 0) {
            $scope.filterOptions.splice(index, 1);
            $scope.filter[p] = [];
            $scope.getDeviceCount();
        }
    };
    
    $scope.removeFilterProperty = function (p) {
        $scope.filterOptions.push(p);
        delete $scope.filter[p];
        $scope.getDeviceCount();
    };

    function reset() {
        $scope.data = {
            platform: 'android',
            pushNow: true,
            useFilter: false
        };
        $scope.filter = {};
        $scope.getDeviceCount();
    }
    function getFilter() {
        var result = {};
        for (var key in $scope.filter) {
            var arr = [];
            for (var x in $scope.filter[key]) {
                arr.push($scope.filter[key][x].text);
            }
            result[key] = {
                $in: arr
            };
        }
        return result;
    }
    $scope.getDeviceCount = function () {
        push.countDevice($rootScope.selectedApp.id, $scope.data.platform, getFilter(), function (r) {
            if (r.data) {
                $scope.deviceCount = r.data.value;
            } else {
                $scope.deviceCount = 0;
            }
        });
    };

    $scope.push = function () {
        if (!$scope.data.msg || $scope.data.msg.length == 0) {
            showNoti($rootScope, $timeout, "Message can't be empty!");
            return;
        }
        //createPush: function (appId, platform, filter, time, msg, callback
        var time = $('#time-to-push').val();
        if ($scope.data.pushNow === true) {
            time = "";
        }

        push.createPush($rootScope.selectedApp.id, $scope.data.platform, getFilter(), time, $scope.data.title, $scope.data.msg, function (r) {
            if (r.data) {
                if (r.data.code === 0) {
                    showNoti($rootScope, $timeout, "Successfully!");
                    reset();
                }
            }
        });

        $scope.filterOptions = ["app_version", "os_version", "sdk_version", "package_name", "zalo_id"];

    };


    $('#time-to-push').datetimepicker({
            inline:true
        });

    if ($rootScope.selectedApp && $rootScope.selectedApp.id && !$rootScope.selectedApp.name) {

        push.getApp($rootScope.selectedApp.id, function (r) {
            if (r.data && r.data.value) {
                $rootScope.selectedApp = r.data.value;
            }
        });

    }

    reset();
});

app.controller("deviceTable", function ($scope, $rootScope, push) {
  
    $scope.dataOption = {
    selectedOption: {id: '10', name: '10'},
    availableOptions: [
      {id: '10', name: '10'},
      {id: '20', name: '20'},
      {id: '30', name: '30'}
    ],
   };

   $scope.exportData = function () {
        var blob = new Blob([document.getElementById('exportable').innerHTML], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        });
        saveAs(blob, "Device.xls");
    };

    $scope.count = 0;
    $scope.size = 10;
    $scope.curPage = 1;
    $scope.devices = [];
    $scope.platform = "android";
    $scope.maxPage = 1;

    $scope.reload = function () {
        $scope.devices = [];
//        $scope.$apply();
        push.getDevicePage($rootScope.selectedApp.id, $scope.platform, $scope.dataOption.selectedOption.id * ($scope.curPage - 1), $scope.dataOption.selectedOption.id, function (r) {
            if (r.data && r.data.value) {
                $scope.devices = r.data.value;
//                $scope.$apply();
                console.log($scope.dataOption.selectedOption.id);
            }
        });

        push.countDevice($rootScope.selectedApp.id, $scope.platform, {}, function (r) {
            if (r.data && r.data.value) {
                $scope.count = r.data.value;
                $scope.maxPage = getMaxPageNumber($scope.count, $scope.dataOption.selectedOption.id);
            }
        });
    };

    $scope.changedValue = function(){
        $scope.curPage = 1;
        $scope.reload();
    }
    $scope.previousPage = function () {
        if ($scope.curPage > 1) {
            $scope.curPage--;
            $scope.reload();
        }
    };
    $scope.nextPage = function () {
        if ($scope.curPage < $scope.maxPage) {
            $scope.curPage++;
            $scope.reload();
        }
    };
    $scope.firstPage = function () {
        if ($scope.curPage !== 1) {
            $scope.curPage = 1;
            $scope.reload();
        }
    };
    $scope.lastPage = function () {
        var lastPageNumber = $scope.maxPage;
        if ($scope.curPage !== lastPageNumber) {
            $scope.curPage = lastPageNumber;
            $scope.reload();
        }
    };
    $scope.reload();
});// end device table

app.controller("scheduledTable", function ($scope, $rootScope, push) {
    $scope.dataOption = {
    selectedOption: {id: '10', name: '10'},
    availableOptions: [
      {id: '10', name: '10'},
      {id: '20', name: '20'},
      {id: '30', name: '30'}
    ],
   };

    $scope.deleteItem = [];

    $scope.deleteListItem = function(){
        for(var x in $scope.deleteItem)
        {
            if($scope.deleteItem[x] != 0 && $scope.deleteItem[x] != null && $scope.deleteItem[x] != undefined && Number.isInteger($scope.deleteItem[x])){
                console.log("delete : " + $scope.deleteItem[x])
                $scope.deleteList($scope.deleteItem[x]);
            }
        }
        reload();
    };

    $scope.checkAll = function(id){
        if($scope.answers[id])
        {
            $scope.deleteItem = [];
            for(var x in $scope.data)
            {
                if($scope.data[x].id != 0 && $scope.data[x].id != null && $scope.data[x].id != undefined && Number.isInteger($scope.data[x].id)){
                    console.log("send : " + $scope.data[x].id)
                    $scope.deleteItem.push($scope.data[x].id);
                }
            }
        }
        else
        {
            $scope.deleteItem = [];
            console.log("send : " + $scope.deleteItem.length);
        }
    }

    $scope.count = 0;
    $scope.size = 10;
    $scope.curPage = 1;
    $scope.data = [];
    $scope.maxPage = 1;

    function reload() {
        $scope.data = [];
       
//        $scope.$apply();
        push.getScheduledPage($rootScope.selectedApp.id, $scope.size * ($scope.curPage - 1), $scope.size, function (r) {
            if (r.data && r.data.value) {
                $scope.data = r.data.value;
//                $scope.$apply();
            }
        });
        push.getScheduledCount($rootScope.selectedApp.id, function (r) {
            if (r.data && r.data.value) {
                $scope.count = r.data.value;
                $scope.maxPage = getMaxPageNumber($scope.count, $scope.size);
            }
        });
    }

    $scope.previousPage = function () {
        if ($scope.curPage > 1) {
            $scope.curPage--;
            reload();
        }
    };
    $scope.nextPage = function () {
        if ($scope.curPage < $scope.maxPage) {
            $scope.curPage++;
            reload();
        }
    };
    $scope.firstPage = function () {
        if ($scope.curPage !== 1) {
            $scope.curPage = 1;
            reload();
        }
    };
    $scope.lastPage = function () {
        var lastPageNumber = $scope.maxPage;
        if ($scope.curPage !== lastPageNumber) {
            $scope.curPage = lastPageNumber;
            reload();
        }
    };
    $scope.delete = function (id) {
        push.deleteSchedule(id, function (r) {
            if (r.data && r.data.code === 0) {
                reload();
            }
        });
    };
    $scope.deleteList = function (id) {
        push.deleteSchedule(id, function (r) {     
        });
    };
    reload();
});

app.controller("historyTable", function ($scope, $rootScope, push) {

    $scope.dataOption = {
    selectedOption: {id: '10', name: '10'},
    availableOptions: [
      {id: '10', name: '10'},
      {id: '20', name: '20'},
      {id: '30', name: '30'}
    ],
   };

    $scope.exportData = function () {
        var blob = new Blob([document.getElementById('exportableHT').innerHTML], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        });
        saveAs(blob, "History.xls");
    };

    $scope.count = 0;
    $scope.size = 10;
    $scope.curPage = 1;
    $scope.data = [];
    $scope.maxPage = 1;
   

    function reload() {
//        $scope.$apply();
        push.getHistoryPage($rootScope.selectedApp.id,$scope.dataOption.selectedOption.id * ($scope.curPage - 1), $scope.dataOption.selectedOption.id, function (r) {
            if (r.data && r.data.value) {
                $scope.data = [];
                r.data.value.forEach(function (e, i) {
                    r.data.value[i].request = JSON.parse(r.data.value[i].request);
                    $scope.data.push(e);
                });
            } 
        });


         push.getHistoryCount($rootScope.selectedApp.id, function (r) {
            if (r.data && r.data.value) {
                $scope.count = r.data.value;
                $scope.maxPage = getMaxPageNumber($scope.count, $scope.dataOption.selectedOption.id);
            }
        });
    }

    $scope.changedValue = function(){
        $scope.curPage = 1;
        reload();
    }
    $scope.previousPage = function () {
        if ($scope.curPage > 1) {
            $scope.curPage--;
            reload();
        }
    };
    $scope.nextPage = function () {
        if ($scope.curPage < $scope.maxPage) {
            $scope.curPage++;
            reload();
        }
    };
    $scope.firstPage = function () {
        if ($scope.curPage !== 1) {
            $scope.curPage = 1;
            reload();
        }
    };
    $scope.lastPage = function () {
        var lastPageNumber = $scope.maxPage;
        if ($scope.curPage !== lastPageNumber) {
            $scope.curPage = lastPageNumber;
            reload();
        }
    };
    reload();
});





app.controller("test", function ($scope, push) {
    $scope.userId = "123";
    $scope.pass = "123";
    $scope.appId = 10073;
    $scope.platform = "android";
    $scope.requestId = "926f37949f9c40eb9ac6e28b8b310d87";
    $scope.login = function () {
        push.login($scope.userId, $scope.pass, function (r) {
            $scope.output = r.data;
        });
    };
    $scope.logout = function () {
        push.logout(function (r) {
            $scope.output = r.data;
        });
    };
    $scope.getProfile = function () {
        push.getProfile(function (r) {
            $scope.output = r.data;
        });
    };
    $scope.getApps = function () {
        push.getApps(function (r) {
            $scope.output = r.data;
        });
    };
    $scope.getDevicePage = function () {
        push.getDevicePage($scope.appId, $scope.platform, 0, 1, function (r) {
            $scope.output = r.data;
        });
    };
    $scope.getScheduledPage = function () {
        push.getScheduledPage($scope.appId, 0, 3, function (r) {
            $scope.output = r.data;
        });
    };
    $scope.getScheduledCount = function () {
        push.getScheduledCount($scope.appId, function (r) {
            $scope.output = r.data;
        });
    };
    $scope.getHistoryPage = function () {
        push.getHistoryPage($scope.appId, 0, 3, function (r) {
            $scope.output = r.data;
        });
    };
    $scope.getPush = function () {
        push.getPush($scope.appId, $scope.requestId, function (r) {
            $scope.output = r.data;
        });
    };
    $scope.countDevice = function () {
        var filter = {
            os_version: {
                $in: ["5.0"]
            }
        };
        push.countDevice($scope.appId, $scope.platform, filter, function (r) {
            $scope.output = r.data;
        });
    };
});
