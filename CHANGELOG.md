# Changelog

## [1.1.0](https://github.com/ahincho/nova-java-mask-utils/compare/nova-mask-utils-v1.0.0...nova-mask-utils-v1.1.0) (2026-07-13)


### Features

* refactor GenericPhoneMaskStrategy to use stream-based digit counting ([cbca210](https://github.com/ahincho/nova-java-mask-utils/commit/cbca21021cdedeae95db70c645d610a80ce2e539))


### Bug Fixes

* **ci:** add explicit component name in release-please-config.json ([1a2e560](https://github.com/ahincho/nova-java-mask-utils/commit/1a2e5601b6692671a06af85330b421a2ce1a43f1))
* **ci:** add skip-snapshot to release-please-config.json ([b3cf331](https://github.com/ahincho/nova-java-mask-utils/commit/b3cf3311739032ed0478cb10507779277d8d81ad))
* **ci:** pin last-release-sha for clean CHANGELOG after path migration ([6e7e7fb](https://github.com/ahincho/nova-java-mask-utils/commit/6e7e7fb74ccffec5e2d085a44f46dffa1477fc3a))

## 1.0.0 (2026-07-10)


### Features

* **ci:** migrate to release-please + tag-based publish flow (NOVA-SEMVER-13) ([5cb31c1](https://github.com/ahincho/nova-java-mask-utils/commit/5cb31c18c4924e6005122568c5c7ef154bebf6c3))
* **gradle:** add GPG signing plugin for Maven Central publishing (NOVA-SEMVER-10) ([15742ad](https://github.com/ahincho/nova-java-mask-utils/commit/15742ad52d82f0fa45f0345ec52a19b80f6e297a))
* **gradle:** enable Local Build Cache and Configuration Cache (NOVA-SEMVER-23-24) ([02d31e3](https://github.com/ahincho/nova-java-mask-utils/commit/02d31e3e61832fcde9dd82c8b54dc4df98940629))
* initial commit with full mask-utils implementation ([11a9dde](https://github.com/ahincho/nova-java-mask-utils/commit/11a9ddee1ac771d06448fde9be157291a22b619b))


### Bug Fixes

* **ci:** inline publish-on-tag and remove dirty closure for Gradle 9.6.1 ([fc0e66c](https://github.com/ahincho/nova-java-mask-utils/commit/fc0e66cbdebe164b7ba855e6d3746d5bc304f323))
* **ci:** update reusable workflow refs from OWNER/galaxy-training-devops to ahincho/nova-devops ([d96245a](https://github.com/ahincho/nova-java-mask-utils/commit/d96245af7c231821ef9724d907bdf7593e9a0065))
* **ci:** use PAT fallback for release-please to enable tag-triggered workflows ([1399577](https://github.com/ahincho/nova-java-mask-utils/commit/13995779402d552438159eb67d884939fd0cf249))
