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
    <div class="row">
        <div id="log-box-outer" class="col-md-12">
            <div id="log-box-inner">
                <span>Audit Log</span>
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