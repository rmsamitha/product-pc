/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
*/

var resources = function (page, meta) {
    return {
        js: ['jquery.MetaData.js', 'jquery.rating.pack.js', 'async.min.js', 'asset-core.js', 'asset.js', 'moment.min.js', 'porthole.min.js','editablegrid-2.0.1.js'],
        css: ['jquery.rating.css', 'asset.css','editablegrid-2.0.1.css'],
        code: ['store.asset.hbs']
    };
};

var assetLinks = function (user) {
    return {
        title: 'Process'
    };
};


asset.server = function(ctx) {
    var type = ctx.type;
    return {
        onUserLoggedIn: function() {},
        endpoints: {
            apis: [{
                url: 'content',
                path: 'content.jag'
            },{
                url: 'processes',
                path: 'processes.jag'
            }]
        }
    };
};
