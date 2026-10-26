package com.kachat.app.util

/**
 * KaChat Wallet's chats switch (iOS wallet 7f789f4 `ChatService.chatsEnabled`). The wallet started
 * as a copy of KaChat, so its chat code is all still here and still compiles; this is off, and every
 * chat path checks it and does nothing - message, handshake and inbox sync, the fallback poll,
 * payment catch-ups, groups, public chats, KaPosts polling, the .kachat sweep that names chat
 * contacts, "chat with yourself", direct-share shortcuts, chat pushes and chat banners.
 *
 * What still runs is the wallet: the node pool, the wallet's own addresses ("Received KAS" through
 * AddressActivityNotifier and a balance refresh), Portfolio, Cold Storage and .kachat names.
 */
object ChatFeatures {
    // A val rather than a const, so the guards read as ordinary checks rather than dead code.
    val enabled: Boolean = false
}
