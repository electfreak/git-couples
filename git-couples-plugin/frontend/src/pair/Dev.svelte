<script lang="ts">
    export let dev: Developer;
</script>

<div class="dev">
    <div class="email">
        {dev.email}
    </div>

    {#each dev.names as name}
        <div class="name">
            {name}
        </div>
    {/each}
</div>

<style>
  .email {
    font-weight: 500;
    font-size: 18px;
    margin-bottom: 12px;
  }

  .name {
    font-size: 15px;
  }
</style>
