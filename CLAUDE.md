# CLAUDE.md

Guidance for Claude Code in this repository.

## What KaChat Wallet is

KaChat Wallet is a standalone Android Kaspa wallet, split out of KaChat so KaChat can stay focused
on 1:1 and group chats - the Android counterpart of the iPhone's KaChat Wallet
(`KaspaSilver/KaChat-Wallet-iPhone`), which it matches screen for screen. It keeps KaChat's wallet
features: Portfolio, Cold Storage, the .kachat names marketplace, Addresses (the Address Book) and
the whole account Profile (receive, addresses, domains, settings, help, log out, about).

The source started as a copy of KaChat-Android (`KaspaSilver/KaChat-Android`) at commit `9289fad8`
(5.2.2, 2026-10-22). Most of `app/` is still that code, including features the wallet never shows
(1:1 and group chats, KaPosts, Public Chats, Chess, Swap, Nextcloud, bots). Calls are gone entirely
(CallService, WebRTC, the call screen). The Kotlin namespace is still `com.kachat.app`, so fixes
port across from KaChat-Android file for file; change only what the wallet uses.

When the iPhone wallet changes, the same change lands here: its commits name the iOS files, and
the Android ones usually have the same names (Swift `FooView` -> Kotlin `FooScreen`).

## Dock

Five fixed tabs, `WALLET_DOCK` in `app/src/main/java/com/kachat/app/ui/KaChatApp.kt` (rendered by
`MainShell`), opening on Portfolio (`HOME_ROUTE`):

| Tab | Screen |
|---|---|
| Portfolio | `PortfolioScreen` - the home tab |
| Storage | `ColdStorageListScreen` |
| Wallet | `ProfileScreen` (`ui/screens/Screens.kt`) - KaChat's Profile, titled "Wallet" with a wallet icon (`Screen.Profile`, route `profile`); Settings opens from here |
| .kachat | `KachatNamesLiveScreens.kt` - the full .kachat names marketplace; dock icon is the name tag with an @ (`dockIconPainter` in `ui/ChessTabIcon.kt`); hidden in Simple Mode |
| Addresses | `AddressBookScreen` (`ui/screens/AddressBookScreens.kt`) - one floating + for Add, Import and Export |

`resolveDock` ignores any saved arrangement and there is no Kaspa Hub or Customize Dock.

Account setup is KaChat's: `KaChatApp` -> `OnboardingScreen` (create / import a seed) ->
`MainShell`. The sign-in screen says "KaChat Wallet" with the wallet logo and no tagline; KaChat's
welcome guide is never shown. Settings has no Chats or Chat History page, no Customize Dock and no
Show Fee Estimate; Connection Settings has Push / Node / Domains / Explorer tabs; Help has only the
.kachat names guide.

## Chats are off

`ChatFeatures.enabled` (`util/ChatFeatures.kt`) is `false`, and every chat path checks it and does
nothing: the message poll loop and `syncMessages`, inbox and pushed-message ingest, "chat with
yourself", group sync, catch-up and live polling, the group and public-room block scanners
(their scopes are cancelled at construction), featured room joins, KaPosts polling, the .kachat
sweep that names chat contacts, payment notices, direct-share shortcuts, chat banners in
`NotificationHelper`, the battery-exemption prompt, and every FCM type but `name_event` and
`address_activity`. The chat code itself still compiles; change nothing in it unless removing it.

What runs is the wallet: the node pool, `AddressActivityNotifier` (a 30 s foreground poll of the
wallet's own addresses - Android has no UTXO subscription - where the main address now counts as a
wallet address, so every receive notifies, and a receive refreshes the balance), Portfolio, Cold
Storage and .kachat. `SyncWorker` (every 15 min) refreshes the balance and runs one receive pass
instead of syncing groups. Notification channels carry no launcher badge.

## Push

See `PUSH_SERVICE.md`. The wallet registers with KaChat's push service only for "Received KAS"
on its own addresses (`watch_only_addresses`) and .kachat events (`primary_address`), with
`apns_topic` `com.kachat.wallet`. FCM needs `app/google-services.json` from KaChat's Firebase
project with `com.kachat.wallet` added - the owner's step.

## Logo

`tools/make_logo.py` draws the logo and launcher icons (teal wallet, Kaspa's backward K) - the
iPhone wallet's script with Android's adaptive-icon layers added:

```bash
python3 tools/make_logo.py app/src/main/res/drawable-nodpi/kachat_wallet_logo.png 512 logo
for d in mdpi:48 hdpi:72 xhdpi:96 xxhdpi:144 xxxhdpi:192; do n=${d#*:}; d=${d%:*}; f=$((n*9/4))
  python3 tools/make_logo.py app/src/main/res/mipmap-$d/ic_launcher_foreground.png $f foreground
  python3 tools/make_logo.py app/src/main/res/mipmap-$d/ic_launcher_monochrome.png $f monochrome
  python3 tools/make_logo.py app/src/main/res/mipmap-$d/ic_launcher.png $n logo
  python3 tools/make_logo.py app/src/main/res/mipmap-$d/ic_launcher_round.png $n logo
done
```

The status-bar icon is the vector `drawable/ic_kachat_wallet_notification.xml`.

## Identifiers

- `applicationId` `com.kachat.wallet` (`com.kachat.wallet.debug` for debug builds). Nothing is
  shared with KaChat (`com.kachat.app`), so the two apps never read each other's storage or keys.
- No `kachat://` or `https://kachat.app` links and no share target: those belong to KaChat.
- Version: `app/build.gradle.kts` (`versionName`, `versionCode`, `kachatBuildNumber`), from 1.0 (1).
- Signing: `keystore.properties` and the keystores are gitignored and the owner's; the wallet
  needs its own upload key for Play.

## Build

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assemblePlayDebug
```

Other KaChat Android projects on this machine share the Kotlin compile daemon; when it is busy,
add `-Pkotlin.compiler.execution.strategy=in-process`. Claude may build and run on the emulator
(`Pixel_8`). Release and Play builds are the owner's.
