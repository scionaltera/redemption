<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard - Redemption</title>

    <#include "stdmeta.inc.ftl">
    <#include "stdlinks.inc.ftl">
    <#include "internal.inc.ftl">
    <link rel="stylesheet" href="<@spring.url '/css/dashboard.css'/>"/>
</head>
<body>
<div class="container-fluid">
<#if secure == false>
    <div class="row">
        <div class="col-md-12">
            <div id="security-box-inner" class="box-inner">
            <span>Warning! The default staff account is enabled, which presents a security risk because it uses a
                username and password that are publicly available on GitHub. Please either delete the account or
                change its password before using Redemption in a production setting.</span>
            </div>
        </div>
    </div>
</#if>
    <div class="row">
        <div id="auth-box-outer" class="col-md-12">
            <div id="auth-box-inner" class="box-inner">
                <form action="<@spring.url '/logout'/>" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-danger">Sign Out</button>
                </form>
            </div>
        </div>
    </div>
<#if list\-asset??>
    <div class="row">
        <div id="asset-box-outer" class="col-md-12">
            <div id="asset-box-inner" class="box-inner">
                <p>Assets <button id="asset-button-create" class="btn btn-success" <#if create\-asset??><#else>disabled</#if>>Create</button></p>
                <ul>
                    <#list assets as asset>
                        <li>${asset.name}</li>
                    </#list>
                </ul>
            </div>
        </div>
    </div>
</#if>
<#if list\-staff??>
    <div class="row">
        <div id="perm-box-outer" class="col-md-6">
            <div id="perm-box-inner" class="box-inner">
                <p>Permissions for ${staff.username?capitalize}</p>
                <ul>
                    <#list staff.permissions as permission>
                        <li>${permission.description}</li>
                    </#list>
                </ul>
            </div>
        </div>

        <div id="staff-box-outer" class="col-md-6">
            <div id="staff-box-inner" class="box-inner">
                <p>Staff Editor <button id="staff-button-create" class="btn btn-success" <#if create\-staff??><#else>disabled</#if>>Create</button></p>
                <table id="staff-box-content" class="table">
                    <tr><th>Name</th><th>Actions</th></tr>
                </table>
            </div>
        </div>
    </div>
</#if>
<#if read\-logs??>
    <div class="row">
        <div id="log-box-outer" class="col-md-12">
            <div id="log-box-inner" class="box-inner">
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
<#list permissions as permission>
    <#if permission.unique??><div id="perm-${permission.unique}"></div></#if>
</#list>
</div>

<#include "stdimports.inc.ftl">
<script type="text/javascript" src="<@spring.url 'js/auditlog.js'/>"></script>
<script type="text/javascript" src="<@spring.url 'js/staffselect.js'/>"></script>
</body>
</html>