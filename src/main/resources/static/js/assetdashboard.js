$(document).ready(function() {
    var $permListAsset = $("#perm-list-asset");
    var $permCreateAsset = $("#perm-create-asset");
    var $permEditAsset = $("#perm-edit-asset");
    var $permDeleteAsset = $("#perm-delete-asset");

    if ($permListAsset.length !== 0) {
        $.getJSON("/api/v1/asset", function (data) {
            var messages = [];

            $.each(data, function() {
                messages.push(
                    "<tr><td>" + this.name + "</td><td>" +
                    "<button class='btn btn-light btn-edit' data-asset-id='" + this.id + "' " + ($permEditAsset.length === 0 ? "disabled" : "") + ">Edit</button>" +
                    "<button class='btn btn-danger btn-delete' data-asset-id='" + this.id + "' " + ($permDeleteAsset.length === 0 ? "disabled" : "") + ">Delete</button>" +
                    "</td></tr>");
            });

            var $assetBoxContent = $("#asset-box-content");

            $assetBoxContent.append(messages.join(""));

            $assetBoxContent.find(".btn-edit").click(function () {
                window.location = "/asset/" + $(this).attr('data-asset-id');

                event.preventDefault();
                return false;
            });

            if ($permDeleteAsset.length !== 0) {
                $assetBoxContent.find(".btn-delete").click(function () {
                    var header = $("meta[name='_csrf_header']").attr("content");
                    var token = $("meta[name='_csrf']").attr("content");

                    $.ajax({
                        url: "/api/v1/asset/" + $(this).attr('data-asset-id'),
                        method: "DELETE",
                        headers: {
                            [header]: token
                        }
                    }).done(function() {
                        window.location = "/dashboard";
                    });

                    event.preventDefault();
                    return false;
                });
            }
        });

        if ($permCreateAsset.length !== 0) {
            $("#asset-button-create").click(function() {
                window.location = "/asset";

                event.preventDefault();
                return false;
            });
        }
    }
});