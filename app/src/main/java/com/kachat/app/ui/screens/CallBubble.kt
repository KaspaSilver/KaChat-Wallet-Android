package com.kachat.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kachat.app.ui.theme.LocalAppColors
import com.kachat.app.util.CallCodec
import com.kachat.app.util.CallEnvelope

// KaChat Wallet has no calls (iOS wallet a11a6d7): the call screen, CallService and WebRTC are
// gone. A call event already in a chat's history still reads as this one line.

/**
 * One line per call event in the chat, like a phone's recents - what it was, and for a
 * finished call how long it lasted. The invite/response/end trio all land as separate
 * messages, so each says its own piece. Mirrors iOS's MessageBubbleView.callBubble.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CallBubble(envelope: CallEnvelope, isSent: Boolean, onLongPress: () -> Unit = {}) {
    val (text, ended) = CallCodec.bubbleText(envelope, isOutgoing = isSent)
    val icon = when {
        (envelope is CallEnvelope.Invite && envelope.video) || (envelope is CallEnvelope.Request && envelope.video) -> Icons.Default.Videocam
        ended -> Icons.Default.PhoneDisabled
        else -> Icons.Default.Phone
    }
    Surface(
        color = if (isSent) LocalAppColors.current.outgoingBubble else LocalAppColors.current.incomingBubble,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .combinedClickable(onClick = {}, onLongClick = onLongPress)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val color = if (isSent) LocalAppColors.current.onOutgoingBubble else LocalAppColors.current.textPrimary
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Text(text, color = color, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}
