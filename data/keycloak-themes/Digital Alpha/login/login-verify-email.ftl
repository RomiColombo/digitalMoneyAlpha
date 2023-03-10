<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
    <#if section = "form">
        <p class="instruction verify-email-container-message">${msg("emailVerifyInstruction1",user.email)}</p> 
    <#elseif section = "info">
        <p class="instruction verify-email-container">
            ${msg("emailVerifyInstruction2")}
            <br/>
            <a href="${url.loginAction}">${msg("doClickHere")}</a> ${msg("emailVerifyInstruction3")}
        </p>
    </#if>
</@layout.registrationLayout>
