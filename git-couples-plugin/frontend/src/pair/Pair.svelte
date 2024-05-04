<script lang="ts">
    import { serverUrl } from "../main";
    import Dev from "./Dev.svelte";

    export let id: number;
    export let pair: CommonPairContribution;
    export let currentBranch: string;
    let commonContribution: ContributionToFile[] | null;

    async function toggleCommonContribuntion() {
        if (commonContribution) {
            commonContribution = null;
            return;
        }

        const response = await fetch(
            `${serverUrl}/api/getIntersectedContribution?branch=${currentBranch}&id=${id}`
        );

        const data = (await response.json()) as ContributionToFile[];
        commonContribution = data;
    }
</script>

<div class="pair">
    <span class="number">
        {id + 1}
    </span>
    <div>
        <Dev dev={pair.devA} />
        <span class="and">and</span>
        <Dev dev={pair.devB} />
        <button on:click={toggleCommonContribuntion} class="show-contribution">
            {#if !commonContribution}
                show
            {:else}
                hide
            {/if}

            common contribution
        </button>

        {#if commonContribution}
            <div>
                {#each commonContribution as contribution}
                    <div>
                        {contribution.filePath}: {contribution.fileChangeCommitCount}
                    </div>
                {/each}
                {#if commonContribution.length === 0}
                    <div>
                        No common contribution
                    </div>
                {/if}
            </div>
        {/if}
    </div>
</div>

<style>
    .number {
        margin-right: 15px;
    }

    .pair {
        display: flex;
        align-items: flex-start;
        margin-bottom: 20px;
    }

    .show-contribution {
        cursor: pointer;
        margin-top: 15px;
        display: block;
        padding: 0;
        appearance: none;
        border: none;
        background: transparent;
        font-size: 18px;
        color: var(--orange);
    }

    .and {
        margin: 15px 0;
        display: inline-block;
    }

    .and,
    .number {
        background-color: rgba(39, 40, 44, 0.05);
        color: rgba(39, 40, 44, 0.7);
        padding: 5px 8px;
        border-radius: 4px;
        font-size: 12px;
    }
</style>
