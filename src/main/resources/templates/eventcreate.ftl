<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Event - Redemption</title>

    <#include "stdmeta.inc.ftl">
    <#include "stdlinks.inc.ftl">
    <#include "internal.inc.ftl">
    <link rel="stylesheet" href="<@spring.url '/css/eventcreate.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="event-create-form">
                <h1>Create Event</h1>
                <form action="<@spring.url '/api/v1/event'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Name</label>
                        <input type="text" class="form-control" name="name" id="event-name" placeholder="Short Name">
                    </div>
                    <div class="form-group">
                        <label for="">Description</label>
                        <input type="text" class="form-control" name="description" id="event-description" placeholder="Description">
                    </div>
                    <div class="form-group">
                        <label for="">Start Date (GMT)</label>
                        <input type="datetime-local" class="form-control" name="startDate" id="event-start-date">
                    </div>
                    <div class="form-group">
                        <label for="">End Date (GMT)</label>
                        <input type="datetime-local" class="form-control" name="endDate" id="event-end-date">
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
<script src="<@spring.url '/js/eventcreate.js'/>"></script>
</body>
</html>