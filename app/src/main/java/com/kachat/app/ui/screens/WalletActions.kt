package com.kachat.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kachat.app.R
import com.kachat.app.ui.theme.KaspaTeal
import com.kachat.app.ui.theme.LocalAppColors

/** What the Wallet tab's floating button opens (iOS wallet 5bab351 `WalletAction`). */
enum class WalletAction { SEND, RECEIVE, REQUEST }

/**
 * The Wallet tab's floating glass up/down arrow, bottom right as in KaChat (iOS wallet 5bab351
 * `sendKaspaButton`): it opens [WalletActionsSheet].
 */
@Composable
fun WalletActionsButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val haptic = com.kachat.app.util.rememberHaptics()
    com.kachat.app.ui.theme.IosGlassFab(
        modifier = modifier,
        onClick = {
            haptic(com.kachat.app.util.IosHaptic.IMPACT_LIGHT)
            onClick()
        },
        icon = Icons.Outlined.SwapVert,
        contentDescription = stringResource(R.string.wallet_send_and_receive),
    )
}

/**
 * The floating button's half sheet, "Send and Receive" (iOS wallet 5bab351
 * `walletActionsSheet`): Send Kaspa, Receive Kaspa and Request Payment as three tiles. [onPick]
 * gets the tile once the sheet is down - a sheet opened while this one is still closing would be
 * dismissed with it.
 */
@Composable
fun WalletActionsSheet(onDismiss: () -> Unit, onPick: (WalletAction) -> Unit) {
    ActionSheetContainer(
        title = stringResource(R.string.wallet_send_and_receive),
        subtitle = null,
        onDismiss = onDismiss,
    ) {
        // 104 dp tiles as on iOS, smaller only where three of them do not fit across.
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            val tileSize = minOf(WalletActionTileSize, (maxWidth - WalletActionTileSpacing * 2) / 3)
            Row(horizontalArrangement = Arrangement.spacedBy(WalletActionTileSpacing)) {
                WalletActionTile(
                    size = tileSize,
                    icon = Icons.Outlined.ArrowCircleUp,
                    title = stringResource(R.string.chats_send_kaspa),
                    hint = stringResource(R.string.chats_send_kaspa_hint),
                ) {
                    onDismiss()
                    onPick(WalletAction.SEND)
                }
                WalletActionTile(
                    size = tileSize,
                    icon = Icons.Outlined.ArrowCircleDown,
                    title = stringResource(R.string.receive_kaspa),
                    hint = stringResource(R.string.chats_receive_subtitle),
                ) {
                    onDismiss()
                    onPick(WalletAction.RECEIVE)
                }
                WalletActionTile(
                    size = tileSize,
                    icon = RequestPaymentIcon,
                    title = stringResource(R.string.pr_request_payment),
                    hint = stringResource(R.string.pr_request_payment_hint),
                ) {
                    onDismiss()
                    onPick(WalletAction.REQUEST)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

/** One square option: an icon over a two-line title, on glass; the explanation is the TalkBack
 *  hint (iOS `WalletActionTile`, 104 pt). */
@Composable
fun WalletActionTile(size: Dp, icon: ImageVector, title: String, hint: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .size(size)
            .sendKaspaGlass(18.dp)
            .clickable(onClick = onClick)
            .semantics { contentDescription = "$title. $hint" }
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, androidx.compose.ui.Alignment.CenterVertically),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(28.dp))
        Text(
            title,
            color = LocalAppColors.current.textPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val WalletActionTileSize = 104.dp
private val WalletActionTileSpacing = 14.dp
