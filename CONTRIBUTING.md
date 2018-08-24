# Contributing guidelines:

## For Issues (bugs or feature requests): 

1. If it's a bug, please clearly state what is the actual behaviour and what is the expected one. A PR with a failing unit test, to prove its existence, would be highly appreciated.

2. If it's a feature request, clearly state what is missing with a link to Docker's API documentation of said method(s). A PR here, with the start of the solution, would also be appreciated (see PR rules below). 

## For Pull Requests:

1. Small PRs! Ideally max. 5 changed files. You shouldn't spend more than 30min on it.

2. Please, stay object oriented. Try to understand the library's architecture before you do anything. If you have a hard time wrapping your head around it, feel free to open Issues and we'll explain everything.

3. No code changes without at least one type of test (unit test or integration test).

4. PRs that don't pass the Travis build, including Checkstyle verification, won't be accepted.
