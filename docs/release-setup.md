# Release Setup

## Variant Mapping

- `app/src/main`
  - Shared code and resources for every app variant.
  - Changes here affect both `debug` and `release`.
- `debug`
  - Default Android development variant.
  - Includes debug-only tooling dependencies.
  - Intended for local development and smoke verification.
- `release`
  - Formal packaging variant.
  - Uses release signing when configured.
  - Enables code shrinking and resource shrinking.
  - Is the artifact published by GitHub Actions.

## Local Signing Files

The following files must stay out of git:

- `keystore.properties`
- `release-keystore.jks`
- any exported Base64 copy of the keystore

Use `keystore.properties.example` as the template for your local untracked `keystore.properties`.

## Required `keystore.properties` Keys

```properties
storeFile=release-keystore.jks
storePassword=...
keyAlias=clockinpro
keyPassword=...
```

`storeFile` may be either a file in the project root or an absolute path.

## Required GitHub Secrets

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_PASSWORD`

## How To Populate `ANDROID_KEYSTORE_BASE64`

On Windows PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("D:\Fish\ClockInPro\release-keystore.jks"))
```

Paste the resulting single-line value into the `ANDROID_KEYSTORE_BASE64` GitHub secret.

## Backup Requirements

Back up all of the following in at least two safe locations:

- `release-keystore.jks`
- `storePassword`
- `keyAlias`
- `keyPassword`

Once this keystore is used to publish a real release, future updates should continue using the same signing key.
