/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
$(document).ready(function () {

    $('#btn-delete-con').on('click', function (e) {
        removeProcessListener()
    });

    $('#btn-cancel-con').on('click', function (e) {
        var assetId = $('#asset-id').val();
        var assetType = $('#asset-type').val();
        var path = caramel.url('/assets/' + assetType + '/details/' + assetId);

        $.ajax({
            success: function (response) {
                window.location = path;
            }
        });
    });

});

function removeProcessListener() {

    var question = "Are you sure you want to delete the process permanently?";
    var confirmModal = confirmDialog(question);
    confirmModal.find('#okButton').click(function (event) {
        deleteProcess();
        confirmModal.modal('hide');
    });
    confirmModal.modal('show');
}

function confirmDialog(question) {
    var confirmModal =
        $('<div class="modal fade">' +
            '<div class="modal-dialog">' +
            '<div class="modal-content" style="background: #e3e3e3; none: repeat scroll 0% 0% ;">' +
            '<div class="modal-header">' +
            '<a class="close" data-dismiss="modal" >&times;</a>' +
            '<h3>Confirm delete</h3>' +
            '</div>' +
            '<div class="modal-body">' +
            '<p>' + question + '</p>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<a href="#!" class="btn" data-dismiss="modal">Cancel</a>' +
            '<a href="#!" id="okButton" class="btn btn-primary">Delete</a>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>');
    return confirmModal;
}

function deleteProcess() {

    var assetId = $('#asset-id').val();
    var assetType = $('#asset-type').val();
    var asset_Name = $('#asset-name').val();
    var path = caramel.url('/apis/assets/' + assetId + '?type=' + assetType);
    var landingPage = $('#landing-page').val();
    var landingPageUrl = caramel.url(landingPage);
    $('#btn-delete-con').addClass('disabled');
    $('#delete-loading').removeClass('hide');
    $('.nav li>a:not(:first):not(:last)').css("display", "none");
    $('.tiles > li > a').css("display", "block");

    $.ajax({
        url: path,
        type: 'DELETE',
        success: function (response) {
            messages.alertSuccess(asset_Name + ' deleted successfully!');
            $('.alert-success').removeClass('hide');
            $('#btn-delete-con').addClass('hide ');
            $('#btn-cancel-con').addClass('hide ');
            $('#delete-loading').addClass('hide');
            $('#delete-msg').addClass('hide');
            $('.message.message-danger').hide();
            setTimeout(function () {
                var path = caramel.url('/assets/' + assetType + '/list');
                window.location = path;
            }, 3000);
        },
        error: function () {
            messages.alertError('Error while deleting' + asset_Name + 'asset!');
            $('.alert-success').removeClass('hide');
            $('#delete-loading').removeClass('hide');
            $('#delete-loading').addClass('hide');
            $('.nav li>a:not(:first):not(:last)').css("display", "inherit");
        }
    });

}
