<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html>
<head>
    <title>Home - Redemption</title>
</head>
<body>
<h1>Redemption!</h1>
<#if message??>
    <div style="color: red;">${message}</div>
</#if>
<form action="<@spring.url '/login'/>" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <div><label> User Name: <input type="text" name="username"/> </label></div>
    <div><label> Password: <input type="password" name="password"/> </label></div>
    <div><input type="submit" value="Sign In"/></div>
</form>
</body>
</html>