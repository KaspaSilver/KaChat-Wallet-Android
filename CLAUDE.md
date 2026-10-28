# CLAUDE.md

Guidance for Claude Code in this repository.

## What KaPay is

KaPay (named KaChat Wallet until 2026-10-27) is a standalone Android Kaspa wallet, split out of
KaChat so KaChat can stay focused on 1:1 and group chats - the Android counterpart of the iPhone's
KaPay (`KaspaSilver/KaChat-Wallet-iPhone`), which it matches screen for screen. It keeps KaChat's
wallet features: Portfolio, Cold Storage, the .kachat names marketplace, the Address Book and the
account's Wallet tab (receive, send, request payment, addresses, domains, help, about).

The source started as a copy of KaChat-Android (`KaspaSilver/KaChat-Android`) at commit `9289fad8`
(5.2.2, 2026-10-22); everything chat has since been deleted (see "No chat code" below). The Kotlin
namespace is still `com.kachat.app`, so wallet fixes port across from KaChat-Android file for
file. Only what people read says KaPay; identifiers, folder and class names keep KaChat, and texts
about the KaChat ecosystem (KaChat profiles, .kachat names, the Nextcloud `KaChat` folder) still say
KaChat.

When the iPhone wallet changes, the same change lands here: its commits name the iOS files, and
the Android ones usually have the same names (Swift `FooView` -> Kotlin `FooScreen`).

## Dock

Five fixed tabs, `WALLET_DOCK` in `app/src/main/java/com/kachat/app/ui/KaChatApp.kt` (rendered by
`MainShell`), opening on Wallet (`HOME_ROUTE`):

| Tab | Screen |
|---|---|
| Portfolio | `PortfolioScreen` - one floating + sheet for every add, import and export |
| Storage | `ColdStorageListScreen` |
| Wallet | `ProfileScreen` (`ui/screens/Screens.kt`, route `profile`) - the home tab: receive, Chatting Address, Manage Addresses, Your Domains, Address Book (`AddressBookScreen`, pushed), Help, About; a floating up/down button opens Send and Receive (`WalletActions.kt`: Send Kaspa, Receive Kaspa, Request Payment) |
| .kachat | `KaChatHubSections.kt` `KachatMarketScreen` + `KachatNamesLiveScreens.kt` - the full .kachat names marketplace, swiping between its tabs; dock icon is the name tag with an @ (`dockIconPainter` in `ui/ChessTabIcon.kt`) |
| Settings | `SettingsScreen(isDockTab = true)` - no Done button; Log Out is its last row |

`resolveDock` ignores any saved arrangement; there is no Kaspa Hub, Customize Dock or Simple Mode.

Account setup: `KaChatApp` -> `OnboardingScreen` (create / import a seed) -> `MainShell`. The
sign-in screen says "KaPay" with the wallet logo and no tagline. Settings: Customization,
Security, Connection (Push / Node / Domains - the .kachat indexer is editable there - / Explorer),
Notifications (one page), Storage (Nextcloud backup), Diagnostics, View Seed Phrase, Log Out.
Every send (Send Kaspa, Compound UTXOs, .kachat Claim and Buy) is the swipe-up screen
(`SwipeUpSendScreen.kt`); in debug builds `adb shell setprop debug.kachat.fail_send 1` makes
sends fail, for testing.

## No chat code

There is no chat code left: don't bring any back. Like the iPhone wallet's 301ebfb, KaPay deleted
1:1, group and public chats, KaPosts, Chess, Swap, bots, voice, the message store, chat push and
chat sync (`ChatRepository`, `GroupRepository`, `BroadcastRepository`, `ChatViewModel` and every
screen, service and util they used, with their tests). `WalletService` keeps the node connection,
sending and the REST reads; `SettingsViewModel` has the Kaspa Explorer choice and the diagnostics
export; `WalletViewModel` the onboarding hold and the own-address activity events. Nextcloud backs
up only the Address Book and the Portfolio. Push keeps its registration shape: the chat fields go
out empty (`PushRegistrationManager.register`).

Room (`services/database/KaChatDatabase.kt`, version 43) holds only the Portfolio:
`portfolio_transactions` and `portfolios`. `MIGRATION_42_43` drops KaChat's chat tables and keeps
those two; anything older is rebuilt empty. The Address Book lives in SharedPreferences
(`AddressBookManager`), settings in DataStore (`AppSettingsRepository`, which still has KaChat's
unused chat keys).

What runs is the wallet: the node pool; `OwnAddressSubscription`, the live UTXO subscription to
the wallet's own addresses (main, spending, cold storage) while the app is open - a node with a
UTXO index streams `NotifyUtxosChanged` batches into `AddressActivityNotifier.handleLiveUtxoChanges`
("Received KAS", self-sends suppressed) and a balance refresh, re-subscribing after a dropped
stream or a changed address set (iOS wallet `WalletService.setupUtxoSubscription`);
`AddressActivityNotifier`'s catch-up (on foreground, when the subscription comes up, and a 60 s
fallback poll only while it is down); Portfolio, Cold Storage and .kachat. `SyncWorker` (every
15 min) refreshes the balance and runs one receive pass. Notification channels carry no launcher
badge.

## Push

See `PUSH_SERVICE.md`. The wallet registers with KaChat's push service only for "Received KAS"
on its own addresses (`watch_only_addresses`) and .kachat events (`primary_address`, which keeps it
registered with Address Activity off), with
`apns_topic` `com.kachat.wallet`. FCM needs `app/google-services.json` from KaChat's Firebase
project with `com.kachat.wallet` added - the owner's step.

## Logo

`tools/make_logo.py` draws the logo and launcher icons (teal wallet, Kaspa's backward K) - the
iPhone wallet's script with Android's adaptive-icon layers added:

```bash
python3 tools/make_logo.py app/src/main/res/drawable-nodpi/kachat_wallet_logo.png 512 logo
python3 tools/make_logo.py app/src/main/res/drawable-nodpi/kachat_wallet_logo_dark.png 512 dark   # the privacy cover in dark mode
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
- URL schemes `kapay` (what KaPost and KaChat open to hand over a payment request:
  `kapay://pay/<address>?...`, the same tail as `kachat://pay/`) and `kachatwallet` (its old name,
  still accepted). No `kachat://` or `https://kachat.app` links and no share target: those belong
  to KaChat.
- Version: `app/build.gradle.kts` (`versionName`, `versionCode`, `kachatBuildNumber`), from 1.0 (1).
- Signing: `keystore.properties` and the keystores are gitignored and the owner's; the wallet
  needs its own upload key for Play.

## Build

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assemblePlayDebug
```

Other KaChat Android projects on this machine share the Kotlin compile daemon; when it is busy,
add `-Pkotlin.compiler.execution.strategy=in-process`. Claude may compile to catch build errors,
but never runs the app on an emulator or device: the owner does all testing. Release and Play
builds are the owner's too.
