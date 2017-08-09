<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard - Redemption</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <#include "stdlinks.inc.ftl">
    <link rel="stylesheet" href="css/dashboard.css"/>
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
<#if staff??>
    <div class="row">
        <div id="perm-box-outer" class="col-md-12">
            <div id="perm-box-inner">
                <p>Permissions for ${staff.username?capitalize}</p>
                <ul>
                <#list staff.permissions as permission>
                    <li>${permission.description}</li>
                </#list>
                </ul>
            </div>
        </div>
    </div>
</#if>
    <div class="row">
        <div id="log-box-outer" class="col-md-12">
            <div id="log-box-inner">
                <p>Audit Log</p>
                <div id="log-box-content"></div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
        <#include "copyright.inc.ftl">
        </div>
    </div>
</div>

<#include "stdimports.inc.ftl">
<script type="text/javascript" src="js/auditlog.js"></script>
</body>
</html>