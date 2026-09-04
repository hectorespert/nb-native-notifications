# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project

See [README.md](README.md) for what the plugins do and where they are published. In short, this is a
two-module Maven build producing Apache NetBeans plugins (NBM modules):

- `linux/` — the Linux native notifications plugin.
- `quote/` — the Yoda Quote notification generator.

## Build commands

Always use the Maven wrapper (`./mvnw`). Builds run on JDK 21 in CI and compile to Java 21 bytecode
(`<release>21</release>` in the root pom). JDK 21 is the floor, not a preference: the
`org.netbeans.*` artifacts for RELEASE310 ship Java 21 class files, so anything lower fails to
compile against them.

```bash
./mvnw clean verify            # full build, what CI runs on PRs
./mvnw -pl linux verify        # single module
./mvnw -pl linux test -Dtest=LinuxNotificationDisplayerTest          # one test class
./mvnw -pl linux test -Dtest=LinuxNotificationDisplayerTest#testNotify  # one test method
```

`verify` produces `<module>/target/<module>-<version>.nbm` (the installable plugin) alongside the jar,
sources jar and javadoc jar. NBM signing is off locally; CI passes `-Dkeystore`, `-Dkeystorepass`
and `-Dkeystorealias` from secrets.

Only `linux` has tests. `quote` has none.

## Versioning

The root pom uses CI-friendly versions: `${revision}${sha1}${changelist}`, resolved at build time by
`flatten-maven-plugin` (`resolveCiFriendliesOnly`). Consequences:

- Bump the version by editing `<revision>` in the root `pom.xml` only — never the module poms.
- `.flattened-pom.xml` files are generated build output; do not hand-edit them.
- Releases are cut by creating a GitHub release; `publish.yml` deploys with
  `-Drevision=$GITHUB_REF_NAME -Dchangelist=` so the tag name becomes the version.

The NetBeans platform version for all `org.netbeans.*` dependencies comes from the
`netbens.release` property in the root pom (note the misspelling — it is used verbatim in both
module poms). Upgrading the platform means changing that one property.

## Module wiring

NBM module metadata that Maven cannot express lives in `<module>/src/main/nbm/manifest.mf`, not in
the pom: the `ModuleInstall` class, the localizing bundle, the display category, and — for `linux` —
`OpenIDE-Module-Requires: org.openide.modules.os.Linux`, which is what keeps the plugin from
loading on other operating systems.

Menu placement and action IDs are declared with NetBeans annotations (`@ActionID`,
`@ActionRegistration`, `@ActionReference`, `@Messages`); the annotation processor generates the
layer entries, so there is no `layer.xml`. The `quote` actions all register under
`Menu/Tools/Notifications`.

## How the Linux notification path fits together

`LinuxNotificationDisplayer` extends the platform's `NotificationDisplayer` and wraps
`es.blackleg:jlibnotify` (a JNA binding to `libnotify`). Its lifecycle is split across four classes:

- `Installer` (`ModuleInstall.restored()`) sets `nb.notification.balloon.disable=true` so the IDE's
  own balloons stop appearing once the module is enabled.
- `LinuxNotificationInitialize` (`@OnStart`) and `LinuxNotificationShutdown` (`@OnStop`) look the
  displayer up and call `start()` / `stop()`, which `libnotify_init` / `libnotify_uninit`.
- `LinuxNotificationDisplayer` is registered with `@ServiceProvider(service = LinuxNotificationDisplayer.class)`
  — under its **own** type, not `NotificationDisplayer.class`. That is deliberate: `notify()` calls
  `getDefaultNotificationDisplayer()`, which scans `Lookup` for `NotificationDisplayer` instances and
  filters itself out, so registering under the parent type would make it delegate to itself.
- `EmptyNotification` is the no-op `Notification` returned when no other displayer exists in Lookup.

Everything is defensive about `libnotify` being absent: a failed load leaves `libnotify` null,
`isLoaded()` guards every call, and notifications still fall through to the platform displayer.
Keep that property when editing — the tests run headless in CI where libnotify may not be present.

## Tests

Tests extend `NbTestCase` (JUnit 3 style, run by Surefire). That means: a `public Ctor(String name)`
constructor is required, test methods must be named `testX()`, there are no `@Test`/`@Before`
annotations, and setup/teardown are `setUp()` / `tearDown()` overrides. Follow the existing classes
in `linux/src/test/java/` rather than writing JUnit 5.
