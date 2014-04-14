%{--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
--}%
<%@ page import="org.chop.service.data.ProjectInfo" %>

<html>
<head>
    <title>Judo Chop</title>
</head>
<body>

<center>
    <form id="form" method="get">
        <g:select id="className" name="className" from="${classNames}" value="name" optionValue="value" optionKey="value" style="width: 400px;" onchange="reload()"/>

        <select id="metric" name="metric" onchange="reload()">
            <option value="AVG">Avg Time</option>
            <option value="MIN">Min Time</option>
            <option value="MAX">Max Time</option>
            <option value="ACTUAL">Actual Time</option>
        </select>

        <br/>

        Percentile:
        <select id="percentile" name="percentile" onchange="reload()">
            <option value="100">100</option>
            <option value="90">90</option>
            <option value="80">80</option>
            <option value="70">70</option>
            <option value="60">60</option>
            <option value="50">50</option>
            <option value="40">40</option>
            <option value="30">30</option>
            <option value="20">20</option>
            <option value="10">10</option>
        </select>

        Interation Points to Plot:
        <select id="pointFilter" name="pointFilter" onchange="reload()">
            <option value="ALL">All</option>
            <option value="FAILED">Only Failure</option>
            <option value="SUCCESS">Only Success</option>
        </select>

    </form>
</center>

<div class="row">
    <div class="span9">
        <div id="chart"></div>
    </div>
    <div class="span3">
        <b>Details</b>:
        <div id="info"></div>
        <br/>

        <b>Note</b>:
        <textarea id="note" rows="10" cols="45" readonly></textarea>
    </div>
</div>

<b>Project Version:</b> ${ProjectInfo.PROPS.get("project.version")}<br/>
<b>Artifact Id:</b> ${ProjectInfo.PROPS.get("artifact.id")}<br/>
<b>Group Id:</b> ${ProjectInfo.PROPS.get("group.id")}<br/>
<b>Url:</b> http://${ProjectInfo.PROPS.get('git.url')}/commits/<span id="urlCommitId"></span>
<br/>

<script class="code" type="text/javascript">

    var commitInfo = ${ProjectInfo.getCommitInfo()};

    var COMMIT_LINK = "<a href='javascript:void(0);' onclick='openCommitDetails(\"commitId\");'>commitId</a>";

    function reload() {
        $('#form').submit()
    }

    function pointClicked(point) {
        showPointInfo(point.info);
        updateCommitUrl(point.info.commitId);
        showNote(point.info);
    }

    function updateCommitUrl(commitId) {
        var commitFullId = commitInfo[commitId]
        $("#urlCommitId").text(commitFullId);
    }

    function showPointInfo(info) {

        var text = "- chopType: " + info.chopType
            + "<br/>- commitId: " + COMMIT_LINK.replace(/commitId/g, info.commitId)
            + "<br/>- runNumber: " + info.runNumber
            + "<br/>- runners: " + info.runners
            + "<br/>- totalTestsRun: " + info.totalTestsRun
            + "<br/>- iterations: " + info.iterations
            + "<br/>- failures: " + info.failures
            + "<br/>- ignores: " + info.ignores
            + "<br/>- value: " + info.value;

        $("#info").html(text);
    }

    function showNote(pointInfo) {
        $.ajax({
            url: "note/get",
            type: "POST",
            data: { commitId: pointInfo.commitId, runNumber: pointInfo.runNumber }
        })
        .done(function(responseText) {
            $('#note').val(responseText);
        });
    }

    function openCommitDetails(commitId) {
        document.location = "commit?commitId=" + commitId;
    }

    $(document).ready(function() {

        $('#className').val('${params.className}');
        $('#metric').val('${params.metric}');
        $('#percentile').val('${params.percentile}');
        $('#pointFilter').val('${params.pointFilter}');

        $('#chart').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: 'Commits / Runs'
            },
            xAxis: {
                title: {
                    text: 'Commits'
                },
                categories: ${commitDirs}
            },
            yAxis: {
                title: {
                    text: 'Time'
                },
                labels: {
                    formatter: function() {
                        return this.value + ' ms'
                    }
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
                enabled: false
            },
            plotOptions: {
                series: {
                    cursor: 'pointer',
                    marker: {
                        enabled: true,
                        symbol: 'circle',
                        radius: 4,
                        lineColor: '#666666',
                        lineWidth: 1,
                        fillColor: 'white'
                    },
                    point: {
                        events: {
                            click: function() {
                                pointClicked(this)
                            }
                        }
                    }
                }
            },
            series: [${series}]
        });

    });
</script>

</body>