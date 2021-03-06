/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
        js: ['audit-log-functions.js'],
        css: ['flowchart/jsPlumb-defaults.css', 'flowchart/jsPlumb-demo.css', 'bootstrap-select.min.css',
            'alertify.css', 'default.css', 'datepick/smoothness.datepick.css', 'grid.css',
            'select2.min.css', 'jquery-ui.css', 'font-awesome.css', 'search.css', 'build.css',
            'auto-complete-tags/bootstrap-tokenfield.min.css', 'auto-complete-tags/tokenfield-typehead.min.css', 'log.css','process-center-custom.css']
    };
};