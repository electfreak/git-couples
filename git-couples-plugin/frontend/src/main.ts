console.log("Hello world world!");
const serverUrl = new URLSearchParams(window.location.search).get("serverUrl");

const selectBranchEl = document.getElementById("branch") as HTMLSelectElement;
const chartEl = document.getElementById("chart") as HTMLDivElement;
const loaderEl = document.createElement("div") as HTMLDivElement;
loaderEl.classList.add("loader");

function drawChart(data: CommonPairContribution[]) {
    data.forEach((pair, id) => {
        const pairEl = document.createElement("div");
        pairEl.innerHTML = `
            <h2><span class="and">${id + 1}</span> ${
            pair.devA.name
        } <span class="and">and</span> ${pair.devB.name}</h2>`;
        //     <ul>
        //         ${pair.intersectedContribution.map((contribution) => {
        //             return `<li>${contribution.filePath} - ${contribution.fileChangeCommitCountMap}</li>`;
        //         })}
        //     </ul>
        // `;
        chartEl.appendChild(pairEl);
    });
}

function clearChart() {
    chartEl.innerHTML = "";
}

function toggleSpinner() {
    console.log("toggleSpinner");
    if (chartEl.contains(loaderEl)) {
        chartEl.removeChild(loaderEl);
    } else {
        chartEl.append(loaderEl);
    }
}

selectBranchEl.onchange = () => {
    const branch = selectBranchEl.value;
    console.log("run fetch", branch);
    clearChart();
    toggleSpinner();
    fetch(`${serverUrl}/api/getChart/${branch}`).then((response) => {
        response.json().then((data) => {
            console.log("data received", data);
            toggleSpinner();
            drawChart(data);
        });
    });
};

if (serverUrl == null) {
    document.body.append(
        document
            .createElement("h1")
            .appendChild(document.createTextNode("No serverUrl provided"))
    );
} else {
    fetch(`${serverUrl}/api/getBranches`).then((response) => {
        response.json().then((data) => {
            data.forEach((branch: string) => {
                const option = document.createElement("option");
                option.value = branch;
                option.text = branch;
                selectBranchEl.appendChild(option);
            });
        });
    });
}
