<script lang="ts">
    import Pair from "./pair/Pair.svelte";
    import { serverUrl } from "./main";

    let chart: CommonPairContribution[] = [];
    let branches: string[] = [];
    let isLoading = false;
    let currentBranch: string = "select branch";

    async function handleSelectBranchChange(event: Event) {
        currentBranch = (event.target as HTMLSelectElement).value;
        isLoading = true;
        const response = await fetch(
            `${serverUrl}/api/getChart/${currentBranch}`
        );

        const data = await response.json();
        chart = data;
        isLoading = false;
    }

    if (serverUrl != null) {
        fetch(`${serverUrl}/api/getBranches`).then((response) => {
            response.json().then((data) => {
                branches = data;
            });
        });
    }
</script>

<main>
    {#if serverUrl == null}
        <h1>No serverUrl provided</h1>
    {:else if isLoading}
        <div class="loader"></div>
    {:else}
        <div class="select">
            <select
                on:change={handleSelectBranchChange}
                name="branch"
                id="branch"
                value={currentBranch}
            >
                <option disabled selected value="select branch">
                    select branch
                </option>
                {#each branches as branch}
                    <option value={branch}>{branch}</option>
                {/each}
            </select>
        </div>
        <div id="chart">
            {#each chart as pair, id}
                <Pair {id} {pair} {currentBranch} />
            {/each}
        </div>
    {/if}
</main>

<style>
</style>
