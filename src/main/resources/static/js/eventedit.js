$(document).ready(function() {
    $("#event-edit-form button.btn-primary").click(function() {
        var form = $("#event-edit-form form");

        $.ajax({
            url: form.attr("action"),
            method: "POST",
            data: form.serialize()
        }).done(function() {
            window.location = "/dashboard";
        }).fail(function(jqXHR) {
            var errorBox = $("#event-error-box");

            errorBox.removeClass("invisible");
            errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
        });

        event.preventDefault();
        return false;
    });

    $("#event-edit-form button.btn-danger").click(function() {
        window.location = "/dashboard";

        event.preventDefault();
        return false;
    });

    $("#event-participants-edit-form button.btn-primary").click(function() {
        var form = $("#event-participants-edit-form form");

        $.ajax({
            url: form.attr("action"),
            method: "POST",
            data: form.serialize()
        }).done(function(response) {
            var participant = response['participant'];
            var eventId = response['eventId'];
            var csrfToken = $("meta[name='_csrf']").attr("content");

            $.ajax({
                url: "/api/v1/asset?eventId=" + eventId,
                method: "GET"
            }).done(function(data) {
                var selectOptions = "";

                for (var i = 0; i < data.length; i++) {
                    selectOptions += `<option value="${data[0].id}">${data[0].name}</option>\n`;
                }

                var row = $("#event-participant-table tr:last").after(`
<tr>
<td>${participant.lastName}, ${participant.firstName} (${participant.email})</td>
<td>
<select data-event-id="${eventId}" data-participant-id="${participant.id}">
    <option value="">None</option>
    ${selectOptions}
</select>
</td>
<td><button data-csrf-param="_csrf" data-csrf-token="${csrfToken}" data-participant-id="${participant.id}" type="submit" class="btn btn-danger">Remove</button></td>
<td><p class="text-danger"></p></td>
</tr>`);
                row.next().find("button").click(removeButtonClick);
                row.next().find("select").change(assetAssignmentChange);
            }).fail(function(jqXHR) {
                console.log("Failed to fetch awards: " + jqXHR.responseJSON.message);
            });
        }).fail(function(jqXHR) {
            var errorBox = $("#event-participant-error-box");

            errorBox.removeClass("invisible");
            errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
        });

        event.preventDefault();
        return false;
    });

    $("#event-participants-edit-form button.btn-danger").click(removeButtonClick);

    $("#event-participants-edit-form select").change(assetAssignmentChange);
});

var removeButtonClick = function() {
    var form = $("#event-participants-edit-form form");
    var participantId = $(this)[0].dataset.participantId;
    var payload = {};

    payload[$(this)[0].dataset.csrfParam] = $(this)[0].dataset.csrfToken;

    $.ajax({
        url: form.attr("action") + "/" + participantId,
        method: "DELETE",
        data: payload
    }).done(function() {
        $("#event-participant-table tr").has("button[data-participant-id=" + participantId + "]").remove();
    }).fail(function(jqXHR) {
        var errorBox = $("#event-participant-error-box");

        errorBox.removeClass("invisible");
        errorBox.find("p").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
    });

    event.preventDefault();
    return false;
};

var assetAssignmentChange = function() {
    var htmlElement = $(this);
    var eventId = $(this)[0].dataset.eventId;
    var participantId = $(this)[0].dataset.participantId;
    var assetId = $(this).val();
    var csrfToken = $("meta[name='_csrf']").attr("content");

    $.ajax({
        url: "/api/v1/event/" + eventId + "/participant/" + participantId + "/asset",
        method: "POST",
        data: {
            "assetId": assetId,
            "_csrf": csrfToken
        }
    }).done(function() {
        htmlElement.parents("tr").find("p.text-danger").html("");
    }).fail(function(jqXHR) {
        htmlElement.parents("tr").find("p.text-danger").html(jqXHR.responseJSON.message.split("\n").join("<br/>"));
    });

    event.preventDefault();
    return false;
};