# edn-diff

This skill should be used when the user asks to "compare EDN files",
"diff EDN", "see EDN changes between branches", mentions "edn-diff",
discusses configuration changes between git branches, or wants to
compare EDN data structures. Provides guidance for using the edn-diff
Babashka script to compare EDN files between git branches.

## Overview

edn-diff is a Babashka tool that compares EDN (Extensible Data
Notation) files between different git branches. It's particularly
useful for:

- Tracking configuration changes across branches
- Comparing data structures between commits
- Identifying added or removed keys/values in EDN files
- Automated EDN comparison in scripts

## When to Use This Skill

Invoke this skill when the user:
- Requests to compare EDN files between git branches
- Wants to see what changed in an EDN file
- Asks about configuration differences
- Mentions diffing EDN data structures
- Needs to track changes in Clojure/ClojureScript configuration files

## Installation

Install via bbin:

```bash
bbin install io.github.200ok-ch/edn-diff
```

## Basic Usage

### Compare with default branch (master)

```bash
edn-diff config.edn
```

### Compare with specific branch

```bash
edn-diff -b=develop config.edn
```

### Show only added items

```bash
edn-diff -a config.edn
```

### Show only removed items

```bash
edn-diff -r config.edn
```

### Output in EDN format (for scripting)

```bash
edn-diff -e config.edn
```

### Combine options

```bash
edn-diff -b=develop -a -e config.edn
```

## Options Reference

| Option                  | Description                        | Default |
|-------------------------+------------------------------------+---------|
| `-b, --branch=<branch>` | Branch to compare with             | master  |
| `-a, --added-only`      | Show only added items              | -       |
| `-r, --removed-only`    | Show only removed items            | -       |
| `-e, --edn-output`      | Output in EDN format for scripting | -       |
| `-h, --help`            | Show help message                  | -       |

## Common Use Cases

### 1. Track configuration changes

When working on a feature branch, see how your configuration differs from the main branch:

```bash
edn-diff -b=main deps.edn
```

### 2. Find new dependencies added in your branch

```bash
edn-diff -a -b=main deps.edn
```

### 3. Check what was removed

```bash
edn-diff -r -b=main config.edn
```

### 4. Script integration

Use `-e` flag to get EDN output for programmatic processing:

```bash
edn-diff -e config.edn | jq
```

## How the Tool Works

1. Reads the EDN file from the current git branch
2. Reads the same EDN file from the specified comparison branch
3. Computes the diff using either:
   - `clojure.data/diff` for simple diffs (returns [only-in-a only-in-b common])
   - `lambdaisland.deep-diff2` for detailed pretty-printed diffs (default output)
4. Formats and outputs the result

## Tips

- Always check the current branch with `git branch --show-current` to confirm what you're comparing
- Use `-e` flag when you need to parse the output in scripts or other tools
- For large EDN files, consider using `-a` or `-r` to filter to just the changes you care about
- The tool works with any EDN file including deps.edn, config.edn, or custom data files

## Examples

### Example 1: Comparing deps.edn

```bash
$ edn-diff deps.edn
=> Shows detailed diff between current branch and master
```

### Example 2: Finding added dependencies

```bash
$ edn-diff -a deps.edn
{:paths [...]
 :deps {org.clojure/clojure {:mvn/version "1.11.1"}
        new-lib/new-lib {:mvn/version "1.0.0"}}}
```

### Example 3: Script-friendly output

```bash
$ edn-diff -e -a deps.edn
{:deps {new-lib/new-lib {:mvn/version "1.0.0"}}}
```
