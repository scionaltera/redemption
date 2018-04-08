$(document).ready(function() {
    var $permListStaff = $("#perm-list-staff");
    var $permCreateStaff = $("#perm-create-staff");
    var $permEditStaff = $("#perm-edit-staff");
    var $permDeleteStaff = $("#perm-delete-staff");

    if ($permListStaff.length !== 0) {
        $.getJSON("/api/v1/staff", function (data) {
            var messages = [];

            $.each(data, function () {
                messages.push(
                    "<tr><td>" + this.username + "</td><td>" +
                    "<button class='btn btn-light btn-edit' data-staff-id='" + this.id + "' " + ($permEditStaff.length === 0 ? "disabled" : "") + ">Edit</button>" +
                    "<button class='btn btn-danger btn-delete' data-staff-id='" + this.id + "' " + ($permDeleteStaff.length === 0 ? "disabled" : "") + ">Delete</button>" +
                    "</td></tr>");
            });

            var $staffBoxContent = $("#staff-box-content");

            $staffBoxContent.append(messages.join(""));

            if ($permEditStaff.length !== 0) {
                $staffBoxContent.find(".btn-edit").click(function () {
                    window.location = "/staff/" + $(this).attr('data-staff-id');

                    event.preventDefault();
                    return false;
                });
            }

            if ($permDeleteStaff.length !== 0) {
                $staffBoxContent.find(".btn-delete").click(function () {
                    var header = $("meta[name='_csrf_header']").attr("content");
                    var token = $("meta[name='_csrf']").attr("content");

                    $.ajax({
                        url: "/api/v1/staff/" + $(this).attr('data-staff-id'),
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

        if ($permCreateStaff.length !== 0) {
            $("#staff-button-create").click(function () {
                window.location = "/staff";

                event.preventDefault();
                return false;
            });
        }
    }
});