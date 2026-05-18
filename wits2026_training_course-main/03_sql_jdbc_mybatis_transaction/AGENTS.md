# Agent Guide

## Purpose

This folder contains a training course for SQL, JDBC, MyBatis, and transaction control.

An agent working here should treat the repository as two things at once:

- a documentation-heavy course package
- a small runnable Spring Boot demo under `jdbc-mybatis-demo`

## Start Here

- Course entry: [README.md](README.md)
- Copilot-specific instructions: [.github/copilot-instructions.md](.github/copilot-instructions.md)
- Demo guide: [jdbc-mybatis-demo/README.md](jdbc-mybatis-demo/README.md)
- Exercises: [exercises/README.md](exercises/README.md)
- Instructor guide: [instructor-guide/README.md](instructor-guide/README.md)

## Safe Defaults

1. Prefer minimal edits.
2. Preserve existing teaching structure and module boundaries.
3. Link to existing docs instead of duplicating module content.
4. Keep documentation in Traditional Chinese unless the user asks otherwise.
5. Keep runnable demo code compatible with Java 8 and Spring Boot 2.7.x.

## Commands

Run these from `jdbc-mybatis-demo`:

- `mvn test`
- `mvn spring-boot:run`

## Environment Notes

- App runtime expects MySQL 8 at `localhost:3306/training_jdbc_demo`
- Default credentials are `root` / `root`
- App runs on port `8081`
- Tests use H2 and should work without MySQL

## Editing Heuristics

- If the request is about curriculum, start from README files.
- If the request is about SQL or transaction behavior, inspect both code and test coverage.
- If the request touches rollback behavior, do not stop at exception handling; confirm expected final data state.
- If the request touches MyBatis Plus, check whether the use case is still simple enough to avoid obscuring SQL learning goals.