# CLAUDE.md

Guidance for Claude Code in this repository.

## What KaPay is

KaPay (named KaChat Wallet until 2026-10-27) is a standalone Android Kaspa wallet, split out of
KaChat so KaChat can stay focused on 1:1 and group chats - the Android counterpart of the iPhone's
KaPay (`KaspaSilver/KaChat-Wallet-iPhone`), which it matches screen for screen. It keeps KaChat's
wallet features: Portfolio, Cold Storage, the .kachat names marketplace, Contacts and the
account's Wallet tab (receive, send, request payment, addresses, domains, help, about).

The source started as a copy of KaChat-Android (`KaspaSilver/KaChat-Android`) at commit `9289fad8`
(5.2.2, 2026-10-22); everything chat has since been deleted (see "No chat code" below). The Kotlin
namespace is still `com.kachat.app`, so wallet fixes port across from KaChat-Android file for
file. Only what people read says KaPay; identifiers, folder and class names keep KaChat, and texts
about the KaChat ecosystem (KaChat profiles, .kachat names, the Nextcloud `KaChat` folder) still say
KaChat.

When the iPhone wallet changes, the same change lands here: its commits name the iOS files, and
the Android ones usually have the same names (Swift `FooView` -> Kotlin `FooScreen`).

Saved addresses are **Contacts** in everything people read (named the Address Book until
2026-10-28, iOS 8118f09): the Wallet tab's Contacts row, the Contacts pill under a recipient
field, Settings > Storage's Contact Photos; the icon is `Icons.Outlined.Badge` (iOS
`person.text.rectangle`). The code keeps its names (`AddressBookManager`, `AddressBookScreen`).

## Dock

Five fixed tabs, `WALLET_DOCK` in `app/src/main/java/com/kachat/app/ui/KaChatApp.kt` (rendered by
`MainShell`), opening on Wallet (`HOME_ROUTE`):

| Tab | Screen |
|---|---|
| Portfolio | `PortfolioScreen` - one floating + sheet for every add, import and export |
| Storage | `ColdStorageListScreen` |
| Wallet | `ProfileScreen` (`ui/screens/Screens.kt`, route `profile`) - the home tab: receive, Chatting Address, Manage Addresses, Your Domains, Contacts (`AddressBookScreen`, pushed), Help, About; a floating up/down button opens Send and Receive (`WalletActions.kt`: Send Kaspa, Receive Kaspa, Request Payment) |
| .kachat | `KaChatHubSections.kt` `KachatMarketScreen` + `KachatNamesLiveScreens.kt` - the full .kachat names marketplace, swiping between its tabs; dock icon is the name tag with an @ (`dockIconPainter` in `ui/ChessTabIcon.kt`) |
| Settings | `SettingsScreen(isDockTab = true)` - no Done button; Log Out is its last row |

`resolveDock` ignores any saved arrangement; there is no Kaspa Hub, Customize Dock or Simple Mode.

Account setup: `KaChatApp` -> `OnboardingScreen` (create / import a seed) -> `MainShell`. The
sign-in screen says "KaPay" with the wallet logo and no tagline. Settings: Customization,
Security, Connection (Push / Node / Domains - the .kachat indexer is editable there - / Explorer),
Notifications (one page), Storage (Nextcloud backup), Diagnostics, View Seed Phrase, Log Out.
In debug builds `adb shell setprop debug.kachat.fail_send 1` makes sends fail, for testing.

## Send Kaspa (and every other send)

Every screen that moves Kaspa is the same steps (iOS wallet 05db583), built from the shared pieces
in `ui/screens/SendKaspaComponents.kt` and `SwipeUpSendScreen.kt`. Change those pieces, not one
screen.

1. **To**: `SendStepScreen` + `SendStepQuestion` ("Who are you sending to?") + `SendRecipientCard`
   (address or name; Paste, Scan QR and Contacts pills under the field). Cancel sits at the bottom
   beside Continue (`SendContinueButton`) on a first step; later steps have Back and Cancel in the
   header (`SendStepHeader`).
2. **From**: the Chatting Address, or Choose an Address with the list right on the screen
   (`OwnAddressList`, no sheet). Skipped when the From is fixed (an address's own Send).
3. **Amount**: `KaspaKeypadAmountEntry`, the app's own number pad (`KaspaKeypad`,
   `KeypadAmountText`), so no system keyboard covers the screen; "To" and "From" chips
   (`SendStepChip`), the KAS / currency switch and Max.
4. **Review**: `SwipeUpToSendScreen` with Back: the amount, To, From, Total. Only the fee and coin
   control can change (`SendFeeControls`); the fee's Edit (`onEditFee`) opens `SendFeeEditorScreen`
   (the fee on the keypad, never under the network's minimum, warned above ten times it).

Where: `SpendingAddressSendFlow` (`Screens.kt`: the Wallet tab's Send, an address's, a contact's,
paying a payment request, Compound UTXOs); Cold Storage's `ColdSendFlow` (then Sign with KasSigner
and the signed screen with swipe up to send); Request Payment's `CreatePaymentRequestSheet`
(`PaymentRequestViews.kt`: where to be paid, how much, the review with Copy and Share); .kachat's
`KachatTxSheet` / `KachatClaimSheet` (`KachatNamesLiveScreens.kt`: an entry step when the action
takes one, then the review with the fee editor and swipe up).

## No chat code

There is no chat code left: don't bring any back. Like the iPhone wallet's 301ebfb, KaPay deleted
1:1, group and public chats, KaPosts, Chess, Swap, bots, voice, the message store, chat push and
chat sync (`ChatRepository`, `GroupRepository`, `BroadcastRepository`, `ChatViewModel` and every
screen, service and util they used, with their tests). `WalletService` keeps the node connection,
sending and the REST reads; `SettingsViewModel` has the Kaspa Explorer choice and the diagnostics
export; `WalletViewModel` the onboarding hold and the own-address activity events. Nextcloud backs
up only Contacts and the Portfolio (`KaPay Contacts.json`, `KaPay Portfolio.json`; a restore falls
back to `KaPay Address Book.json`, then the `KaChat Wallet …` names). Push keeps its registration shape: the chat fields go
out empty (`PushRegistrationManager.register`).

Room (`services/database/KaChatDatabase.kt`, version 43) holds only the Portfolio:
`portfolio_transactions` and `portfolios`. `MIGRATION_42_43` drops KaChat's chat tables and keeps
those two; anything older is rebuilt empty. Contacts live in SharedPreferences
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

See `PUSH_SERVICE.md`. KaChat's indexer is `https://indexer.kachat.app` - the default for push and the
.kachat indexer alike (`AppSettingsRepository.DEFAULT_*`); a saved `kachat.duckdns.org`, its old
host, reads as it, and the device unregisters from the old host before registering with the new.
Testnet has no default indexer (blank: no push, .kachat names from the chain), and only
`https://kachat.app` web links are opened (not the old `kachat.duckdns.org` ones).
The wallet registers with KaChat's push service only for "Received KAS"
on its own addresses (`watch_only_addresses`) and .kachat events (`primary_address`, which keeps it
registered with Address Activity off), with
`apns_topic` `kapay.kachat.app` (the app's package; the iPhone wallet sends `com.kachat.wallet`).
FCM needs `app/google-services.json` (gitignored) from KaChat's Firebase project (`kachat-fc22a`),
where `kapay.kachat.app` and `kapay.kachat.app.debug` are registered.

Claude never changes the server's code. Anything the server needs for the Android apps is handed
off in the KaChat indexer repo (`/Users/restosaved/KaChat Server/kachat-indexer`,
`KaspaSilver/KaChat-Indexer`) as one doc per change in its `docs/`, in that repo's format (a dated
title, "**For:** the indexer session. **From:** ...", the rule, what happens today, a suggested fix,
how to test), e.g. `docs/ANDROID_APP_TOPICS.md`. Commit only that doc there.

## Logo

`tools/make_logo.py` draws the logo and launcher icons (teal wallet, KaChat's backward K - the shape of KaChat's icon) - the
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

- `applicationId` `kapay.kachat.app` (`kapay.kachat.app.debug` for debug builds), the package
  registered in Play Console; the iPhone wallet's bundle is `com.kachat.wallet`. Nothing is
  shared with KaChat (`com.kachat.app`), so the two apps never read each other's storage or keys.
- URL schemes `kapay` (what KaPost and KaChat open to hand over a payment request:
  `kapay://pay/<address>?...`, the same tail as `kachat://pay/`) and `kachatwallet` (its old name,
  still accepted). No `kachat://` or `https://kachat.app` links and no share target: those belong
  to KaChat.
- Version: `app/build.gradle.kts` (`versionName`, `versionCode`, `kachatBuildNumber`), from 1.0 (1).
- Signing: `keystore.properties` and the keystores are gitignored and the owner's. KaPay's
  `keystore.properties` holds only the Play upload key (`kapay-upload.jks`, alias `kapay-upload`),
  which signs `bundlePlayRelease`; the GitHub APK keeps the debug key until a `storeFile` key is
  added there.

## Build

```bash
JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home" ./gradlew assemblePlayDebug
```

Other KaChat Android projects on this machine share the Kotlin compile daemon; when it is busy,
add `-Pkotlin.compiler.execution.strategy=in-process`. Claude may compile to catch build errors,
but never runs the app on an emulator or device: the owner does all testing. Release and Play
builds are the owner's too.
