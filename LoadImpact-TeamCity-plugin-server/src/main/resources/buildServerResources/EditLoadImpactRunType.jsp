<%@ page import="com.loadimpact.teamcity_plugin.Constants" %>
<%@ page import="com.loadimpact.teamcity_plugin.LoadImpactSettings" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.loadimpact.teamcity_plugin.Debug" %>
<%--<%@ page import="com.loadimpact.eval.DelayUnit" %>--%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="bs" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>
<jsp:useBean id="json" class="com.loadimpact.teamcity_plugin.JsonGenerator"/>

<%--
    Initialize JSP
--%>
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
    
    request.setAttribute("delayUnits", com.loadimpact.eval.DelayUnit.values());
    request.setAttribute("apiToken", LoadImpactSettings.instance().getApiToken());
%>

<%--
    Styles
--%>
<style type="text/css">
    .LI_dropList {
        padding:0; margin:0;
    }
    .LI_numericField {
        text-align: right;
        width: 6em;
    }
    .LI_addButton {
        cursor: pointer;
        background: url("/img/add.png") no-repeat left;
        padding-left: 1.5em;
        white-space: nowrap;
        text-decoration: none;
    }
    .LI_removeButton {
        color: red;
        font-weight: bold;
        cursor: pointer;
        text-decoration: none;
    }
    .LI_reloadButton {
        color: #008000;
        font-weight: bold;
        cursor: pointer;
        text-decoration: none;
    }
    .LI_unit {
        font-style: italic;
        width: 3em;
        overflow: hidden;
        display: inline-block;
        vertical-align: text-bottom;
    }
</style>

<%--
    HTML Templates
--%>
<div id="option" style="display: none;">
    <option value="{0}" data-title="" {2}>{1}</option>
</div>

<div id="threshold" style="display: none;">
    <div id="threshold_{0}" class="threshold" style="display: none;">
        <strong>IF</strong>
        <select id="threshold_{0}_metric" name="prop:threshold.{0}.metric" class="LI_dropList"> {1} </select>
        <select id="threshold_{0}_operator" name="prop:threshold.{0}.operator" class="LI_dropList"> {2} </select>
        <input type="text" id="threshold_{0}_value" name="prop:threshold.{0}.value" value="{3}" class="LI_numericField"/>
        <span id="threshold_{0}_unit" class="LI_unit">XX</span>
        <strong>THEN</strong> mark as
        <select id="threshold_{0}_result" name="prop:threshold.{0}.result"  class="LI_dropList"> {4} </select>
        <a href="#" id="threshold_{0}_remove" class="LI_removeButton">X</a>
    </div>
</div>


<%--
    JavaScript Functions
--%>
<script type="text/javascript">
    // Populate JSON data from the server
    var metrics = ${json.metrics};
    var operators = ${json.operators};
    var actions = ${json.actions};
    var thresholds = ${json.thresholds};
    var chosenTestConfiguration = "${chosenTestConfiguration}";
    var nextMetricId = 1;

    String.prototype.format = function () {
        var args = arguments;
        return this.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined' ? args[number] : match;
        });
    };

    function mkOptions(tmpl, items, selected) {
        var result = "";
        for (var k=0; k<items.length; ++k) {
            var item = items[k];
            result += tmpl.format(item.name, item.label, (item.name == selected) ? "SELECTED=''" : "");
        }
        return result;
    }

    function mkThreshold(id, metric, operator, value, action) {
        var optionTmpl    = $j("#option").html();
        var metricItems   = mkOptions(optionTmpl, metrics  , metric);
        var operatorItems = mkOptions(optionTmpl, operators, operator);
        var actionItems   = mkOptions(optionTmpl, actions  , action);

        var thresholdTmpl = $j("#threshold").html();
        var thresholdHtml = thresholdTmpl.format(id, metricItems, operatorItems, value, actionItems);

        return thresholdHtml;
    }

    function insertThreshold(id, metric, operator, value, action) {
        var thresholdHtml = mkThreshold(id, metric, operator, value, action);

        // Append HTML to threshold DIV
        var thresholdsDiv = $j("#thresholds");
        $j(thresholdHtml).appendTo(thresholdsDiv).slideDown("slow");

        // Attach click-handler to Remove button
        $j("#threshold_" + id + "_remove").click(function(){
            var target = $j("#threshold_" + id);
            target.slideUp("slow", function () { target.remove(); });
            return false;
        });

        // Attach change-handler to metrics drop-list
        $j("#threshold_" + id + "_metric").change(function() {
            var valueSelected  = $j(this).find("option:selected").val();
            for (var k=0; k<metrics.length; ++k) {
                if (metrics[k].name == valueSelected) {
                    $j("#threshold_" + id + "_unit").text(metrics[k].unit);
                    break;
                }
            }
            return false;
        }).change();
    }

    $j(document).ready(function(){
        console.log("[LoadImpact] Initializing ...");
        console.log("[LoadImpact] metrics: " + JSON.stringify(metrics, null, 2));
        console.log("[LoadImpact] operators: " + JSON.stringify(operators, null, 2));
        console.log("[LoadImpact] actions: " + JSON.stringify(actions, null, 2));
        console.log("[LoadImpact] thresholds: " + JSON.stringify(thresholds, null, 2));
        console.log("[LoadImpact] chosenTestConfiguration: " + chosenTestConfiguration);

        // Attach click-handler to the 'Add Threshold' button
        $j("#addThreshold").click(function(){
            insertThreshold(nextMetricId++, "user_load_time", "greaterThan", 5000, "unstable");
            return false;
        });

        // Populate stored thresholds
        for (var k = 0; k < thresholds.length; ++k) {
            var t = thresholds[k];
            insertThreshold(nextMetricId++, t.metric, t.operator, t.value, t.action);
        }

        // Attach click-handler to the 'Reload' test-configurations button
        // and invokes it as well during page load
        $j("#tstCfgReload").click(function(){
            var tstCfgSelect = $j("#test\\.configuration\\.id");
            var tstCfgSpinner = $j("#tstCfgSpinner");

            tstCfgSelect.prop("disabled",true);
            tstCfgSpinner.show();
            console.log("Invoking AJAX...");
            $j.getJSON("/loadImpactProxy.html", {"task":"testConfigurations"}, function(cfgs){
                console.log("AJAX response: " + JSON.stringify(cfgs,null,2));
                tstCfgSelect.empty();

                $j.each(cfgs, function(ix, cfg){
                    var selected = (chosenTestConfiguration == cfg.id) ? "SELECTED" : "";
                    var html = $j("#option").html().format(cfg.id, cfg.name+" ("+cfg.url+")", selected);
                    tstCfgSelect.append(html);
                });

                tstCfgSelect.prop("disabled",false);
                tstCfgSpinner.hide();
            });

            return false;
        }).click();

        console.log("[LoadImpact] Initialized");
    });
</script>

<%--
    HTML Contents
--%>
<props:hiddenProperty name="api.token" value="${apiToken}" />

<l:settingsGroup title="Load Test Configuration">
    <tr>
        <th>Test Configuration</th>
        <td>
            <props:selectProperty name="test.configuration.id"> </props:selectProperty>
            <img id="tstCfgSpinner" src="/img/spinner.gif" style="display: none;" />
            <a id="tstCfgReload" href="#" class="btn"><span class="LI_reloadButton">Reload</span></a>
            <span class="smallNote">Choose a test configuration to run.</span>
        </td>
    </tr>
</l:settingsGroup>

<l:settingsGroup title="Failure Criteria">
    <tr>
        <th><label for="delay.value">Start looking after</label></th>
        <td>
            <props:textProperty name="delay.value"/>
            <props:selectProperty name="delay.unit">
                <c:forEach items="${delayUnits}" var="unit">
                    <props:option value="${unit.id}">${unit.displayName}</props:option>
                </c:forEach>
            </props:selectProperty>
            <span class="error" id="error_delay.value"></span>
            <span class="smallNote">Wait for a specified time or number of users, before evaluating the failure criteria thresholds.</span>
        </td>
    </tr>
    <tr>
        <th><label for="delay.size">Use last</label></th>
        <td>
            <props:textProperty name="delay.size"/> metric values
            <span class="error" id="error_delay.size"></span>
            <span class="smallNote">Compute an average of the last N metric values and use for the criteria evaluation.</span>
        </td>
    </tr>
    <tr>
        <th>Abort at failure</th>
        <td>
            <props:checkboxProperty name="abort.failure"/>
            <span class="smallNote">Abort a running load test if the build is marked as a failure, because of an exceeded threshold.</span>
        </td>
    </tr>
</l:settingsGroup>

<l:settingsGroup title="Thresholds">
    <tr>
        <th>
            <a href="#" id="addThreshold" class="btn"><span class="addNew">Add Threshold</span></a>
        </th>
        <td><span class="smallNote">
            You can configure one or more thresholds, which determines if a load-test should be marked as unstable or failed.
        </span></td>
    </tr>
    <tr>
        <th>&nbsp;</th>
        <td id="thresholds"></td>
    </tr>
</l:settingsGroup>

<l:settingsGroup title="Advanced Settings">
    <tr>
        <th><label for="poll.interval">Poll interval</label></th>
        <td>
            <props:textProperty name="poll.interval"/> seconds<br/>
            <span class="error" id="error_poll.interval"></span>
            <span class="smallNote">How often should the ApiToken client poll the LoadImpact server for status during a running load-test</span>
        </td>
    </tr>
</l:settingsGroup>

<l:settingsGroup title="Log/Debug Settings">
    <tr>
        <th><label for="log.http">Log HTTP REST</label></th>
        <td>
            <props:checkboxProperty name="log.http"/>
            <span class="smallNote">Show HTTP response/request headers in the TeamCity console log (debug)</span>
        </td>
    </tr>
    <tr>
        <th><label for="log.replies">Log Replies</label></th>
        <td>
            <props:checkboxProperty name="log.replies"/>
            <span class="smallNote">Show the JSON replies as value-objects in the TeamCity console log (debug)</span>
        </td>
    </tr>
    <tr>
        <th><label for="log.debug">Log Developer DEBUG Messages</label></th>
        <td>
            <props:checkboxProperty name="log.debug"/>
            <span class="smallNote">Show the developer debug messages in the TeamCity console log (debug)</span>
        </td>
    </tr>
</l:settingsGroup>
