import App from "./App.svelte";

export const serverUrl = new URLSearchParams(window.location.search).get(
    "serverUrl"
);

const app = new App({
    target: document.getElementById("app")!,
});

export default app;
