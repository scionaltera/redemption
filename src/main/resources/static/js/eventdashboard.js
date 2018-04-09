$(document).ready(function() {
    var $permListEvent = $("#perm-list-event");
    var $permCreateEvent = $("#perm-create-event");
    var $permEditEvent = $("#perm-edit-event");
    var $permDeleteEvent = $("#perm-delete-event");

    if ($permListEvent.length !== 0) {
        $.getJSON("/api/v1/event", function (data) {
            var messages = [];

            $.each(data, function() {
                messages.push(
                    "<tr><td>" + this.name + "</td>" +
                    "<td>" + new Date(this.startDate).toLocaleString('en-US', { timeZone: 'GMT' }) + "</td>" +
                    "<td>" + new Date(this.endDate).toLocaleString('en-US', { timeZone: 'GMT' }) + "</td>" +
                    "<td>" +
                    "<button class='btn btn-light btn-edit' data-event-id='" + this.id + "' " + ($permEditEvent.length === 0 ? "disabled" : "") + ">Edit</button>" +
                    "<button class='btn btn-danger btn-delete' data-event-id='" + this.id + "' " + ($permDeleteEvent.length === 0 ? "disabled" : "") + ">Delete</button>" +
                    "</td></tr>");
            });

            var $eventBoxContent = $("#event-box-content");

            $eventBoxContent.append(messages.join(""));

            $eventBoxContent.find(".btn-edit").click(function () {
                window.location = "/event/" + $(this).attr('data-event-id');

                event.preventDefault();
                return false;
            });

            if ($permDeleteEvent.length !== 0) {
                $eventBoxContent.find(".btn-delete").click(function () {
                    var header = $("meta[name='_csrf_header']").attr("content");
                    var token = $("meta[name='_csrf']").attr("content");

                    $.ajax({
                        url: "/api/v1/event/" + $(this).attr('data-event-id'),
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

        if ($permCreateEvent.length !== 0) {
            $("#event-button-create").click(function() {
                window.location = "/event";

                event.preventDefault();
                return false;
            });
        }
    }
});