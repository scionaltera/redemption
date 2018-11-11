<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Event - Redemption</title>

    <#include "stdmeta.inc.ftl">
    <#include "stdlinks.inc.ftl">
    <#include "internal.inc.ftl">
    <link rel="stylesheet" href="<@spring.url '/css/eventedit.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="event-edit-form">
                <h1>Edit Event</h1>
                <form action="<@spring.url '/api/v1/event/${event.id}'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Name</label>
                        <input type="text" class="form-control" name="name" id="event-name" placeholder="Short Name" value="${event.name}">
                    </div>
                    <div class="form-group">
                        <label for="">Description</label>
                        <input type="text" class="form-control" name="description" id="event-description" placeholder="Description" value="${event.description}">
                    </div>
                    <div class="form-group">
                        <label for="">Start Date (GMT)</label>
                        <input type="datetime-local" class="form-control" name="startDate" id="event-start-date" value="${event.startDate?string["yyyy-MM-dd'T'HH:mm"]}">
                    </div>
                    <div class="form-group">
                        <label for="">End Date (GMT)</label>
                        <input type="datetime-local" class="form-control" name="endDate" id="event-end-date" value="${event.endDate?string["yyyy-MM-dd'T'HH:mm"]}">
                    </div>
                    <div class="form-group">
                        <label for="">Participants</label>
                        <select multiple="multiple" name="participants" id="event-participants">
                            <#list participants as participant>
                                <option value="${participant.id}" ${event.participants?seq_contains(participant)?string("selected", "")}>${participant.lastName}, ${participant.firstName}</option>
                            </#list>
                        </select>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-danger">Cancel</button>
                    <button class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col">
        <#include "copyright.inc.ftl">
        </div>
    </div>
</div>

<#include "stdimports.inc.ftl">
<script src="<@spring.url '/js/eventedit.js'/>"></script>
</body>
</html>