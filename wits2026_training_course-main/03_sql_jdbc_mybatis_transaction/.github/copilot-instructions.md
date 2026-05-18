# Copilot Instructions

## Scope

These instructions apply to the training course under `03_sql_jdbc_mybatis_transaction`.

## Project Intent

This repository is a course package, not only an application codebase.

- Most folders are teaching materials in Markdown.
- `jdbc-mybatis-demo` is the runnable Spring Boot example.
- Changes should preserve teaching value, not only technical correctness.

## First Read

Before changing anything, identify which area the task belongs to:

- Course overview and learner guidance: [README.md](../README.md)
- Exercises and evaluation: [exercises/README.md](../exercises/README.md)
- Instructor flow: [instructor-guide/README.md](../instructor-guide/README.md)
- Runnable demo: [jdbc-mybatis-demo/README.md](../jdbc-mybatis-demo/README.md)

## Working Rules

1. Prefer linking to existing module or exercise documents instead of duplicating large sections.
2. Preserve Traditional Chinese teaching tone in course Markdown unless the user asks for English.
3. Keep code examples compatible with Java 8 and Spring Boot 2.7.x.
4. Keep MyBatis and JDBC examples explicit and readable for training purposes.
5. Avoid large structural rewrites across modules unless the task is explicitly about curriculum redesign.

## Runnable Demo Commands

Run these from `jdbc-mybatis-demo`:

- Tests: `mvn test`
- App: `mvn spring-boot:run`

## Environment Facts

- Main app uses MySQL 8 on `localhost:3306`
- Default database: `training_jdbc_demo`
- Main app credentials: `root` / `root`
- Main app port: `8081`
- Tests use H2 in-memory database and do not require MySQL

Relevant files:

- [jdbc-mybatis-demo/src/main/resources/application.properties](../jdbc-mybatis-demo/src/main/resources/application.properties)
- [jdbc-mybatis-demo/src/test/resources/application.properties](../jdbc-mybatis-demo/src/test/resources/application.properties)

## Demo Code Conventions

- Complex joins and reporting queries belong in MyBatis mapper + XML.
- Low-level transaction behavior should remain visible in JDBC-focused examples.
- Use MyBatis Plus for simple single-table patterns; do not force it into complex join teaching examples.
- When changing transaction examples, verify both exception behavior and final data state.

## Documentation Conventions

- Top-level README is the entry point.
- Module READMEs contain the detailed teaching content.
- Exercises and instructor guide should be referenced, not copied into new summary files.
- If a new document is added, explain why it exists and who should read it first.

## Common Pitfalls

- `@Transactional` examples can fail because of self-invocation, exception type mismatch, or wrong transaction boundary.
- `spring.sql.init.mode=never` in the main app means MySQL must be initialized manually.
- Slow query discussions should distinguish SQL structure, index design, and transaction settings.

## Good Task Outcomes

- A learner can find the right document quickly.
- A trainer can use the updated material without losing the teaching narrative.
- A coding agent can run the correct command without guessing environment details.