<%@ page import="java.util.Map" %>
<%@ page import="com.loadimpact.teamcity_plugin.Debug" %>
<%@ page import="com.loadimpact.teamcity_plugin.Constants" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.regex.Matcher" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="json" class="com.loadimpact.teamcity_plugin.JsonGenerator"/>

<%
    Debug debug = new Debug("[LoadImpact] EditLIRT.jsp");
    Debug.setEnabled(true);
    debug.print("initializing...");

    Map<String, String> settings = propertiesBean.getProperties();
    debug.print("settings: %s", settings);
    json.setSettings(settings);

    String chosenTestConfiguration = settings.containsKey(Constants.testConfigurationId_key)
                                     ? settings.get(Constants.testConfigurationId_key)
                                     : "";
    request.setAttribute("chosenTestConfiguration", chosenTestConfiguration);

    Set<Integer> thresholdIds = new TreeSet<Integer>();
    Pattern tpat = Pattern.compile("threshold\\.(\\d+)\\.value");
    for (String key : settings.keySet()) {
        Matcher m = tpat.matcher(key);
        if (m.matches()) thresholdIds.add(Integer.parseInt(m.group(1)));
    }
    request.setAttribute("thresholdIds", thresholdIds);
%>

<div id="tstCfgTmpl" style="display: none;">
    <span>
        {0}: <strong>{1}</strong> - <a href="{2}">{2}</a>
    </span>
</div>

<script type="text/javascript">
    var chosenTestConfiguration = "${chosenTestConfiguration}";
    var thresholds = ${json.thresholds};

    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined' ? args[number] : match;
        });
    };
    
    $j(document).ready(function(){
        console.log("[LoadImpact] Initializing ...");
        console.log("[LoadImpact] thresholds: " + JSON.stringify(thresholds, null, 2));
        console.log("[LoadImpact] chosenTestConfiguration: " + chosenTestConfiguration);
        
        var tstCfg = $j("#tstCfg");
        var tstCfgSpinner = $j("#tstCfgSpinner");

        tstCfgSpinner.show();
        console.log("Invoking AJAX...");
        $j.getJSON("/loadImpactProxy.html", {"task":"testConfiguration", "id":chosenTestConfiguration}, function(cfg){
            console.log("AJAX response: " + JSON.stringify(cfg,null,2));

            tstCfg.empty();
            var tmpl = $j("#tstCfgTmpl").html();
            var html = tmpl.format(cfg.id, cfg.name, cfg.url);
            tstCfg.append(html);
            
            tstCfgSpinner.hide();
        });
        console.log("[LoadImpact] Initialized");
    });
</script>


<h2>Load Test Configuration</h2>
<div class="parameter">
    Test Configuration: <span id="tstCfg"></span> <img id="tstCfgSpinner" src="/img/spinner.gif" style="display: none;" />
</div>


<h2>Failure Criteria</h2>
<div class="parameter">
    Start looking after <props:displayValue name="delay.value" emptyValue="0"/>
    <props:displayValue name="delay.unit" emptyValue="seconds"/>
</div>
<div class="parameter">
    Use last <props:displayValue name="delay.size" emptyValue=""/> metric values
</div>
<div class="parameter">
    Abort if failure: <props:displayValue name="abort.failure" emptyValue="false"/>
</div>


<h2>Thresholds</h2>
<c:forEach items="${thresholdIds}" var="t">
    <div class="parameter">
        IF <props:displayValue name="threshold.${t}.metric" emptyValue="M"/>
        <props:displayValue name="threshold.${t}.operator" emptyValue="#"/>
        <props:displayValue name="threshold.${t}.value" emptyValue="0"/>
        THEN mark as
        <props:displayValue name="threshold.${t}.result" emptyValue="X"/>
    </div>
</c:forEach>


<h2>Advanced Settings</h2>
<div class="parameter">
    Poll interval: <props:displayValue name="poll.interval" emptyValue="0"/>
</div>


<h2>Log/Debug Settings</h2>
<div class="parameter">
    Log HTTP REST: <props:displayValue name="log.http" emptyValue="false"/>
</div>
<div class="parameter">
    Log Replies: <props:displayValue name="log.replies" emptyValue="false"/>
</div>
<div class="parameter">
    Log Developer DEBUG Messages: <props:displayValue name="log.debug" emptyValue="false"/>
</div>
