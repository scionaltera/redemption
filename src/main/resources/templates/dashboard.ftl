<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard - Redemption</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <#include "stdlinks.inc.ftl">
    <link rel="stylesheet" href="/css/dashboard.css"/>
</head>
<body>
<div class="container-fluid">
<#if secure == false>
    <div class="row">
        <div class="col-md-12">
            <div id="security-box-inner">
            <span>Warning! The default staff account is enabled, which presents a security risk because it uses a
                username and password that are publicly available on GitHub. Please either delete the account or
                change its password before using Redemption in a production setting.</span>
            </div>
        </div>
    </div>
</#if>
    <div class="row">
        <div id="auth-box-outer" class="col-md-12">
            <div id="auth-box-inner">
                <form action="<@spring.url '/logout'/>" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-danger">Sign Out</button>
                </form>
            </div>
        </div>
    </div>
<#if liststaff??>
    <div class="row">
        <div id="perm-box-outer" class="col-md-6">
            <div id="perm-box-inner">
                <p>Permissions for ${staff.username?capitalize}</p>
                <ul>
                    <#list staff.permissions as permission>
                        <li>${permission.description}</li>
                    </#list>
                </ul>
            </div>
        </div>

        <div id="staff-box-outer" class="col-md-6">
            <div id="staff-box-inner">
                <p>Staff Editor <button id="staff-button-create" class="btn btn-success" <#if createstaff??><#else>disabled</#if>>Create</button></p>
                <table id="staff-box-content" class="table">
                    <tr><th>Name</th><th>Actions</th></tr>
                </table>
            </div>
        </div>
    </div>
</#if>
<#if readlogs??>
    <div class="row">
        <div id="log-box-outer" class="col-md-12">
            <div id="log-box-inner">
                <p>Audit Log</p>
                <div id="log-box-content"></div>
            </div>
        </div>
    </div>
</#if>
    <div class="row">
        <div class="col-md-12">
        <#include "copyright.inc.ftl">
        </div>
    </div>
</div>

<div class="invisible">
<#if liststaff??><div id="perm-list-staff"></div></#if>
<#if createstaff??><div id="perm-create-staff"></div></#if>
<#if editstaff??><div id="perm-edit-staff"></div></#if>
<#if deletestaff??><div id="perm-delete-staff"></div></#if>
</div>

<#include "stdimports.inc.ftl">
<script type="text/javascript" src="js/auditlog.js"></script>
<script type="text/javascript" src="js/staffselect.js"></script>
</body>
</html>