# Common contribution chart between pairs of developers

## How to run
To run the plugin, run the `:git-couples-plugin:plugin:runIde` task in gradle in the manner that suits you. For example, in a unix environment:
```shell
cd path/to/git-couples
./gradlew :git-couples-plugin:plugin:runIde
```

## Decisions were made:
- I recognize developers by their emails. Many developers commit from different devices and have different names in their config. Unfortunately, emails are sometimes also different, but emails differ less often than names.
- I only count the contribution from `author` (I do not consider `committer`)
- I only count commits from specific (selected) branch
- Only contributions to files are counted
- I chose the plugin to familiarize myself with developing for the intellij platform and also chose jcef because it was just interesting and I love browsers. For a real project, perhaps swing would have been a better choice.

## Possibilities
I tried to write the code so that I could write an adapter for any type of repository (e.g. using a github api rather than a locally cloned repository) and reuse the logic.
You can also reuse the adapter for a cloned repository and very quickly write a CLI application, for example.

## Complexity
I can roughly estimate the complexity of the algorithm like this:

`O(k * m + n^2 * m)`, where
- `k` – number of commits in a branch
- `m` – the largest number of files in the history of commits in the branch
- `n` – number of developers

## Improvements 
The obvious improvement is to keep track of file name/path changes. Now, if a file is changed or moved, it is a different file and the contribution to it is counted separately. To track file changes, we need to write some code, possibly change the logic.