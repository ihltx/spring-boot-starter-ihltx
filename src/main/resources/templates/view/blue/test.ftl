freemarker tpl(blue)
<hr/>
appName = ${appName}
<hr/>
APP_SHOP_ID = ${APP_SHOP_ID}
<hr/>
APP_THEME = ${APP_THEME}
<hr/>
APP_PATH = ${APP_PATH}
<hr/>
APP_STATIC_CDN_URL_PREFIX = ${APP_STATIC_CDN_URL_PREFIX}
<hr/>
APP_GLOBAL_THEME_PATH = ${APP_GLOBAL_THEME_PATH}
<hr/>
APP_THEME_PATH = ${APP_THEME_PATH}
<hr/>
APP_LANGUAGES =
<#if APP_LANGUAGES?exists>
    <#list APP_LANGUAGES as item>
        ${item_index} = langId: ${item.langId}  langCode: ${item.langCode}    shopId: ${item.shopId}  langName: ${item.langName}
        langRemark: ${item.langRemark}  langCssClass: ${item.langCssClass}  langOrder: ${item.langOrder}
        langEnabled: ${item.langEnabled?string("true","false")}  langIsDefault: ${item.langIsDefault?string("true","false")}  langDeleted: ${item.langDeleted?string("true","false")}
        <br/>
    </#list>
</#if>
<hr/>
APP_All_Langs = ${APP_All_Langs}
<hr/>
