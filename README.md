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


TODO:
- Clone repo size restrictions/warnings?
- Plugin
- Front-end
- Back-end
- CLI