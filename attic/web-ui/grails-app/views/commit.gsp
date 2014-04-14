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
<html>
<head>
    <meta name="layout" content="main">
    <title>Commit ${params.commitId}</title>
</head>
<body>

    <h4>${params.commitId}</h4>
    <b>Class</b>: <a href="/web-ui?className=${session.className}&metric=${session.metricType}">${session.className}</a><br/>
    <b>Metric</b>: ${session.metricType}<br/>

    <form id="form" method="get">
        <input type="hidden" name="commitId" value="${params.commitId}"/>

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

    <br/>
    <br/>

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
            <button id="editNoteButton" class="btn btn-mini" type="button" onclick="editNote()">Edit</button>
            <button id="saveNoteButton" class="btn btn-mini" type="button" onclick="saveNote()" style="display: none">Save</button>
            <button id="cancelNoteButton" class="btn btn-mini" type="button" onclick="cancelNote()" style="display: none">Cancel</button>
        </div>
    </div>

    <hr/>

    <div class="row">
        <div class="span5">
            <b>Runners:</b><br/>
            <select id="runners" size="10" style="width: 100%" onclick="showRunnerInfo();">
            </select>
        </div>
        <div class="span7">
            <b>Runner's Summary:</b><br/>
            <span id="runnerInfo"></span>
        </div>
    </div>

<script class="code" type="text/javascript">

    var currentRun = 0;
    var runInfo = ${runInfo};
    var RUN_LINK = "<a href='javascript:void(0);' onclick='openRunDetails(\"runNumber\");'>run#: runNumber</a>";

    function editNote() {
        $("#note").removeAttr('readonly');
        $("#editNoteButton").hide();
        $("#saveNoteButton").show();
        $("#cancelNoteButton").show();
    }

    function cancelNote() {
        makeNoteReadOnly();
    }

    function makeNoteReadOnly() {
        $("#note").attr('readonly','readonly');
        $("#editNoteButton").show();
        $("#saveNoteButton").hide();
        $("#cancelNoteButton").hide();
    }

    function saveNote() {

        var noteText = $('#note').val();
        console.log(noteText);

        $.ajax({
            url: "note/save",
            type: "POST",
            data: { commitId: "${params.commitId}", runNumber: currentRun, note: noteText }
        })
        .done(function(responseText) {
            console.log(responseText);
        });

        makeNoteReadOnly();
    }

    function showNote(pointInfo) {
        $.ajax({
            url: "note/get",
            type: "POST",
            data: { commitId: pointInfo.commitId, runNumber: pointInfo.runNumber }
        })
        .done(function(responseText) {
            console.log(responseText);
            $('#note').val(responseText);
        });
    }

    function reload() {
        $('#form').submit()
    }

    function pointClicked(point) {
        showPointInfo(point.info);
        showRunners(point.info);
        showNote(point.info);
    }

    function showRunners(pointInfo) {

        $('#runners').empty();
        $("#runnerInfo").html("");

        currentRun = pointInfo.runNumber;
        var runJson = runInfo[currentRun];

        $.each(runJson, function(key, value) {
            var s = "<option value='" + key + "'>" + key + "</option>";
            $('#runners').append(s);
        });
    }

    function showRunnerInfo() {

        if (currentRun == 0) {
            return;
        }

        var runner = $("#runners").find(':selected').text();
        var text = jsonToString( runInfo[currentRun][runner] );

        $("#runnerInfo").html(text);
    }

    function jsonToString(json) {

        var s = "";

        $.each(json, function(k, v) {
            s += k + ": " + v + "<br/>";
        });

        return s;
    }

    function showPointInfo(info) {

        var text = "- chopType: " + info.chopType
                + "<br/>- " + RUN_LINK.replace(/runNumber/g, info.runNumber)
                + "<br/>- runners: " + info.runners
                + "<br/>- totalTestsRun: " + info.totalTestsRun
                + "<br/>- iterations: " + info.iterations
                + "<br/>- failures: " + info.failures
                + "<br/>- ignores: " + info.ignores
                + "<br/>- value: " + info.value;

        $("#info").html(text);
    }

    function openRunDetails(runNumber) {
        document.location = "run?commitId=${params.commitId}&runNumber=" + runNumber;
    }

    $(document).ready(function() {

        $('#percentile').val('${params.percentile}');
        $('#pointFilter').val('${params.pointFilter}');

        $('#chart').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: 'Runs in commit ${params.commitId}'
            },
            xAxis: {
                title: {
                    text: 'Runs'
                }
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