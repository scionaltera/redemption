$(document).ready(function() {
    $.getJSON("/api/v1/staff", function(data) {
        var messages = [];

        $.each(data, function() {
            messages.push(
                "<tr><td>" + this.username + "</td><td>" +
                "<button class='btn btn-default btn-edit' data-staff-id='" + this.id + "'>Edit</button>" +
                "<button class='btn btn-danger btn-delete' data-staff-id='" + this.id + "'>Delete</button>" +
                "</td></tr>");
        });

        var $staffBoxContent = $("#staff-box-content");

        $staffBoxContent.append(messages.join(""));

        $staffBoxContent.find(".btn-edit").click(function() {
            window.location = "/staff/" + $(this).attr('data-staff-id');

            event.preventDefault();
            return false;
        });

        $staffBoxContent.find(".btn-delete").click(function() {
            alert("Delete " + $(this).attr('data-staff-id'));
            // hit delete API with staff id

            event.preventDefault();
            return false;
        });
    });

    $("#staff-button-create").click(function() {
        // go to create form

        event.preventDefault();
        return false;
    });
});