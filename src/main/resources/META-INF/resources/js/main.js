let chart
let durationChart
let series = {}
let finishedJobs = {}

$(document).ready(function () {
    let socket;
    chart = Highcharts.chart('job-details', {
        chart: {
            type: 'spline',
            scrollablePlotArea: {
                minWidth: 600,
                scrollPositionX: 1
            }
        },
        title: {
            text: 'Job Workload'
        },
        xAxis: {
            id: 'x',
            min: 0,
            labels: {
                format: '{value}s'
            },
        },
        yAxis: {
            title: {
                text: 'Insertions'
            }
        },
        tooltip: {
            headerFormat: '{point.x}s<br>',
            crosshairs: true,
            shared: true
        },
        plotOptions: {
            spline: {
                marker: {
                    radius: 4,
                    lineColor: '#666666',
                    lineWidth: 1
                }
            }
        },
        credits: {
            enabled: false
        },
    });

    durationChart = Highcharts.chart('job-duration', {
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Job Duration'
        },
        yAxis: {
            min: 0,
            labels: {
                format: '{value}s'
            },
        },
        series: [{
            name: 'Finished Jobs',
            data: []
        }],
        credits: {
            enabled: false
        },
    })

    let createChartData = function (job) {
        let data = []
        job.statistics.snapshots.forEach(snapshot => {
            data.push(createPoint(snapshot))
        })
        return data;
    }

    let createPoint = function (snapshot) {
        return [snapshot.instant, snapshot.count]
    }

    let updateJob = function (job) {
        let jobDetails = function (job) {
            let rowClass;
            switch (job.state) {
                case "WAITING":
                    rowClass = "secondary"
                    break;
                case "RUNNING":
                    rowClass = "active"
                    break;
                case "SUCCESS":
                    rowClass = "success"
                    break;
                case "FAIL":
                    rowClass = "danger"
                    break;
            }
            let html = "<tr id='job-" + job.id + "' class='job-row table-" + rowClass + "'>"
            html += "<th scope='row'>" + job.id + "</th>"
            html += "<td>" + job.definition.name + "</td>"
            html += "<td>" + job.definition.cache + "</td>"
            html += "<td>" + job.definition.entries + "</td>"
            html += "<td>" + job.definition.size + "</td>"
            html += "<td>" + job.definition.threads + "</td>"
            html += "<td>" + job.statistics.count + "</td>"
            if (job.statistics.elapsedTime) {
                html += "<td>" + job.statistics.elapsedTime.displayValue + "</td>"
            } else {
                html += "<td></td>"
            }
            html += "<td>" + (job.statistics.progress * 100).toFixed(2) + "</td>"
            html += "<td>" + job.state + "</td>"
            html += "</tr>"
            return html
        }

        let jobItem = $("#job-" + job.id)
        if (jobItem.length) {
            jobItem.replaceWith(jobDetails(job))
        } else {
            $("#job-list tbody").append(jobDetails(job));
        }

        if (series[job.id]) {
            let jobChartRow = series[job.id]
            let snapshots = job.statistics.snapshots
            for (let i = jobChartRow.data.length; i < snapshots.length; i++) {
                jobChartRow.addPoint(createPoint(snapshots[i]))
            }
        } else {
            series[job.id] = chart.addSeries({
                name: job.definition.name,
                data: createChartData(job)
            }, false)
        }

        if (job.state === "SUCCESS") {
            finishedJobs[job.id] = job
        }
    };

    $("#job-submit").click(function () {
        payload = [{
            "name": $("#job-name").val(),
            "cache": $("#job-cache").val(),
            "entries": $("#job-entries").val(),
            "size": $("#job-size").val(),
            "snapshotInterval": $("#job-snapshot").val(),
            "threads": $("#job-threads").val()
        }]
        $.ajax({
            url: "jobs",
            type: "POST",
            contentType: 'application/json; charset=utf-8',
            dataType: "json",
            data: JSON.stringify(payload)
        });
    })

    let updateDurationChart = function () {
        let categories = []
        let data = []
        for (let id in finishedJobs) {
            categories.push(finishedJobs[id].definition.name)
            data.push(finishedJobs[id].statistics.duration)
        }
        durationChart.xAxis[0].setCategories(categories)
        durationChart.series[0].setData(data, false)
    }

    $.ajax({
        url: "jobs",
        type: "GET",
        dataType: "json",
        success: function (data) {
            data.values.forEach(updateJob)
            updateDurationChart()
            chart.redraw()
            durationChart.redraw()
        }
    });

    socket = new WebSocket("ws://" + location.host + "/jobs");
    socket.onopen = function () {
        console.log("Connected to the web socket");
    };
    socket.onmessage = function (m) {
        console.log("Got message: " + m.data);
        let jobEvent = JSON.parse(m.data);
        let job = jobEvent.job;
        updateJob(job)
        updateDurationChart()
        chart.redraw()
        durationChart.redraw()
    };
})