<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Redemption</title>
</head>
<body>
<h1>Redemption!</h1>
<form action="<@spring.url '/logout'/>" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="submit" value="Sign Out"/>
</form>
</body>
</html>