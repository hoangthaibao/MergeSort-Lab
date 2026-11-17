
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ChartGenerator {

    public static void main(String[] args)
            throws IOException {

        Path csv = Path.of(
                "results", "experiment_results.csv");

        if (!Files.exists(csv)) {
            System.out.println("CSV not found!");
            return;
        }

        List<String> lines = Files.readAllLines(csv);
        StringBuilder data = new StringBuilder("[");

        for (int i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) continue;

            String[] p = lines.get(i).split(",");

            if (p.length != 6) {
                throw new IOException("Invalid CSV row: " + i);
            }

            if (data.length() > 1) data.append(",");

            data.append(String.format(
                    java.util.Locale.US,
                    "{\"type\":\"%s\",\"size\":%d," +
                            "\"cutoff\":%d,\"avg\":%s," +
                            "\"median\":%s,\"std\":%s}",
                    p[0],
                    Integer.parseInt(p[1]),
                    Integer.parseInt(p[2]),
                    Double.toString(Double.parseDouble(p[3])),
                    Double.toString(Double.parseDouble(p[4])),
                    Double.toString(Double.parseDouble(p[5]))
            ));
        }

        data.append("]");

        String html = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport"
              content="width=device-width,initial-scale=1">
        <title>MergeSort Lab Dashboard</title>
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
          * { box-sizing: border-box; }
          body {
            margin: 0;
            background: #0b1220;
            color: #edf2fa;
            font-family: Arial, sans-serif;
          }
          .container {
            max-width: 1150px;
            margin: auto;
            padding: 36px 22px;
          }
          h1 { margin-bottom: 4px; font-size: 34px; }
          .sub { color: #94a3b8; }
          .panel, .metric {
            background: #172235;
            border: 1px solid #29394e;
            border-radius: 16px;
            padding: 22px;
          }
          .panel { margin-top: 24px; }
          .filters, .metrics {
            display: grid;
            gap: 16px;
            margin-top: 25px;
          }
          .filters { grid-template-columns: repeat(2,1fr); }
          .metrics { grid-template-columns: repeat(3,1fr); }
          label {
            display: block;
            color: #94a3b8;
            margin-bottom: 10px;
          }
          select {
            width: 100%;
            padding: 12px;
            background: #0b1220;
            color: white;
            border: 1px solid #475569;
            border-radius: 9px;
          }
          .metric span { color: #94a3b8; }
          .metric h2 {
            margin: 14px 0 0;
            color: #38bdf8;
            font-size: 30px;
          }
          .chartbox { height: 380px; }
          table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 18px;
          }
          th, td {
            padding: 13px;
            border-bottom: 1px solid #29394e;
            text-align: left;
          }
          th { color: #94a3b8; }
          .best { color: #4ade80; font-weight: bold; }
          .scroll { overflow-x: auto; }
          @media(max-width:650px) {
            .metrics, .filters { grid-template-columns: 1fr; }
          }
        </style>
        </head>

        <body>
        <div class="container">
          <h1>MergeSort Lab</h1>
          <p class="sub">
            Finding the Optimal Cutoff — Experimental Dashboard
          </p>

          <div class="filters">
            <div>
              <label>Dataset Type</label>
              <select id="type"></select>
            </div>
            <div>
              <label>Array Size</label>
              <select id="size"></select>
            </div>
          </div>

          <div class="metrics">
            <div class="metric">
              <span>Best CUTOFF</span>
              <h2 id="best">--</h2>
            </div>
            <div class="metric">
              <span>Median Runtime</span>
              <h2 id="runtime">--</h2>
            </div>
            <div class="metric">
              <span>Time Reduction vs Pure MergeSort</span>
              <h2 id="improvement">--</h2>
            </div>
          </div>

          <div class="panel">
            <h2>Cutoff Performance</h2>
            <p class="sub">Lower runtime is better</p>
            <div class="chartbox">
              <canvas id="chart"></canvas>
            </div>
          </div>

          <div class="panel">
            <h2>Detailed Results</h2>
            <div class="scroll">
              <table>
                <thead>
                  <tr>
                    <th>CUTOFF</th>
                    <th>Mean (ms)</th>
                    <th>Median (ms)</th>
                    <th>Std Dev (ms)</th>
                  </tr>
                </thead>
                <tbody id="table"></tbody>
              </table>
            </div>
          </div>

          <div class="panel">
            <h2>Experiment Notes</h2>
            <p class="sub">
              Results from Java MergeSort with Insertion Sort cutoff.
              Each configuration uses repeated measurements.
              Best cutoff is selected by lowest median runtime.
              All results depend on the dataset and machine.
            </p>
          </div>
        </div>

        <script>
        const records = __DATA__;
        const typeSelect = document.getElementById("type");
        const sizeSelect = document.getElementById("size");

        const types = [...new Set(records.map(x => x.type))];
        const sizes = [...new Set(records.map(x => x.size))]
                        .sort((a,b) => a-b);

        types.forEach(x => typeSelect.add(new Option(x,x)));
        sizes.forEach(x =>
          sizeSelect.add(new Option(x.toLocaleString(),x))
        );

        typeSelect.value = types.includes("RANDOM")
          ? "RANDOM" : types[0];

        sizeSelect.value = sizes.includes(100000)
          ? "100000" : String(sizes[0]);

        let chart = null;

        function update() {
          const rows = records.filter(x =>
            x.type === typeSelect.value &&
            x.size === Number(sizeSelect.value)
          ).sort((a,b) => a.cutoff-b.cutoff);

          if (!rows.length) return;

          const best = rows.reduce((a,b) =>
            b.median < a.median ? b : a
          );

          const baseline = rows.find(x => x.cutoff === 0);

          const improvement = baseline && baseline.median > 0
            ? (baseline.median-best.median)
                / baseline.median*100 : 0;

          document.getElementById("best").textContent =
            best.cutoff;

          document.getElementById("runtime").textContent =
            best.median.toFixed(3)+" ms";

          document.getElementById("improvement").textContent =
            improvement.toFixed(2)+"%";

          document.getElementById("table").innerHTML =
            rows.map(x => `
              <tr class="${x.cutoff===best.cutoff?'best':''}">
                <td>${x.cutoff}</td>
                <td>${x.avg.toFixed(3)}</td>
                <td>${x.median.toFixed(3)}</td>
                <td>${x.std.toFixed(3)}</td>
              </tr>
            `).join("");

          if (chart) chart.destroy();

          chart = new Chart(
            document.getElementById("chart"), {
              type: "line",
              data: {
                labels: rows.map(x => x.cutoff),
                datasets: [{
                  label: "Median Runtime (ms)",
                  data: rows.map(x => x.median),
                  borderColor: "#38bdf8",
                  backgroundColor: "#38bdf8",
                  tension: 0.2,
                  pointRadius: 5
                }]
              },
              options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                  legend: { labels: {color:"#edf2fa"} }
                },
                scales: {
                  x: {
                    ticks: {color:"#94a3b8"},
                    grid: {color:"#29394e"},
                    title: {
                      display:true,
                      text:"CUTOFF",
                      color:"#edf2fa"
                    }
                  },
                  y: {
                    ticks: {color:"#94a3b8"},
                    grid: {color:"#29394e"},
                    title: {
                      display:true,
                      text:"Runtime (ms)",
                      color:"#edf2fa"
                    }
                  }
                }
              }
            }
          );
        }

        typeSelect.addEventListener("change",update);
        sizeSelect.addEventListener("change",update);

        update();
        </script>
        </body>
        </html>
        """;

        html = html.replace("__DATA__", data.toString());

        Path output = Path.of(
                "results", "dashboard.html");

        Files.writeString(output, html);

        System.out.println("Dashboard created!");
        System.out.println(output.toAbsolutePath());
    }
}
