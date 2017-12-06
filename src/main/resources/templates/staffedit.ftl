<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Staff - Redemption</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <#include "stdlinks.inc.ftl">
    <link rel="stylesheet" href="/css/staffedit.css"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div id="staff-edit-form">
                <form action="/api/v1/staff/${staff.id}" method="post">
                    <div class="form-group">
                        <label for="">Username</label>
                        <input type="text" class="form-control" id="staff-username" placeholder="Username" value="${staff.username}">
                    </div>
                    <div class="form-group">
                        <label for="">Password</label>
                        <input type="password" class="form-control" id="staff-password" placeholder="Password">
                    </div>
                <#list permissions as permission>
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" ${staff.permissions?seq_contains(permission)?string("checked='true'", "")}> ${permission.description}
                        </label>
                    </div>
                </#list>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-danger">Cancel</button>
                    <button class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </div>
</div>

<#include "stdimports.inc.ftl">
</body>
</html>