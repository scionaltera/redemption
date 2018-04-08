$(document).ready(function() {
    var $permListParticipant = $("#perm-list-participant");
    var $permCreateParticipant = $("#perm-create-participant");
    var $permEditParticipant = $("#perm-edit-participant");
    var $permDeleteParticipant = $("#perm-delete-participant");

    if ($permListParticipant.length !== 0) {
        $.getJSON("/api/v1/participant", function (data) {
            var messages = [];

            $.each(data, function() {
                messages.push(
                    "<tr><td>" + this.firstName + "</td><td>" + this.lastName + "</td><td>" + this.email + "</td><td>" +
                    "<button class='btn btn-light btn-edit' data-participant-id='" + this.id + "' " + ($permEditParticipant.length === 0 ? "disabled" : "") + ">Edit</button>" +
                    "<button class='btn btn-danger btn-delete' data-participant-id='" + this.id + "' " + ($permDeleteParticipant.length === 0 ? "disabled" : "") + ">Delete</button>" +
                    "</td></tr>");
            });

            var $participantBoxContent = $("#participant-box-content");

            $participantBoxContent.append(messages.join(""));

            $participantBoxContent.find(".btn-edit").click(function () {
                window.location = "/participant/" + $(this).attr('data-participant-id');

                event.preventDefault();
                return false;
            });

            if ($permDeleteParticipant.length !== 0) {
                $participantBoxContent.find(".btn-delete").click(function () {
                    var header = $("meta[name='_csrf_header']").attr("content");
                    var token = $("meta[name='_csrf']").attr("content");

                    $.ajax({
                        url: "/api/v1/participant/" + $(this).attr('data-participant-id'),
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

        if ($permCreateParticipant.length !== 0) {
            $("#participant-button-create").click(function() {
                window.location = "/participant";

                event.preventDefault();
                return false;
            });
        }
    }
});