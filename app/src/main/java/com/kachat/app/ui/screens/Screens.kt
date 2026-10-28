package com.kachat.app.ui.screens

import androidx.compose.material.icons.filled.IosShare
import com.kachat.app.util.UserFacingError
import com.kachat.app.util.redactedForLog
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.view.Gravity
import android.view.WindowManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import coil.compose.SubcomposeAsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import com.kachat.app.ui.theme.IosRoundedFontFamily
import com.kachat.app.ui.theme.iosGlass
import com.kachat.app.ui.theme.iosShadow
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SaveAlt
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.PhoneEnabled
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Switch
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.PhoneDisabled
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.CallMerge
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.composed
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.FlowRow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.kachat.app.R
import com.kachat.app.services.ColdStorageAddressDiscovery
import com.kachat.app.services.KnsInscriptionEngine
import com.kachat.app.services.KnsService
import com.kachat.app.services.UtxoEntry
import com.kachat.app.ui.theme.KaspaBlue
import com.kachat.app.ui.theme.KaspaSubtext
import com.kachat.app.ui.theme.KaspaTeal
import com.kachat.app.ui.theme.LocalAppColors
import com.kachat.app.util.KaspaAddress
import com.kachat.app.util.KaspaMass
import com.kachat.app.util.authenticateWithDeviceCredential
import com.kachat.app.util.ImagePrep
import com.kachat.app.util.KaspaUnit
import com.kachat.app.util.copyPrivateKeyWithAutoWipe
import com.kachat.app.viewmodels.ConnectionStatus as ConnStatus
import com.kachat.app.viewmodels.ConnectionViewModel
import com.kachat.app.viewmodels.NodeInfo
import com.kachat.app.viewmodels.SettingsViewModel
import com.kachat.app.viewmodels.WalletViewModel
import kotlinx.coroutines.Dispatchers
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import com.kachat.app.util.showAddressCopiedToast
import androidx.compose.material.icons.outlined.InsertDriveFile

/** What the Donate row reads: KaChat's .kachat name (iOS e7cc0d5). */
private const val DONATE_NAME = "kachat.kachat"

/**
 * The address Donate pays, pinned (iOS 8bc86f8, IOS-067): a name is first-come, so resolving
 * `kachat.kachat` would pay whoever registered it. This is the address `kachat.kas` has always
 * paid; on testnet the same key's `kaspatest:` form. The Send screen shows the address's own
 * .kachat name once the project registers `kachat` to it.
 */
private const val DONATION_ADDRESS_MAINNET = "kaspa:qzy7da4589avjwmmnqfvkhp5p8p268gc7rvr9lg2xxpuhj75sy8kgdqmpd2fu"
private val donationAddress: String get() = com.kachat.app.util.KaspaNetwork.reencode(DONATION_ADDRESS_MAINNET)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: WalletViewModel,
    navController: NavController,
    connectionViewModel: ConnectionViewModel = hiltViewModel(),
    portfolioViewModel: com.kachat.app.viewmodels.PortfolioViewModel = hiltViewModel()
) {
    val address by viewModel.address.collectAsState()
    val accountName by viewModel.accountName.collectAsState()
    val balance by viewModel.fullBalance.collectAsState()
    val identityBalanceSompi by viewModel.balanceSompi.collectAsState()
    val showSetupGuides by viewModel.showSetupGuides.collectAsState()
    val dotColorHex by connectionViewModel.dotColorHex.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    /** Everything this account holds - Profile's Manage Addresses card (iOS 6333dd3). */
    val accountTotalSompi by viewModel.accountTotalSompi.collectAsState()
    var showFundIdentityQr by remember { mutableStateOf(false) }
    // Its own state (not shared with any other QR overlay) so its bigger green border can't
    // accidentally affect an unrelated overlay reusing the same flag.
    var showAcceptPaymentQr by remember { mutableStateOf(false) }
    // The address the Receive QR should draw: one that has never been used. Warmed when this
    // screen appears and re-confirmed when the overlay opens - see both effects below.
    var receiveQrAddress by remember { mutableStateOf<String?>(null) }
    /** A lookup for [receiveQrAddress] is still out - the page says it is preparing one. */
    var receiveQrResolving by remember { mutableStateOf(true) }
    // Re-confirmed on open, but NOT cleared first: the warm-up above has almost always settled
    // it already, so the overlay draws immediately and this only swaps the value if the warmed
    // answer went stale in between. A QR must not change under a pointed camera for any lesser
    // reason, and blanking it first would guarantee a spinner every time.
    LaunchedEffect(showAcceptPaymentQr) {
        if (showAcceptPaymentQr) {
            if (receiveQrAddress == null) receiveQrResolving = true
            // Shown now: it goes on the Manage Addresses list (and is watched for payments).
            viewModel.resolveFreshReceiveAddress(revealing = true) { address ->
                receiveQrAddress = address
                receiveQrResolving = false
            }
        }
    }
    // Donate: the Send screen, addressed to KaChat's donation address (iOS e7cc0d5, 8bc86f8).
    var showDonateSheet by remember { mutableStateOf(false) }
    // The floating Send and Receive button (iOS wallet 5bab351) and what its tiles open.
    var showWalletActions by remember { mutableStateOf(false) }
    var showSendSheet by remember { mutableStateOf(false) }
    var showRequestPayment by remember { mutableStateOf(false) }

    val knsInscribeState by viewModel.knsInscribeState.collectAsState()
    val pendingKnsCommit by viewModel.pendingKnsCommit.collectAsState()

    val profileAssetId by viewModel.profileDomainAssetId.collectAsState()
    val knsProfile by viewModel.knsProfile.collectAsState()
    // Your `.kachat` label (KACHAT_NAMES.md section 7 - your primary name while you own it and it
    // is active, else your oldest active name), shown as your name on the hero (iOS 5df42b4), and
    // your address profile. On every network since iOS d36fc42: mainnet has no registry yet, so
    // there you show by your short address, with your profile's picture, banner and bio.
    val kachatLive: KachatLiveViewModel? =
        if (com.kachat.app.services.kachatnames.KachatNamesService.profilesEnabled) hiltViewModel() else null
    var kachatLabel by remember { mutableStateOf<String?>(null) }
    // The address profile's sources (a social link each for avatar, banner and bio, looked up on
    // this device) and Linktree link (iOS ad32798 / 1322216 / c124cb3; mainnet too since d36fc42).
    var kachatAvatarSource by remember { mutableStateOf<String?>(null) }
    var kachatBannerSource by remember { mutableStateOf<String?>(null) }
    var kachatBioSource by remember { mutableStateOf<String?>(null) }
    var kachatLinktree by remember { mutableStateOf<String?>(null) }
    val kachatRevision = kachatLive?.registry?.revision?.collectAsState()?.value
    LaunchedEffect(address, kachatRevision) {
        val a = address
        val hero = if (kachatLive != null && a != null) kachatLive.hero(a) else null
        kachatLabel = hero?.label
        kachatAvatarSource = hero?.avatar
        kachatBannerSource = hero?.banner
        kachatBioSource = hero?.bio
        kachatLinktree = hero?.linktree
    }
    // What each source shows, from the device's cache; a missing or stale answer is looked up
    // again and lands here through the collected cache (iOS `socialImages.profile(for:)`).
    val kachatSocialEntries = kachatLive?.social?.entries?.collectAsState()?.value
    LaunchedEffect(kachatAvatarSource, kachatBannerSource, kachatBioSource) {
        listOf(kachatAvatarSource, kachatBannerSource, kachatBioSource).forEach { kachatLive?.social?.refreshIfStale(it) }
    }
    val kachatAvatar = kachatSocialEntries?.let { kachatLive?.social?.cached(kachatAvatarSource, it) }?.avatar
    val kachatBanner = kachatSocialEntries?.let { kachatLive?.social?.cached(kachatBannerSource, it) }?.banner
    val kachatBio = kachatSocialEntries?.let { kachatLive?.social?.cached(kachatBioSource, it) }?.bio
    val activeProfileDomainName = viewModel.activeProfileDomainName.collectAsState().value
    val hasAnyProfileData = knsProfile != null && listOf(
        knsProfile?.bio, knsProfile?.x, knsProfile?.website, knsProfile?.telegram,
        knsProfile?.discord, knsProfile?.contactEmail, knsProfile?.github, knsProfile?.redirectUrl
    ).any { !it.isNullOrBlank() }

    LaunchedEffect(Unit) {
        // The balance requests at most every 30 s: switching tabs back and forth started them
        // each time. Arriving funds still refresh at once (the activity events below), and pull
        // to refresh is always there (iOS 9de981a).
        if (viewModel.claimProfileAppearRefresh()) {
            viewModel.refreshBalance()
            viewModel.refreshSpendingAddress()
        }
        viewModel.refreshOwnedDomains()
        viewModel.loadManageAddresses()
        // Decide the Receive QR's address NOW, not when the button is tapped. The freshness
        // check is a network round trip, so doing it on tap put a spinner between pressing
        // Receive Kaspa and seeing a code. Only picked, not revealed (iOS 24c2094): nothing
        // appears in Manage Addresses until the QR is actually shown.
        viewModel.resolveFreshReceiveAddress(revealing = false) { address ->
            receiveQrAddress = address
            receiveQrResolving = false
        }
    }

    // Funds landing used to show up here only on the next open or a pull-to-refresh - the screen
    // that shows your balance was the one screen not listening for it.
    LaunchedEffect(Unit) {
        viewModel.ownAddressUtxoActivityEvents.collect {
            viewModel.refreshBalance()
            viewModel.refreshSpendingAddress()
        }
    }

    val pullRefreshState = rememberPullToRefreshState()
    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            viewModel.refreshBalanceAndAwait()
            viewModel.refreshSpendingBalanceAndAwait()
            pullRefreshState.endRefresh()
        }
    }

    // Re-tapping the Profile tab while already on it (e.g. to back out of a full-screen QR
    // overlay) doesn't re-navigate/recompose this screen — see WalletViewModel.notifyTabReselected.
    val tabReselectSignal by viewModel.tabReselectSignal.collectAsState()
    LaunchedEffect(tabReselectSignal) {
        if (tabReselectSignal.second == "profile") {
            showFundIdentityQr = false
            showAcceptPaymentQr = false
        }
    }

    // Donate goes straight to Send with KaChat's pinned donation address filled in - an address,
    // not a name anyone could register (iOS 8bc86f8, IOS-067) - no chat opened.
    if (showDonateSheet) {
        IosFullSheet(onDismissed = { showDonateSheet = false }, swipeToDismiss = false) { close ->
            SpendingAddressSendFlow(
                fromAddress = address ?: "",
                balanceSompi = identityBalanceSompi,
                title = "Send Kaspa",
                viewModel = viewModel,
                portfolioViewModel = portfolioViewModel,
                onDone = close,
                presentedAsSheet = true,
                prefillAddress = donationAddress,
            )
        }
    }

    val dockInset = LocalDockInset.current
    Scaffold(
        containerColor = LocalAppColors.current.background,
        // The page runs on under the dock; its scroll ends with room to clear it.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column(
                modifier = Modifier
                    .background(LocalAppColors.current.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                // The Profile bell: Kaspa arriving in your wallets and .kachat news (iOS 86471dd,
                // 4500ebc). Declared before the bar it now lives in.
                val notifCenterVm: com.kachat.app.viewmodels.NotificationCenterViewModel = hiltViewModel()
                val notifEntries by notifCenterVm.store.entries.collectAsState()
                val notifLastSeen by notifCenterVm.store.lastSeenAt.collectAsState()
                var showNotifCenter by remember { mutableStateOf(false) }
                val notifUnread = notifEntries.count { it.timestampMs > notifLastSeen }
                // Row one is the status row - connection dot, balance, bell - the way every
                // other main page opens and the way iOS orders its Profile toolbar. The title
                // and the settings button come underneath, not above.
                TopStatusBar(
                    balance = balance,
                    onStatusClick = { ConnectionStatusOverlayState.open() },
                    dotColorHex = dotColorHex,
                    showAddButton = false,
                    trailingContent = {
                        IconButton(
                            onClick = {
                                notifCenterVm.store.reloadIfNeeded()
                                showNotifCenter = true
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box {
                                Icon(
                                    // SF Symbols "bell" is an outline.
                                    Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = KaspaTeal,
                                    modifier = Modifier.size(22.dp),
                                )
                                if (notifUnread > 0) {
                                    // A plain red DOT (no count) - "there is something unread"
                                    // is the signal.
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color(0xFFE0245E)),
                                    )
                                }
                            }
                        }
                    },
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 6.dp),
                ) {
                    // KaChat Wallet: this screen is the "Wallet" tab (iOS wallet 1314382).
                    Text(
                        stringResource(R.string.wallet_title),
                        color = LocalAppColors.current.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = LargeTitleSize,
                    )
                    // Beside the title: a kachat.app link to you (iOS 863b0e9). Pasted anywhere it
                    // previews with your KNS name and avatar; tapped with KaChat installed it opens a
                    // chat with you, or the new-chat screen for someone who has not got you as a
                    // contact; without KaChat the page offers the download.
                    address?.let { myAddress ->
                        val shareContext = LocalContext.current
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LocalAppColors.current.textSecondary.copy(alpha = 0.15f))
                                .clickable {
                                    // The link and nothing else: it previews with your name and
                                    // avatar on its own (iOS 7ce35ef). EXTRA_TITLE only titles the
                                    // share sheet's preview; it is not part of what gets pasted.
                                    val link = KaChatLink.profileWebUrl(myAddress)
                                    val send = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, link)
                                        putExtra(Intent.EXTRA_TITLE, activeProfileDomainName?.takeIf { com.kachat.app.services.KnsService.SHOWS_DOMAIN_NAMES_AS_IDENTITY } ?: "My KaChat profile")
                                    }
                                    runCatching { shareContext.startActivity(Intent.createChooser(send, null)) }
                                }
                                .semantics { contentDescription = "Share your profile" },
                        ) {
                            Icon(
                                Icons.Default.IosShare,
                                contentDescription = null,
                                tint = KaspaTeal,
                                modifier = Modifier.size(19.dp),
                            )
                        }
                    }
                }
                if (showNotifCenter) {
                    // A full-height sheet, as iOS presents GlobalNotificationListView.
                    GlobalNotificationSheet(
                        store = notifCenterVm.store,
                        walletViewModel = viewModel,
                        navController = navController,
                        onDismiss = { showNotifCenter = false },
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                // Still scrollable as a safety net (smaller phones, larger system font scale),
                // but every element below is sized to comfortably fit a typical phone screen
                // without needing it.
                .verticalScroll(scrollState)
                // iOS: the pinned title's 8pt bottom padding, then the list's 16pt top padding
                // pulled back 8 by the account row - 16 from the title row in all, of which the
                // title row here already carries 14.
                .padding(top = 2.dp),
            // iOS's VStack(spacing: 16).
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // one of your .kachat names expired and is in grace: renew it in time (iOS 6ac48a7)
            KachatExpiredNamesBanner(walletAddress = address)
            // 4.0 Profile redesign (matches iOS): inline-editable account name up top, then a
            // KaPosts-style hero (KNS banner, overlapping avatar, display name, bio), then the
            // KNS profile editing entry - the old boxed Account section is gone.
            run {
                var isEditingName by remember { mutableStateOf(false) }
                var editedName by remember { mutableStateOf("") }
                // iOS accountNameRow: title3 semibold (20) and, 8 to its right, a title3
                // pencil.circle.fill (a filled secondary-grey circle with the pencil cut out) - or,
                // while editing, the field and an accent checkmark.circle.fill.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (isEditingName) {
                        com.kachat.app.ui.theme.IosTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = LocalAppColors.current.textPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        )
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.done),
                            tint = KaspaTeal,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val trimmed = editedName.trim()
                                    isEditingName = false
                                    if (trimmed.isNotEmpty() && trimmed != accountName) {
                                        address?.let { viewModel.renameAccount(it, trimmed) }
                                    }
                                }
                        )
                    } else {
                        Text(
                            accountName ?: stringResource(R.string.account_name),
                            color = LocalAppColors.current.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(21.dp)
                                .clip(CircleShape)
                                .background(LocalAppColors.current.textSecondary)
                                .clickable {
                                    editedName = accountName ?: ""
                                    isEditingName = true
                                }
                                .semantics { contentDescription = "Rename account" },
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = LocalAppColors.current.background,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            // iOS glassBackground(cornerRadius: 18): the material, a 0.8 white hairline at 18%
            // and the soft shadow.
            Column(modifier = Modifier.fillMaxWidth().iosGlass(18.dp)) {
                run {
                    // On testnet the .kachat profile (avatar, banner and bio looked up from its
                    // social link) comes first; the KNS one otherwise (iOS ad32798 / 1322216).
                    // On testnet identity is .kachat only: no KNS fallback there (iOS e52357d).
                    val bannerUrl = kachatBanner
                        ?: knsProfile?.bannerUrl?.takeIf { !com.kachat.app.services.kachatnames.KachatNamesService.isEnabled }
                    if (bannerUrl != null) {
                        // Whole, at its own proportions (iOS c66bfc7).
                        WholeBanner(model = bannerUrl, placeholderHeight = 140.dp) {
                            Box(Modifier.fillMaxSize().background(KaspaTeal.copy(alpha = 0.25f)))
                        }
                    } else {
                        Box(
                            Modifier.fillMaxWidth().height(140.dp).background(
                                androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(KaspaTeal.copy(alpha = 0.55f), KaspaTeal.copy(alpha = 0.15f))
                                )
                            )
                        )
                    }
                    // The .kas is part of the name, so the hero shows it too.
                    // Your domain name, else your short address - never the account name, which is
                    // your own label for the account, not what anyone sees you as (iOS 3041164).
                    // Your .kas name isn't your name here since 5.2 (iOS 509c0fe); that will be
                    // your .kachat name.
                    // On testnet your .kachat label comes first (iOS 5df42b4).
                    val heroName = kachatLabel?.let { "$it.kachat" }
                        ?: activeProfileDomainName?.takeIf { com.kachat.app.services.KnsService.SHOWS_DOMAIN_NAMES_AS_IDENTITY }
                        ?: address?.let { com.kachat.app.util.KaspaAddress.shortDisplay(it) } ?: ""
                    // This card only ever renders the user's own profile (ProfileScreen is
                    // own-account only), so the edit/create entry is always shown here.
                    // Label follows the same reactive condition as the destination: no profile
                    // yet means "Create KNS Profile", and it flips to "Edit KNS Profile" the
                    // moment activeProfileDomainName lands - no app restart needed.
                    val hasKnsProfile = !activeProfileDomainName.isNullOrBlank()
                    // iOS: the avatar row is pulled 38 up into the banner (.padding(.top, -38)),
                    // taking its layout space with it - so the name sits 8 under the avatar's
                    // row and the card ends 14 under the text, not 38 lower.
                    Column(modifier = Modifier.fillMaxWidth().profilePulledUp(38.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // 76 across, ringed by a 3pt stroke of the screen background centred
                            // on its edge (iOS .overlay(Circle().stroke(systemBackground, 3))).
                            val ringColor = LocalAppColors.current.background
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .drawWithContent {
                                        drawContent()
                                        drawCircle(ringColor, radius = size.minDimension / 2f, style = Stroke(width = 3.dp.toPx()))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                ContactAvatar(
                                    imageUrl = if (com.kachat.app.services.kachatnames.KachatNamesService.isEnabled) kachatAvatar else knsProfile?.avatarUrl,
                                    fallbackText = heroName,
                                    size = 76.dp
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            // KaChat's own profile (iOS 09e0403). .kas profiles are customized per
                            // domain in Your Domains instead; the app no longer shows them.
                            // subheadline semibold, bottom-aligned with the avatar, 4 up.
                            Text(
                                stringResource(R.string.edit_kachat_profile),
                                color = KaspaTeal,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                                    .clickable { navController.navigate("edit_kachat_profile") }
                            )
                        }
                        // VStack(spacing: 6): title3 bold name, subheadline bio, the link.
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 8.dp, bottom = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(heroName, color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 1)
                            (kachatBio ?: knsProfile?.bio?.takeIf { it.isNotBlank() && !com.kachat.app.services.kachatnames.KachatNamesService.isEnabled })?.let { bio ->
                                Text(bio, color = LocalAppColors.current.textSecondary, fontSize = 15.sp)
                            }
                            // The Linktree link: the one way a .kachat profile links anything else.
                            kachatLinktree?.let { linktree ->
                                val linktreeUriHandler = LocalUriHandler.current
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { runCatching { linktreeUriHandler.openUri(linktree) } }
                                ) {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        linktree.replace("https://", ""),
                                        color = KaspaTeal,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }

            run {
                // iOS HStack(spacing: 24) { Spacer; Receive; Spacer; Chatting; Spacer }: the two
                // buttons at their own width, the room left over shared by three equal spacers.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(Modifier.weight(1f))
                    ProfileCircleAction(
                        icon = Icons.Default.QrCode,
                        label = stringResource(R.string.receive_kaspa),
                    ) {
                        showAcceptPaymentQr = true
                    }
                    Spacer(Modifier.weight(1f))
                    ProfileCircleAction(
                        icon = Icons.Default.QrCode,
                        label = stringResource(R.string.chatting_address),
                    ) {
                        showFundIdentityQr = true
                    }
                    Spacer(Modifier.weight(1f))
                }
            }

            // iOS addressDropdownsSection: VStack(spacing: 20) { faucet (testnet); card }.
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Testnet only: free TN10 coins for the chatting address, from the official faucet
                // (iOS 182ae68).
                if (com.kachat.app.util.KaspaNetwork.isTestnet && address != null) {
                    TestnetFaucetClaimButton(
                        address = address!!,
                        balanceSompi = { viewModel.balanceSompi.value },
                        refreshBalance = { viewModel.refreshBalanceAndAwait() },
                    )
                }
                // One card for every address of this account (iOS 6333dd3 ContactsView
                // addressDropdownsSection): it opens Manage Addresses, which lists them all, the
                // chatting address first. Which address a send comes from is picked on the send
                // screen (From), so Profile no longer splits Chatting from Spending.
                ProfileManageAddressesCard(
                    totalSompi = accountTotalSompi,
                    onClick = { navController.navigate("manage_addresses") },
                )
            }

            if (pendingKnsCommit != null) {
                SettingsSection(title = null) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.a_kns_inscription_s_commit_transaction),
                            color = LocalAppColors.current.textSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.retryPendingKnsReveal() },
                            colors = ButtonDefaults.buttonColors(containerColor = KaspaTeal),
                            enabled = knsInscribeState.status != WalletViewModel.KnsInscribeUiStatus.SUBMITTING_REVEAL
                        ) {
                            Text(stringResource(R.string.retry_inscription_reveal), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Your domains are a thing you own, not a setting of the profile that happens to use
            // one of them - so they sit here rather than two levels down inside Edit KNS Profile,
            // where nothing on this screen suggested they existed.
            // Every name the account owns: .kachat, KNS, .k and .kaspa (iOS df23b6f, 10e4a1a).
            val ownedKnsDomains by viewModel.ownedDomains.collectAsState()
            val ownedServiceNames by viewModel.nameServices.owned.collectAsState()
            val kachatRevision by viewModel.kachatRegistryRevision.collectAsState()
            var kachatOwnedCount by remember(address) { mutableStateOf(0) }
            // The .k and .kaspa lookups, so the count includes them before the list opens.
            LaunchedEffect(address) {
                address?.let { viewModel.nameServices.refresh(it) }
            }
            // Reloads whenever the registry moves (a registration, sale or transfer lands).
            val kachatCacheContext = LocalContext.current
            LaunchedEffect(address, kachatRevision) {
                val owner = address ?: return@LaunchedEffect
                // the saved list's count at once; the read below corrects it when it answers
                // (KachatOwnedNamesCache, iOS wallet f4f5c0f)
                if (kachatOwnedCount == 0 && com.kachat.app.services.kachatnames.KachatNamesService.isLaunched) {
                    com.kachat.app.services.kachatnames.KachatOwnedNamesCache.load(kachatCacheContext, owner)?.let {
                        kachatOwnedCount = it.names.size
                    }
                }
                viewModel.watchKachatOwnedCount(owner) { kachatOwnedCount = it }
            }
            val domainsCount = kachatOwnedCount + ownedKnsDomains.size + ownedServiceNames.values.sumOf { it.size }
            // iOS: each a card of its own (glassBackground 18) - a Label (white outline symbol,
            // body text), the count for Your Domains, and a caption chevron, padded 16.
            ProfileLinkRow(
                label = stringResource(R.string.kns_domains),
                icon = Icons.Default.AlternateEmail,
                value = "$domainsCount",
                onClick = { navController.navigate("kns_domains") },
            )

            // KaPay: Settings is its own dock tab, and the Address Book opens from here (iOS
            // wallet 040f91a addressBookSection).
            ProfileLinkRow(
                label = stringResource(R.string.ab_address_book),
                icon = Icons.Outlined.Book,
                onClick = { navController.navigate(com.kachat.app.ui.Screen.AddressBook.route) },
            )

            ProfileLinkRow(
                label = stringResource(R.string.help),
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                onClick = { navController.navigate("help") },
            )

            // Log Out moved to the Settings tab, its last row (iOS wallet 040f91a).

            // Bottom-most section on Profile - merges what used to be a separate "Info" section
            // (just "Created") with Settings' old "About" section (Version/Website/Support
            // Email/Donate), now reached without needing to open Settings at all.
            // iOS aboutSection: a footnote "About" over one card, rows in iOS's order with
            // Open Source Licenses last.
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    stringResource(R.string.about),
                    color = LocalAppColors.current.textSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                Column(modifier = Modifier.fillMaxWidth().iosGlass(18.dp)) {
                    // Was a hardcoded literal. This is when the account was actually created or
                    // imported on this device (see WalletManager.accountAddedAt); an account that
                    // predates the stamp shows nothing rather than a made-up date.
                    val addedAtMillis = remember(address) { address?.let { viewModel.accountAddedAt(it) } }
                    ProfileAboutRow(
                        stringResource(R.string.created),
                        addedAtMillis?.let {
                            java.text.SimpleDateFormat("MMM d, yyyy 'at' h:mm a", java.util.Locale.getDefault())
                                .format(java.util.Date(it))
                        } ?: "--"
                    )
                    ProfileAboutDivider()
                    // "5.0 (1)" through the betas, "5.0" at release - so anyone can say which build
                    // they are on. Same shape as iOS's About row.
                    ProfileAboutRow(stringResource(R.string.version), com.kachat.app.util.AppVersion.display)
                    ProfileAboutDivider()
                    ProfileAboutRow(
                        stringResource(R.string.website),
                        "home.kachat.app",
                        onClick = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://home.kachat.app")))
                            } catch (e: Exception) { /* no browser available */ }
                        }
                    )
                    ProfileAboutDivider()
                    // The one accent value: iOS shows the address as a mail link.
                    ProfileAboutRow(
                        stringResource(R.string.support_email),
                        "kaspasilver@gmail.com",
                        valueColor = KaspaTeal,
                        onClick = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:kaspasilver@gmail.com")))
                            } catch (e: Exception) { /* no email app available */ }
                        }
                    )
                    ProfileAboutDivider()
                    ProfileAboutRow(
                        stringResource(R.string.donate),
                        DONATE_NAME,
                        onClick = { showDonateSheet = true }
                    )
                    ProfileAboutDivider()
                    // The notices WebRTC, Opus and Protocol Buffers require to ship with the binary,
                    // and the attribution the Apache/MIT libraries ask for. Same row as iOS's About.
                    ProfileAboutRow(
                        stringResource(R.string.open_source_licenses),
                        null,
                        onClick = { navController.navigate("open_source_licenses") }
                    )
                }
            }

            // Moved here from Settings > Actions - Profile is where the rest of the
            // account-level actions (address management, About) already live.

            // Room for the last card to scroll clear of the dock it runs under, and out from
            // under the floating Send and Receive button (iOS wallet 6d4bf75).
            Spacer(modifier = Modifier.height(dockInset + 16.dp + 56.dp))
        }

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // KaChat's floating up/down arrow, bottom right above the dock (iOS wallet 5bab351):
        // Send Kaspa, Receive Kaspa and Request Payment. (iOS hides it under its KNS save banner;
        // Android's profile save has no banner.)
        WalletActionsButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = dockInset + 16.dp),
            onClick = { showWalletActions = true },
        )

        }
    }

    // The same two QR pages the Chats New sheet opens (iOS ChattingAddressQRView /
    // ReceiveKaspaQRView), pushed here as iOS's Profile pushes them: over the whole screen, bar
    // included, the dock left in place, a back chevron to leave.
    PushedQrPage(visible = showFundIdentityQr, white = true, onBack = { showFundIdentityQr = false }) {
        ChattingAddressQrPage(
            address = address ?: "",
            balanceSompi = identityBalanceSompi,
            dismiss = QrPageDismiss.BACK,
            onDismiss = { showFundIdentityQr = false },
        )
    }
    PushedQrPage(visible = showAcceptPaymentQr, white = receiveQrAddress != null, onBack = { showAcceptPaymentQr = false }) {
        ReceiveKaspaQrPage(
            address = receiveQrAddress,
            resolving = receiveQrResolving,
            dismiss = QrPageDismiss.BACK,
            onDismiss = { showAcceptPaymentQr = false },
            walletViewModel = viewModel,
        )
    }

    // The floating button's half sheet, then the tile picked once it is down (iOS wallet
    // 5bab351): Send from the main address with nothing filled in, the Receive Kaspa page, or a
    // new payment request.
    if (showWalletActions) {
        WalletActionsSheet(
            onDismiss = { showWalletActions = false },
            onPick = { action ->
                when (action) {
                    WalletAction.SEND -> showSendSheet = true
                    WalletAction.RECEIVE -> showAcceptPaymentQr = true
                    WalletAction.REQUEST -> showRequestPayment = true
                }
            },
        )
    }
    if (showSendSheet) {
        IosFullSheet(onDismissed = { showSendSheet = false }, swipeToDismiss = false) { close ->
            SpendingAddressSendFlow(
                fromAddress = address ?: "",
                balanceSompi = identityBalanceSompi,
                title = stringResource(R.string.chats_send_kaspa),
                viewModel = viewModel,
                portfolioViewModel = portfolioViewModel,
                onDone = close,
                presentedAsSheet = true,
            )
        }
    }
    if (showRequestPayment) {
        CreatePaymentRequestSheet(walletViewModel = viewModel, onDismissed = { showRequestPayment = false })
    }
}

/**
 * Dedicated KNS domain-management screen — the owned-domain list (star to mark primary, swap
 * icon to transfer), plus inscribing a new domain. Used to live inline as a collapsible section
 * on [ProfileScreen] itself; broken out once the list plus its two dialogs (inscribe/transfer)
 * made that screen too crowded to scan at a glance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnsDomainsScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val ownedDomainAssets by viewModel.ownedDomainAssets.collectAsState()
    // Testnet: a .kachat name opened from the .kachat tab - its live detail (iOS 5df42b4).
    var selectedKachat by remember { mutableStateOf<com.kachat.app.services.kachatnames.NameInfo?>(null) }
    // Inscribe: the .kachat marketplace over Your Domains, whether or not .kachat is in the dock
    // or Kaspa Hub; the list refreshes when it closes (iOS e4da63d).
    var showKachatMarket by remember { mutableStateOf(false) }
    var kachatRefreshRequested by remember { mutableStateOf(false) }
    // The name service whose card was tapped (iOS debd6fd), null on the cards themselves. Held
    // above the marketplace's early return, so closing the marketplace lands back on .kachat.
    var openedService by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<com.kachat.app.services.NameServiceTLD?>(null) }
    val walletAddress by viewModel.address.collectAsState()
    // The cards' counts (iOS 2ee667c), kept while a card's screen is open.
    val ownedServiceNames by viewModel.nameServices.owned.collectAsState()
    val kachatCount = rememberKachatHeldCount(walletAddress ?: "")
    if (showKachatMarket) {
        androidx.activity.compose.BackHandler { showKachatMarket = false; kachatRefreshRequested = true }
        KachatMarketScreen(
            onBack = { showKachatMarket = false; kachatRefreshRequested = true },
        )
        return
    }
    val primaryDomainName by viewModel.primaryDomainName.collectAsState()
    val setPrimaryState by viewModel.setPrimaryState.collectAsState()
    var selectedDomain by remember { mutableStateOf<com.kachat.app.services.KnsAsset?>(null) }
    var showSendScreen by remember { mutableStateOf(false) }
    var editingDomain by remember { mutableStateOf<com.kachat.app.services.KnsAsset?>(null) }

    LaunchedEffect(Unit) {
        viewModel.refreshOwnedDomainsAndAwait()
    }
    LaunchedEffect(Unit) {
        viewModel.refreshServiceNamesAndAwait()
    }

    selectedKachat?.let { info ->
        AboveDock { KachatLiveNameDetailScreen(info, onBack = { selectedKachat = null }) }
        return
    }

    // A .kas domain's own profile, edited on its own (iOS 09e0403): .kas profiles are no longer
    // shown in the app, but a domain's profile is still its owner's to change.
    editingDomain?.let { domain ->
        AboveDock { KnsDomainProfileEditorScreen(domain = domain, viewModel = viewModel, onBack = { editingDomain = null }) }
        return
    }

    // Full-screen swap: domain detail (card, primary status, Send entry point) - reached by
    // tapping a card in the list below. No transfer-history section: KNS only exposes a
    // "currently owned assets" endpoint, and this app's own KNS-transfer tracking is a one-shot
    // chat notification, not a persisted per-domain log, so there's no reliable data source yet.
    selectedDomain?.let { domain ->
        if (!showSendScreen) {
            val isPrimary = domain.asset != null && domain.asset == primaryDomainName
            val settingThisOne = setPrimaryState.inFlight && setPrimaryState.assetId == domain.assetId
            AboveDock {
                KnsDomainDetailScreen(
                    domain = domain,
                    isPrimary = isPrimary,
                    settingInFlight = settingThisOne,
                    onSetPrimary = { domain.assetId?.let { viewModel.setPrimaryDomain(it) } },
                    onSend = {
                        viewModel.resetTransferDomainState()
                        showSendScreen = true
                    },
                    onCustomizeProfile = { editingDomain = domain },
                    onBack = { selectedDomain = null }
                )
            }
            return
        } else {
            AboveDock {
                KnsDomainSendScreen(
                    domain = domain,
                    viewModel = viewModel,
                    onDone = {
                        showSendScreen = false
                        selectedDomain = null
                    },
                    onBack = { showSendScreen = false }
                )
            }
            return
        }
    }

    // The service a card opened: its own screen, titled by its ending, holding the list each
    // service's tab used to show (iOS debd6fd - these used to be tabs on this screen).
    openedService?.let { tld ->
        androidx.activity.compose.BackHandler { openedService = null }
        Scaffold(
            containerColor = LocalAppColors.current.background,
            // Runs on under the dock, as iOS's content runs on under its tab bar; each list clears
            // it (its pinned button above the dock, or its scroll's end).
            contentWindowInsets = WindowInsets(0),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(tld.suffix, color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        SheetBarBackButton(onClick = { openedService = null }, modifier = Modifier.padding(start = 12.dp))
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
                )
            },
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (tld) {
                    com.kachat.app.services.NameServiceTLD.KAS -> KnsDomainListTab(
                        domains = ownedDomainAssets.filter { it.asset != null },
                        primaryDomainName = primaryDomainName,
                        onRefresh = { viewModel.refreshOwnedDomainsAndAwait() },
                        onSelect = { selectedDomain = it },
                    )
                    com.kachat.app.services.NameServiceTLD.K, com.kachat.app.services.NameServiceTLD.KASPA ->
                        ServiceNameListTab(tld = tld, viewModel = viewModel)
                    // Live on both networks (testnet-10, and mainnet since iOS ef6b21e). The
                    // "coming" note stays, unreachable.
                    com.kachat.app.services.NameServiceTLD.KACHAT ->
                        if (com.kachat.app.services.kachatnames.KachatNamesService.isEnabled) {
                            KachatLiveDomainsTab(
                                walletAddress = walletAddress ?: "",
                                onOpen = { selectedKachat = it },
                                onInscribe = { showKachatMarket = true },
                                refreshRequested = kachatRefreshRequested,
                                onRefreshHandled = { kachatRefreshRequested = false },
                            )
                        } else {
                            KachatNamesComingSoon()
                        }
                }
            }
        }
        return
    }

    Scaffold(
        containerColor = LocalAppColors.current.background,
        // Runs on under the dock, as iOS's scroll runs on under its tab bar.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Domains", color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    SheetBarBackButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        },
    ) { padding ->
        // One square card per name ending, KaChat's own first: .kachat, KNS (.kas), dotk (.k),
        // Kaspa Names (.kaspa) - the same tile look as Kaspa Hub, two across (iOS debd6fd).
        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp + LocalDockInset.current),
        ) {
            items(com.kachat.app.services.NameServiceTLD.entries.size) { i ->
                val tld = com.kachat.app.services.NameServiceTLD.entries[i]
                // How many names this wallet has with each service, 0 included; null while the
                // answer is not in yet (first load, or the service unreachable) - iOS 2ee667c.
                val count = when (tld) {
                    com.kachat.app.services.NameServiceTLD.KAS -> ownedDomainAssets.count { it.asset != null }
                    com.kachat.app.services.NameServiceTLD.K, com.kachat.app.services.NameServiceTLD.KASPA -> ownedServiceNames[tld]?.size
                    com.kachat.app.services.NameServiceTLD.KACHAT -> kachatCount
                }
                DomainServiceCard(tld = tld, count = count, onClick = { openedService = tld })
            }
        }
    }
}

/**
 * A Kaspa Hub style square (iOS debd6fd `serviceCard`): the name ending big in the brand style,
 * who runs it under it. Same fill, 18 radius and 0.8 white hairline at 15% as the hub tiles.
 */
@Composable
private fun DomainServiceCard(
    tld: com.kachat.app.services.NameServiceTLD,
    /** How many names you hold there; null draws a dash until the answer is in (iOS 2ee667c). */
    count: Int?,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .background(LocalAppColors.current.surface)
            .border(0.8.dp, Color.White.copy(alpha = 0.15f), shape)
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // iOS .title2 rounded, heavy, in the accent colour; one line, shrinking to 0.6.
            KachatFitText(
                tld.suffix,
                color = KaspaTeal,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = IosRoundedFontFamily,
                minScale = 0.6f,
            )
            // iOS .caption semibold, secondary.
            Text(
                tld.serviceName,
                color = LocalAppColors.current.textSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // How many you have there, 0 included; a dash until the answer is in.
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .defaultMinSize(minWidth = 28.dp, minHeight = 22.dp)
                    .clip(RoundedCornerShape(50))
                    .background(KaspaTeal.copy(alpha = 0.14f))
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    count?.toString() ?: "\u2013",
                    color = KaspaTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
            }
        }
    }
}

/** A pull-to-refresh list with the service's "Get a ... domain" button pinned under it. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DomainTabList(
    tld: com.kachat.app.services.NameServiceTLD,
    onRefresh: suspend () -> Unit,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()
    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            onRefresh()
            pullRefreshState.endRefresh()
        }
    }
    // With no "Get a domain" button pinned under it, the list itself clears the dock.
    val hasGetButton = tld.websiteUrl != null && tld.websiteName != null
    val dockInset = LocalDockInset.current
    Column(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(Modifier.height(16.dp)) }
                content()
                item { Spacer(Modifier.height(if (hasGetButton) 16.dp else 16.dp + dockInset)) }
            }
            PullToRefreshContainer(state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))
        }
        GetDomainButton(tld)
    }
}

/**
 * "Get a .kas domain at knsdomains.org" - the pinned button under each outside service's tab,
 * opening that service's own site (iOS 718724b). KaChat creates only its own `.kachat` names; the
 * other services' names are registered with them. The same outlined capsule the in-app Inscribe
 * button was (not the teal fill: the cards above are teal-filled).
 */
@Composable
private fun GetDomainButton(tld: com.kachat.app.services.NameServiceTLD) {
    val url = tld.websiteUrl ?: return
    val site = tld.websiteName ?: return
    val uriHandler = LocalUriHandler.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Above the dock the page runs under.
            .padding(bottom = LocalDockInset.current)
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(LocalAppColors.current.surface)
            .border(1.5.dp, KaspaTeal, RoundedCornerShape(28.dp))
            .clickable { runCatching { uriHandler.openUri(url) } }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.get_domain_at, tld.suffix, site),
            color = KaspaTeal,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
    }
}

@Composable
private fun KnsDomainListTab(
    domains: List<com.kachat.app.services.KnsAsset>,
    primaryDomainName: String?,
    onRefresh: suspend () -> Unit,
    onSelect: (com.kachat.app.services.KnsAsset) -> Unit,
) {
    DomainTabList(tld = com.kachat.app.services.NameServiceTLD.KAS, onRefresh = onRefresh) {
        if (domains.isEmpty()) {
            item {
                Text(text = stringResource(R.string.no_domains_yet), color = LocalAppColors.current.textSecondary, modifier = Modifier.padding(16.dp))
            }
        } else {
            items(domains, key = { it.assetId ?: it.asset ?: it.hashCode().toString() }) { domainAsset ->
                KnsDomainCard(
                    domain = domainAsset,
                    isPrimary = domainAsset.asset == primaryDomainName,
                    modifier = Modifier.clickable { onSelect(domainAsset) }
                )
            }
        }
    }
}

@Composable
private fun ServiceNameListTab(tld: com.kachat.app.services.NameServiceTLD, viewModel: WalletViewModel) {
    val owned by viewModel.nameServices.owned.collectAsState()
    val loading by viewModel.nameServices.loading.collectAsState()
    val failed by viewModel.nameServices.failed.collectAsState()
    val names = owned[tld].orEmpty()
    DomainTabList(tld = tld, onRefresh = { viewModel.refreshServiceNamesAndAwait() }) {
        if (names.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                    when {
                        tld in loading && owned[tld] == null -> com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal)
                        tld in failed -> Text(
                            stringResource(R.string.could_not_reach_service, tld.serviceName),
                            color = LocalAppColors.current.textSecondary,
                            textAlign = TextAlign.Center,
                        )
                        else -> Text(
                            stringResource(R.string.no_names_yet, tld.suffix),
                            color = LocalAppColors.current.textSecondary,
                        )
                    }
                }
            }
        } else {
            items(names, key = { it.display }) { name ->
                DomainNameCard(
                    title = name.display,
                    badge = if (name.isProvisional) stringResource(R.string.settling) else null,
                )
            }
        }
    }
}

@Composable
private fun KachatNamesComingSoon() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp)
            .padding(bottom = LocalDockInset.current),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(44.dp))
        Text(stringResource(R.string.kachat_names_are_coming), color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(
            stringResource(R.string.kachat_names_coming_body),
            color = LocalAppColors.current.textSecondary,
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
        )
    }
}

/** Teal card matching the app's KNS domain branding - used both as the row style in [KnsDomainsScreen] and as the header of [KnsDomainDetailScreen]. */
@Composable
fun KnsDomainCard(domain: com.kachat.app.services.KnsAsset, isPrimary: Boolean = false, modifier: Modifier = Modifier) {
    DomainNameCard(title = domain.asset ?: "", badge = if (isPrimary) stringResource(R.string.primary) else null, modifier = modifier)
}

/** The teal name card, for any name service: the name, and an optional corner badge ("Primary"
 *  on KNS, "Settling" on a `.kaspa` name still inside its settling window) - iOS DomainNameCardView. */
@Composable
fun DomainNameCard(
    title: String,
    badge: String? = null,
    modifier: Modifier = Modifier,
    /** A small line in the bottom-right corner (Your Domains > .kachat: when it expires, iOS 72b7dc7). */
    footnote: String? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(KaspaTeal),
        contentAlignment = Alignment.Center
    ) {
        Text(
            title,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        if (badge != null) {
            Text(
                badge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        if (footnote != null) {
            Text(
                footnote,
                color = Color.Black.copy(alpha = 0.65f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(start = 14.dp, end = 14.dp, bottom = 10.dp)
            )
        }
    }
}

/**
 * Detail screen for a single owned domain - the card itself, primary/status info, and a
 * dedicated Send entry point. Reached by tapping a card in [KnsDomainsScreen]. No transfer
 * history section: KNS only exposes a "currently owned assets" endpoint, and this app's own
 * KNS-transfer tracking isn't a persisted per-domain log, so there's no reliable data source yet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnsDomainDetailScreen(
    domain: com.kachat.app.services.KnsAsset,
    isPrimary: Boolean,
    settingInFlight: Boolean,
    onSetPrimary: () -> Unit,
    onSend: () -> Unit,
    /** Opens the full profile editor for this domain (avatar, banner, bio, links). KaChat does not
     *  SHOW .kas profiles anymore - .kachat profiles take priority - but a .kas domain's own
     *  profile can still be customized here (iOS 09e0403). Null where the host offers no editor. */
    onCustomizeProfile: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(domain.asset ?: "", color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    SheetBarBackButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = onSend,
                    enabled = domain.assetId != null,
                    colors = ButtonDefaults.buttonColors(containerColor = KaspaTeal, disabledContainerColor = KaspaTeal.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.send), color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            KnsDomainCard(domain = domain, isPrimary = isPrimary)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LocalAppColors.current.surface)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.asset_id), color = LocalAppColors.current.textPrimary, modifier = Modifier.weight(1f))
                    Text(
                        domain.assetId ?: "",
                        color = LocalAppColors.current.textSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
                if (onCustomizeProfile != null && domain.assetId != null) {
                    HorizontalDivider(color = LocalAppColors.current.background)
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onCustomizeProfile() }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.customize_profile), color = LocalAppColors.current.textPrimary, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = KaspaTeal)
                    }
                }
                HorizontalDivider(color = LocalAppColors.current.background)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (!isPrimary && domain.assetId != null) Modifier.clickable(enabled = !settingInFlight) { onSetPrimary() }
                            else Modifier
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isPrimary) stringResource(R.string.primary_domain) else stringResource(R.string.set_as_primary),
                        color = LocalAppColors.current.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (settingInFlight) {
                        com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            if (isPrimary) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = KaspaTeal
                        )
                    }
                }
            }
        }
    }
}

/**
 * Sends (transfers) a single KNS domain inscription to a recipient address or KNS domain - same
 * UX conventions as the app's KAS send flows (KNS-domain-aware recipient, editable network fee)
 * but with no amount field or coin control, since a domain transfer moves the whole inscription
 * rather than a chosen KAS amount.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnsDomainSendScreen(
    domain: com.kachat.app.services.KnsAsset,
    viewModel: WalletViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit,
    /** Non-null: sign/fund the transfer from this spending-chain address index instead of the
     *  identity address (the spending-address detail screen's KNS Domains tab passes it). */
    fromSpendingAddressIndex: Int? = null,
    /** The source address matching [fromSpendingAddressIndex] — recipient validation checks the
     *  recipient differs from the SOURCE, not necessarily from the identity address. */
    sourceAddress: String? = null
) {
    val recipientPreview by viewModel.transferRecipientPreview.collectAsState()
    val transferState by viewModel.transferDomainState.collectAsState()
    val kaspaExplorer by viewModel.kaspaExplorer.collectAsState()
    val uriHandler = LocalUriHandler.current

    var recipientInput by remember { mutableStateOf("") }
    var feeTier by remember { mutableStateOf(ColdFeeTier.NORMAL) }
    var customFeeSompi by remember { mutableStateOf<Long?>(null) }
    var isEditingFee by remember { mutableStateOf(false) }
    var customFeeText by remember { mutableStateOf("") }

    val domainName = domain.asset ?: ""
    val assetId = domain.assetId ?: ""
    val baseFeeSompi = KnsInscriptionEngine.REVEAL_PRIORITY_FEE_SOMPI
    val priorityFeeSompi = customFeeSompi ?: (baseFeeSompi * feeTier.multiplier)
    val inFlight = transferState.status !in listOf(
        WalletViewModel.KnsInscribeUiStatus.IDLE,
        WalletViewModel.KnsInscribeUiStatus.SUCCESS,
        WalletViewModel.KnsInscribeUiStatus.FAILED
    )

    // Progress in its own sheet rather than a spinner inside the Send button: a transfer is a
    // commit, a wait, then a reveal, and a button that just spins through all of it is
    // indistinguishable from being stuck. Not dismissible - leaving mid-transfer would hide a
    // running on-chain operation.
    if (inFlight) {
        DomainTransferProgressSheet(domainName = domainName, status = transferState.status)
    }
    val canSend = !inFlight && recipientPreview?.resolvedAddress != null

    fun trimmedKas(sompi: Long): String {
        var text = "%.8f".format(sompi / 100_000_000.0)
        while (text.endsWith("0")) text = text.dropLast(1)
        if (text.endsWith(".")) text = text.dropLast(1)
        return text
    }

    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.send_domain), color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    SheetBarIconButton(Icons.Filled.ArrowBackIosNew, "Back", onClick = onBack, enabled = !inFlight, modifier = Modifier.padding(start = 12.dp), tint = LocalAppColors.current.textPrimary)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        },
        bottomBar = {
            if (transferState.status != WalletViewModel.KnsInscribeUiStatus.SUCCESS) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.transferDomain(domainName, assetId, priorityFeeSompi, fromSpendingAddressIndex) },
                        enabled = canSend,
                        colors = ButtonDefaults.buttonColors(containerColor = KaspaTeal, disabledContainerColor = KaspaTeal.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        if (inFlight) {
                            com.kachat.app.ui.theme.IosActivityIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.send), color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (transferState.status == WalletViewModel.KnsInscribeUiStatus.SUCCESS) {
                val result = transferState.result
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Icon(Icons.Default.CheckCircle, null, tint = LocalAppColors.current.success, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.sent), color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LocalAppColors.current.surface)
                            .padding(16.dp)
                    ) {
                        Text(stringResource(R.string.to), color = LocalAppColors.current.textSecondary, fontSize = 12.sp)
                        Text(result?.toAddress ?: "", color = LocalAppColors.current.textPrimary, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(LocalAppColors.current.surface)
                            .clickable { result?.revealTxId?.let { uriHandler.openUri(kaspaExplorer.txUrl(it)) } }
                            .padding(16.dp)
                    ) {
                        Text("Transaction ID · tap to view in ${kaspaExplorer.displayName}", color = LocalAppColors.current.textSecondary, fontSize = 12.sp)
                        Text(result?.revealTxId ?: "", color = KaspaTeal, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = onDone,
                        colors = ButtonDefaults.buttonColors(containerColor = KaspaTeal),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(stringResource(R.string.done), color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LocalAppColors.current.surface)
                    .padding(16.dp)
            ) {
                Text(domainName, color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold)
                Text(assetId, color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }

            when (transferState.status) {
                WalletViewModel.KnsInscribeUiStatus.SUBMITTING_COMMIT -> InscribeProgressRow(stringResource(R.string.submitting_commit_transaction))
                WalletViewModel.KnsInscribeUiStatus.SUBMITTING_REVEAL -> InscribeProgressRow(stringResource(R.string.submitting_reveal_transaction))
                WalletViewModel.KnsInscribeUiStatus.VERIFYING -> InscribeProgressRow(stringResource(R.string.verifying_new_ownership_on_chain_this))
                WalletViewModel.KnsInscribeUiStatus.FAILED -> {
                    Text(transferState.errorMessage ?: stringResource(R.string.something_went_wrong), color = LocalAppColors.current.danger)
                }
                else -> {
                    Text(stringResource(R.string.recipient_address).uppercase(), color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    com.kachat.app.ui.theme.IosTextField(
                        value = recipientInput,
                        onValueChange = {
                            recipientInput = it
                            viewModel.checkTransferRecipient(it, sourceAddress)
                        },
                        placeholder = { Text(stringResource(R.string.kaspa_qr_or_domain)) },
                        singleLine = true,
                        enabled = !inFlight,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LocalAppColors.current.textPrimary,
                            unfocusedTextColor = LocalAppColors.current.textPrimary,
                            focusedBorderColor = KaspaTeal,
                            unfocusedBorderColor = LocalAppColors.current.textSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    recipientPreview?.let { preview ->
                        Spacer(Modifier.height(4.dp))
                        // the shared card: the domain typed, else the address's own .kachat name
                        AddressResolutionCard(address = preview.resolvedAddress, domain = preview.resolvedName)
                        when {
                            preview.checking -> Text(stringResource(R.string.looking_up_domain), color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.bodySmall)
                            preview.errorMessage != null -> Text(preview.errorMessage, color = LocalAppColors.current.danger, style = MaterialTheme.typography.bodySmall)
                            preview.resolvedAddress != null && preview.resolvedName != null -> Column {
                                Text(stringResource(R.string.resolved_name, preview.resolvedName), color = LocalAppColors.current.success, style = MaterialTheme.typography.bodySmall)
                                MiddleEllipsisText(
                                    preview.resolvedAddress,
                                    color = LocalAppColors.current.textSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                )
                            }
                            preview.resolvedAddress != null -> Text("Resolves to: ${preview.resolvedAddress}", color = LocalAppColors.current.success, style = MaterialTheme.typography.bodySmall)
                        }
                        if (!preview.checking) {
                            OtherDomainsDropdown(preview.nameResolutions, preview.selectedTld) {
                                viewModel.selectTransferResolution(it, sourceAddress)
                            }
                        }
                    }
                    Text(
                        stringResource(R.string.enter_kaspa_address_or_domain_short_kachat_first),
                        color = LocalAppColors.current.textSecondary,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("FEE", color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(LocalAppColors.current.surface)
                    ) {
                        ColdFeeTier.entries.forEach { tier ->
                            val selected = feeTier == tier && customFeeSompi == null
                            Text(
                                tier.label,
                                color = if (selected) Color.Black else LocalAppColors.current.textSecondary,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) KaspaTeal else Color.Transparent)
                                    .clickable(enabled = !inFlight) {
                                        feeTier = tier
                                        customFeeSompi = null
                                        isEditingFee = false
                                    }
                                    .padding(vertical = 10.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.network_fee), color = LocalAppColors.current.textPrimary, modifier = Modifier.weight(1f))
                        if (isEditingFee) {
                            com.kachat.app.ui.theme.IosTextField(
                                value = customFeeText,
                                onValueChange = { customFeeText = it },
                                singleLine = true,
                                modifier = Modifier.width(110.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = LocalAppColors.current.textPrimary,
                                    unfocusedTextColor = LocalAppColors.current.textPrimary
                                )
                            )
                            IconButton(onClick = {
                                KaspaUnit.sompiFromUserText(customFeeText)?.let { sompi ->
                                    customFeeSompi = sompi
                                }
                                isEditingFee = false
                            }) {
                                Icon(Icons.Default.Check, null, tint = KaspaTeal)
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(enabled = !inFlight) {
                                    customFeeText = trimmedKas(priorityFeeSompi)
                                    isEditingFee = true
                                }
                            ) {
                                Text("${trimmedKas(priorityFeeSompi)} ${KaspaUnit.symbol}", color = KaspaTeal, textDecoration = TextDecoration.Underline)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Default.Edit, null, tint = KaspaTeal, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

        }
    }
}

/**
 * Every address of this account (iOS 6333dd3 ManageAddressesView), reached from Profile > Manage
 * Addresses: the chatting address first, then the spending addresses, each with its balance and
 * whether it was used, a "generate new address" action, rename, QR, hide. Which address a send
 * comes from is picked on the send screen itself (From), so there is no "primary" here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAddressesScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onNavigateToTxHistory: (Int) -> Unit = {},
    /** The chatting address's own screen (history, UTXOs, Domains, Send / Receive). */
    onOpenChattingAddress: () -> Unit = {},
    onNavigateToHidden: () -> Unit = {},
    onNavigateToVisibility: () -> Unit = {},
    onAddressPicked: ((com.kachat.app.services.WalletService.SpendingAddressEntry) -> Unit)? = null,
    settingsViewModel: com.kachat.app.viewmodels.SettingsViewModel = hiltViewModel(),
) {
    val addresses by viewModel.manageAddresses.collectAsState()
    val chattingAddress by viewModel.address.collectAsState()
    val chattingBalanceSompi by viewModel.balanceSompi.collectAsState()
    val loading by viewModel.manageAddressesLoading.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var qrAddress by remember { mutableStateOf<String?>(null) }
    var renamingEntry by remember { mutableStateOf<com.kachat.app.services.WalletService.SpendingAddressEntry?>(null) }
    var renameInput by remember { mutableStateOf("") }
    var showActionsMenu by remember { mutableStateOf(false) }
    val isDiscoveringAddresses by viewModel.discoveringAddresses.collectAsState()
    val discoveryProgress by viewModel.spendingDiscoveryProgress.collectAsState()
    var discoverySummary by remember { mutableStateOf<String?>(null) }
    val pullRefreshState = rememberPullToRefreshState()

    val domainOwningAddresses by viewModel.domainOwningAddresses.collectAsState()
    // Quantum-safe rows whose key a spend has put on chain (their shield is off for good).
    val quantumSpentAddresses by viewModel.quantumSpentAddresses.collectAsState()
    // The spending addresses under the Chatting Address: unused ones first, newest first - so a
    // freshly generated address lands right under the Chatting Address - then the used ones in
    // number order (iOS wallet 59593af). One still being checked counts as unused until its
    // check says otherwise.
    val visibleAddresses = remember(addresses) {
        fun used(a: com.kachat.app.services.WalletService.SpendingAddressEntry) = a.everUsed || a.balanceSompi > 0
        addresses.filterNot { it.hidden }.sortedWith { a, b ->
            val aUsed = used(a)
            val bUsed = used(b)
            when {
                aUsed != bUsed -> if (aUsed) 1 else -1
                aUsed -> a.index.compareTo(b.index)
                else -> b.index.compareTo(a.index)
            }
        }
    }
    val hiddenAddresses = remember(addresses) { addresses.filter { it.hidden } }

    LaunchedEffect(Unit) {
        viewModel.loadManageAddresses()
    }

    // Pull-to-refresh owns its spinner end-to-end (same pattern as the Profile and Domains
    // screens): await the refresh, then dismiss. NEVER keyed off manageAddressesLoading — that
    // flag only flips on the empty-list initial load (a warm pull leaves it false the whole
    // time, so a spinner waiting on it spun forever) and is also driven by background reloads,
    // which must not surface or dismiss the pull indicator.
    LaunchedEffect(pullRefreshState.isRefreshing) {
        if (pullRefreshState.isRefreshing) {
            viewModel.loadManageAddressesAndAwait()
            pullRefreshState.endRefresh()
        }
    }

    val dockInset = LocalDockInset.current
    Scaffold(
        containerColor = LocalAppColors.current.background,
        // Runs on under the dock, as iOS's list runs on under its tab bar.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.manage_addresses), color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    SheetBarBackButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                // Address Visibility used to be an unlabelled checklist glyph here. It is one
                // of this account's address actions, so it lives with the others in the Address
                // Actions sheet, where it has room to say what it does.
                actions = {},
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        },
    ) { padding ->
        // Half sheet, opened by the button under Total Balance rather than a FAB - see the
        // header item below, and ManageAddressesActionsSheet for the discovery readout.
        if (showActionsMenu) {
            ManageAddressesActionsSheet(
                isDiscovering = isDiscoveringAddresses,
                progress = discoveryProgress,
                summary = discoverySummary,
                onGenerate = {
                    showActionsMenu = false
                    viewModel.generateNewSpendingAddress { index ->
                        // The chain-local number, never the raw public index (1000000+ for a quantum slot).
                        if (index != null) IosToasts.show("${com.kachat.app.services.WalletManager.spendingAddressTitle(index)} is ready.")
                        else IosToasts.error("Could not check addresses. Try again when connected.")
                    }
                },
                onDiscover = {
                    discoverySummary = null
                    viewModel.discoverSpendingAddresses { count ->
                        // The count is addresses that hold a balance or a KNS domain - say so,
                        // rather than "used", which is what the old number implied and was not.
                        val summary = if (count == 0) "No addresses with a balance or domain found."
                        else "Found $count address${if (count == 1) "" else "es"} with a balance or domain."
                        discoverySummary = summary
                        // Closed the sheet and carried on? Then the summary above has nowhere to
                        // render, so say it here instead of finishing silently.
                        if (!showActionsMenu) IosToasts.show(summary)
                    }
                },
                onVisibility = { showActionsMenu = false; onNavigateToVisibility() },
                onDismiss = { showActionsMenu = false; discoverySummary = null },
            )
        }
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Same fix as PortfolioScreen: an idle PullToRefreshContainer "hides" by
                // translating one full container-height above its own position via graphicsLayer,
                // and graphicsLayer translation does NOT clip. On screens whose pull Box is the
                // first child under the Scaffold padding, that resting gray circle lands in the
                // top-app-bar band and the opaque app bar draws over it. clipToBounds keeps the
                // indicator inside this Box: invisible at rest, revealed by a real pull.
                .clipToBounds()
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
        // iOS `addressesTabContent`: a plain List on the screen's own background - the account
        // summary edge to edge (20 in), the Notify switch as a plain row, then the chatting
        // address and the spending addresses as glass cards 16 in, 6 above and below each.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Total, then the actions button directly under it rather than a FAB floating over
            // the list: the actions are about this account, so they belong with it, and the FAB
            // covered the last address row on a short list. Same shape as Cold Storage. The total
            // is everything this account holds: the chatting address and every spending address,
            // hidden ones too - the same total as Profile's Manage Addresses card (iOS 245a5d2).
            // No card: iOS `accountSummary` sits straight on the list.
            if (onAddressPicked == null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(stringResource(R.string.total_balance), color = LocalAppColors.current.textSecondary, fontSize = 12.sp)
                            Text(
                                "%.8f ${KaspaUnit.symbol}".format(java.util.Locale.US, (chattingBalanceSompi + addresses.sumOf { it.balanceSompi }) / 100_000_000.0),
                                color = LocalAppColors.current.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                        Button(
                            onClick = { showActionsMenu = true },
                            enabled = !isDiscoveringAddresses,
                            // iOS's .plain button keeps its accent capsule while busy.
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KaspaTeal,
                                contentColor = Color.Black,
                                disabledContainerColor = KaspaTeal,
                                disabledContentColor = Color.Black,
                            ),
                            shape = CircleShape,
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            if (isDiscoveringAddresses) {
                                com.kachat.app.ui.theme.IosActivityIndicator(strokeWidth = 2.dp, color = Color.Black, modifier = Modifier.size(18.dp))
                            } else {
                                Text(stringResource(R.string.address_actions), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            run {
                // This account's own switch (a cold-storage account has one in its own menu):
                // the spending addresses' receives, not every address the wallet watches
                // (iOS ba352a2). A plain list row under the summary, no card.
                item {
                    val spendingNotify by settingsViewModel.spendingReceiveNotifications.collectAsState()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                "Notify on receive",
                                color = LocalAppColors.current.textPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                            )
                            Text(
                                "When a spending address receives Kaspa from someone else.",
                                color = LocalAppColors.current.textSecondary,
                                fontSize = 12.sp,
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        com.kachat.app.ui.theme.IosSwitch(
                            checked = spendingNotify,
                            onCheckedChange = { settingsViewModel.setSpendingReceiveNotifications(it) }
                        )
                    }
                }
            }
            // The chatting address, first in the list: it opens its own screen (history, UTXOs,
            // Domains, Send / Receive), the same one Profile used to.
            val chatting = chattingAddress
            if (onAddressPicked == null && chatting != null) {
                item {
                    ManageChattingAddressRow(
                        address = chatting,
                        balanceSompi = chattingBalanceSompi,
                        onClick = onOpenChattingAddress,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
            }
            if (onAddressPicked != null) {
                item {
                    Text(
                        stringResource(R.string.tap_an_address_below_to_swap),
                        color = KaspaTeal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
            // The Hidden (n) sub-screen survives only for Swap's address picker — the normal
            // screen manages visibility through the checklist button instead (iOS parity).
            if (hiddenAddresses.isNotEmpty() && onAddressPicked != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onNavigateToHidden)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VisibilityOff, null, tint = LocalAppColors.current.textSecondary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Hidden (${hiddenAddresses.size})",
                            color = LocalAppColors.current.textSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = LocalAppColors.current.textSecondary)
                    }
                }
            }

            if (loading && addresses.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), contentAlignment = Alignment.Center) {
                        com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal)
                    }
                }
            } else if (visibleAddresses.isEmpty()) {
                // iOS `emptyState`.
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(Icons.Default.CreditCard, null, tint = LocalAppColors.current.textSecondary, modifier = Modifier.size(40.dp))
                        Text("No Spending Addresses", color = LocalAppColors.current.textPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Generate one below to start sending payments from a separate address than the one you chat from.",
                            color = LocalAppColors.current.textSecondary,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                items(visibleAddresses, key = { it.index }) { entry ->
                    ManageAddressRow(
                        entry = entry,
                        showsDomainTag = entry.address in domainOwningAddresses,
                        quantumSpent = entry.address.lowercase() in quantumSpentAddresses,
                        onClick = { if (onAddressPicked != null) onAddressPicked(entry) else onNavigateToTxHistory(entry.index) },
                        onCopyClick = {
                            clipboardManager.setText(AnnotatedString(entry.address))
                            showAddressCopiedToast(context, entry.address)
                        },
                        onQrClick = { qrAddress = entry.address },
                        onRenameClick = { renamingEntry = entry; renameInput = entry.label ?: "" },
                        onHideClick = {
                            // iOS hideAddress: any address, funded too - it comes back from
                            // Address Visibility (iOS 245a5d2).
                            viewModel.setManageAddressHidden(entry.index, true) { ok ->
                                if (ok) IosToasts.show("Address hidden. Re-enable it in Address Visibility.")
                                else IosToasts.show("This address can't be hidden.")
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    )
                }
            }

            item {
                // The last address scrolls clear of the dock the list runs under.
                Spacer(Modifier.height(16.dp + dockInset))
            }
        }

        // Hard guarantee on top of the clipToBounds above: the indicator is only ever composed
        // while a pull is actually in progress or a refresh is running. At rest (offset 0, not
        // refreshing) there is nothing to draw, so no layout quirk can park the gray circle
        // over the content again.
        if (pullRefreshState.verticalOffset > 0f || pullRefreshState.isRefreshing) {
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        qrAddress?.let { address ->
            QrCodeOverlay(value = address, onDismiss = { qrAddress = null })
        }
        }
        }
    }

    renamingEntry?.let { entry ->
        RenameAddressDialog(
            index = entry.index,
            nameInput = renameInput,
            onNameChange = { renameInput = it },
            onDismiss = { renamingEntry = null },
            onSave = {
                viewModel.setManageAddressLabel(entry.index, renameInput)
                renamingEntry = null
            }
        )
    }
}

/** Rename dialog shared by [ManageAddressesScreen] and [ManageAddressesHiddenScreen] — an empty/blank name clears back to the default "Address #N". */
@Composable
private fun RenameAddressDialog(
    index: Int,
    nameInput: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    com.kachat.app.ui.theme.IosAlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LocalAppColors.current.surface,
        title = { Text(stringResource(R.string.rename_address), color = LocalAppColors.current.textPrimary) },
        text = {
            Column {
                Text(com.kachat.app.services.WalletManager.spendingAddressTitle(index), color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                com.kachat.app.ui.theme.IosTextField(
                    value = nameInput,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.name)) },
                    placeholder = { Text(com.kachat.app.services.WalletManager.spendingAddressTitle(index)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LocalAppColors.current.textPrimary,
                        unfocusedTextColor = LocalAppColors.current.textPrimary,
                        focusedBorderColor = KaspaTeal,
                        unfocusedBorderColor = LocalAppColors.current.textSecondary,
                        focusedLabelColor = KaspaTeal,
                        unfocusedLabelColor = LocalAppColors.current.textSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(R.string.save), color = KaspaTeal, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = LocalAppColors.current.textSecondary)
            }
        }
    )
}

/**
 * Every hidden spending address, reached via the "Hidden (N)" link on [ManageAddressesScreen] —
 * the only place a hidden address can be unhidden again. Shares [viewModel]'s own `manageAddresses`
 * list rather than loading a separate one, so it's always in sync with the main screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAddressesHiddenScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onNavigateToTxHistory: (Int) -> Unit = {},
    onAddressPicked: ((com.kachat.app.services.WalletService.SpendingAddressEntry) -> Unit)? = null
) {
    val addresses by viewModel.manageAddresses.collectAsState()
    val quantumSpentAddresses by viewModel.quantumSpentAddresses.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var qrAddress by remember { mutableStateOf<String?>(null) }
    var renamingEntry by remember { mutableStateOf<com.kachat.app.services.WalletService.SpendingAddressEntry?>(null) }
    var renameInput by remember { mutableStateOf("") }

    val hiddenAddresses = remember(addresses) {
        addresses.filter { it.hidden }
            .sortedWith(compareByDescending<com.kachat.app.services.WalletService.SpendingAddressEntry> { it.balanceSompi > 0 }.thenByDescending { it.index })
    }

    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.hidden_addresses), color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    SheetBarBackButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        }
    ) { padding ->
        if (hiddenAddresses.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.VisibilityOff, null, tint = LocalAppColors.current.textSecondary, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.no_hidden_addresses), color = LocalAppColors.current.textSecondary, textAlign = TextAlign.Center)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (onAddressPicked != null) {
                        item {
                            Text(
                                stringResource(R.string.tap_an_address_below_to_use),
                                color = KaspaTeal,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    items(hiddenAddresses, key = { it.index }) { entry ->
                        ManageAddressRow(
                            entry = entry,
                            onClick = { if (onAddressPicked != null) onAddressPicked(entry) else onNavigateToTxHistory(entry.index) },
                            onCopyClick = {
                                clipboardManager.setText(AnnotatedString(entry.address))
                                showAddressCopiedToast(context, entry.address)
                            },
                            onQrClick = { qrAddress = entry.address },
                            onRenameClick = { renamingEntry = entry; renameInput = entry.label ?: "" },
                            quantumSpent = entry.address.lowercase() in quantumSpentAddresses,
                        )
                    }
                }
                qrAddress?.let { address ->
                    QrCodeOverlay(value = address, onDismiss = { qrAddress = null })
                }
            }
        }
    }

    renamingEntry?.let { entry ->
        RenameAddressDialog(
            index = entry.index,
            nameInput = renameInput,
            onNameChange = { renameInput = it },
            onDismiss = { renamingEntry = null },
            onSave = {
                viewModel.setManageAddressLabel(entry.index, renameInput)
                renamingEntry = null
            }
        )
    }
}

/**
 * Address Visibility (iOS parity: SpendingAddressVisibilityView, d7e6dd1): first a card per
 * chain - Quantum Addresses and Non-Quantum Addresses - then that chain's checklist
 * ([AddressVisibilityChainList]). One pager over raw indices could never reach the quantum chain,
 * which starts at [com.kachat.app.services.WalletManager.QUANTUM_INDEX_BASE]. Hide All
 * (confirmed) covers both chains and starts the list over with only the chatting address
 * (iOS 245a5d2). The cards and the list read the same loaded rows, so a toggle updates the counts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressVisibilityScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val addresses by viewModel.manageAddresses.collectAsState()
    val loading by viewModel.manageAddressesLoading.collectAsState()
    var showHideAllConfirm by remember { mutableStateOf(false) }
    // The chain whose card was tapped (true = quantum), null on the cards themselves.
    var openedChain by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<Boolean?>(null) }

    // Refresh on entry: rows inherited from a previous screen visit can carry a stale
    // isCurrent/balance (the primary rotates after every send), and the toggle guards in the
    // checklist read those flags. The live reload also runs the self-heal purge for any wrongly
    // hidden primary/funded index.
    LaunchedEffect(Unit) { viewModel.loadManageAddresses() }

    openedChain?.let { quantum ->
        androidx.activity.compose.BackHandler { openedChain = null }
        AddressVisibilityChainList(viewModel = viewModel, quantum = quantum, onBack = { openedChain = null })
        return
    }

    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Address Visibility", color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Hide All leads the bar, Done trails it (iOS 245a5d2).
                    SheetBarButton(stringResource(R.string.hide_all), onClick = { showHideAllConfirm = true }, modifier = Modifier.padding(start = 12.dp))
                },
                actions = {
                    SheetBarButton(stringResource(R.string.done), onClick = onBack, modifier = Modifier.padding(end = 12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        }
    ) { padding ->
        // Two Kaspa Hub style squares, quantum first (iOS d7e6dd1).
        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp),
        ) {
            items(2) { i ->
                val quantum = i == 0
                // "n of m shown": m = this chain's revealed addresses (every row the loaded list
                // holds for it), n = those Manage Addresses shows (not hidden).
                val chain = addresses.filter { it.isQuantum == quantum }
                AddressVisibilityChainCard(
                    quantum = quantum,
                    shown = chain.count { !it.hidden },
                    total = chain.size,
                    loading = loading,
                    onClick = { openedChain = quantum }
                )
            }
        }
    }

    if (showHideAllConfirm) {
        com.kachat.app.ui.theme.IosAlertDialog(
            onDismissRequest = { showHideAllConfirm = false },
            containerColor = LocalAppColors.current.surface,
            title = { Text(stringResource(R.string.hide_all_addresses_title), color = LocalAppColors.current.textPrimary) },
            text = { Text(stringResource(R.string.hide_all_addresses_message), color = LocalAppColors.current.textSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showHideAllConfirm = false
                    // Every revealed address hidden, both chains.
                    viewModel.hideAllSpendingAddresses()
                }) {
                    Text(stringResource(R.string.hide_all), color = LocalAppColors.current.danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHideAllConfirm = false }) {
                    Text(stringResource(R.string.cancel), color = LocalAppColors.current.textSecondary)
                }
            }
        )
    }
}

/**
 * A Kaspa Hub style square (iOS d7e6dd1 `chainCard`): the chain's glyph, its name, and how many
 * of its addresses Manage Addresses shows. Same fill, 18 radius and 0.8 white hairline at 15% as
 * the hub tiles and the Your Domains cards.
 */
@Composable
private fun AddressVisibilityChainCard(
    quantum: Boolean,
    shown: Int,
    total: Int,
    loading: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(shape)
            .background(LocalAppColors.current.surface)
            .border(0.8.dp, Color.White.copy(alpha = 0.15f), shape)
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (quantum) {
                // "shield.fill"
                Icon(Icons.Filled.Shield, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(26.dp))
            } else {
                // "circle.grid.2x2.fill": four dots in a square.
                androidx.compose.foundation.Canvas(Modifier.size(26.dp)) {
                    val r = size.minDimension * 0.2f
                    for (cx in listOf(0.27f, 0.73f)) for (cy in listOf(0.27f, 0.73f)) {
                        drawCircle(KaspaTeal, radius = r, center = Offset(size.width * cx, size.height * cy))
                    }
                }
            }
            // iOS .subheadline semibold, primary, up to two lines.
            Text(
                stringResource(if (quantum) R.string.quantum_addresses else R.string.non_quantum_addresses),
                color = LocalAppColors.current.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (loading) {
                com.kachat.app.ui.theme.IosActivityIndicator(modifier = Modifier.size(16.dp))
            } else {
                // iOS .caption, secondary.
                Text(
                    stringResource(R.string.addresses_shown_count, shown, total),
                    color = LocalAppColors.current.textSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/**
 * One chain's Address Visibility checklist (iOS d7e6dd1 `AddressVisibilityChainList`) — a compact
 * checkmark list of EVERY address on that chain, paged 50 at a time from the chain's own start
 * (the quantum chain at [com.kachat.app.services.WalletManager.QUANTUM_INDEX_BASE]) and numbered
 * from #0, so dozens can be toggled off the main Manage Addresses list in one sitting. The right
 * arrow never runs out: future pages derive addresses beyond the chain's revealed bound on the
 * fly, and toggling one on raises the bound while keeping the intermediate indices hidden. Every
 * address toggles both ways, funded ones too (iOS 245a5d2).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddressVisibilityChainList(
    viewModel: WalletViewModel,
    quantum: Boolean,
    onBack: () -> Unit
) {
    val addresses by viewModel.manageAddresses.collectAsState()
    var page by remember { mutableStateOf(0) }
    val pageSize = 50
    val byIndex = remember(addresses) { addresses.associateBy { it.index } }
    // The legacy bound: a quantum row's index sits a million up and must not stretch it.
    val listMax = remember(addresses) { addresses.filterNot { it.isQuantum }.maxOfOrNull { it.index } ?: -1 }
    // This chain's own revealed bound - past it, rows are derived on the fly.
    val chainMax = if (quantum) {
        addresses.filter { it.isQuantum }.maxOfOrNull { it.index }
            ?: (com.kachat.app.services.WalletManager.QUANTUM_INDEX_BASE - 1)
    } else {
        listMax
    }
    // Lazily filled Used/Unused results for rows derived beyond the loaded list.
    val usedCache = remember { mutableStateMapOf<Int, Boolean>() }

    // Chain-local numbers for the pager label; raw indices for the rows.
    val localStart = page * pageSize
    val localEnd = localStart + pageSize - 1
    val chainBase = if (quantum) com.kachat.app.services.WalletManager.QUANTUM_INDEX_BASE else 0
    val start = chainBase + localStart
    val end = chainBase + localEnd
    // Addresses for rows the loaded list does not cover, derived OFF the main thread and once
    // per page.
    //
    // This used to call `spendingAddressAt(index)` for each of the 50 rows from inside a
    // `remember` during composition. Every one of those redoes the BIP-39 seed derivation -
    // PBKDF2, 2048 rounds of HMAC-SHA512, deliberately slow - so a page flip did that fifty times
    // on the main thread before it could draw. That is what made this screen unresponsive enough
    // that a row could not be tapped. `deriveSpendingAddresses` pays the seed cost once for the
    // whole range, and it happens in a coroutine on the default dispatcher.
    var derivedAddresses by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    LaunchedEffect(page, chainMax) {
        val needed = (start..end).filter { it > chainMax }
        if (needed.isEmpty()) {
            derivedAddresses = emptyMap()
            return@LaunchedEffect
        }
        derivedAddresses = withContext(Dispatchers.Default) {
            viewModel.spendingAddressesAt(needed)
        }
    }
    // One sweep per page for the Used badges on derived rows, paced so a page flip is a trickle
    // rather than fifty simultaneous history probes. Cache-first: an address already known used
    // never asks the network again (used-ness is monotonic).
    LaunchedEffect(page, derivedAddresses) {
        for ((index, address) in derivedAddresses) {
            if (address.isEmpty() || index in usedCache) continue
            usedCache[index] = viewModel.hasSpendingAddressBeenUsed(address)
            delay(60)
        }
    }
    val pageEntries = remember(byIndex, page, derivedAddresses) {
        (start..end).map { index ->
            byIndex[index] ?: com.kachat.app.services.WalletService.SpendingAddressEntry(
                index = index,
                address = derivedAddresses[index].orEmpty(),
                balanceSompi = 0L,
                everUsed = false,
                isCurrent = false,
                hidden = true,
                label = null
            )
        }
    }
    val listState = rememberLazyListState()
    // A page flip must land the user at the TOP of the new page, not wherever the previous
    // page left the scroll offset.
    LaunchedEffect(page) { listState.scrollToItem(0) }

    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(if (quantum) R.string.quantum_addresses else R.string.non_quantum_addresses),
                        color = LocalAppColors.current.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                // Hide All and Done stay on the cards (iOS d7e6dd1); this list only goes back.
                navigationIcon = {
                    SheetBarBackButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LocalAppColors.current.background)
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { if (page > 0) page -= 1 }, enabled = page > 0) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        "Previous page",
                        tint = if (page > 0) KaspaTeal else LocalAppColors.current.textSecondary.copy(alpha = 0.4f)
                    )
                }
                Text(
                    "#$localStart - #$localEnd",
                    color = LocalAppColors.current.textSecondary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodySmall
                )
                IconButton(onClick = { page += 1 }) {
                    Icon(Icons.Default.ChevronRight, "Next page", tint = KaspaTeal)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(pageEntries, key = { it.index }) { entry ->
                val funded = entry.balanceSompi > 0
                // Every row toggles both ways - funded ones too (Hide All starts the list over).
                // A revealed quantum row sits a million past the legacy listMax, so it shows and
                // toggles by its own hidden flag (iOS ManageAddressesView's row: visible =
                // !hidden); only a slot the list doesn't hold yet is revealed instead.
                val listedQuantum = entry.isQuantum && entry.index in byIndex
                val visible = (listedQuantum || entry.index <= listMax) && !entry.hidden
                // Used-state comes from the page's own sweep below, not a per-row effect: fifty
                // rows each launching their own history probe fired fifty requests the moment a
                // page appeared, which is the other half of what made this screen unusable.
                val used = if (listedQuantum || entry.index <= listMax) entry.everUsed else usedCache[entry.index]
                // The WHOLE row toggles, not just the checkmark.
                val toggleVisibility: () -> Unit = {
                    when {
                        listedQuantum ->
                            viewModel.setManageAddressHidden(entry.index, !entry.hidden)
                        entry.index > listMax ->
                            viewModel.revealSpendingAddress(entry.index)
                        else -> viewModel.setManageAddressHidden(entry.index, !entry.hidden)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(LocalAppColors.current.surface)
                        .clickable(onClick = toggleVisibility)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .alpha(if (visible) 1f else 0.55f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = toggleVisibility,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            if (visible) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            if (visible) "Visible" else "Hidden",
                            tint = if (visible) KaspaTeal else LocalAppColors.current.textSecondary
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // The chain-local number; the shield marks the quantum chain's rows
                            // apart from the legacy chain's same numbers (iOS 0b4e27e).
                            Text(
                                "#${if (entry.isQuantum) entry.index - com.kachat.app.services.WalletManager.QUANTUM_INDEX_BASE else entry.index}",
                                color = LocalAppColors.current.textSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (entry.isQuantum) {
                                // iOS: SF Symbol "shield" (outline) at .caption2, secondary.
                                Icon(
                                    Icons.Outlined.Shield,
                                    contentDescription = stringResource(R.string.quantum_safe),
                                    tint = LocalAppColors.current.textSecondary,
                                    modifier = Modifier.size(11.dp),
                                )
                            }
                            entry.label?.takeIf { it.isNotBlank() }?.let { label ->
                                Text(label, color = LocalAppColors.current.textSecondary, fontSize = 12.sp, maxLines = 1)
                            }
                        }
                        Text(
                            if (entry.address.isNotEmpty()) "${entry.address.take(14)}...${entry.address.takeLast(6)}" else "deriving...",
                            color = LocalAppColors.current.textPrimary,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    when {
                        funded -> Text(
                            "%.4f ${KaspaUnit.symbol}".format(java.util.Locale.US, entry.balanceSompi / 100_000_000.0),
                            color = KaspaTeal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        used != null -> Text(
                            if (used) "Used" else "Unused",
                            color = if (used) LocalAppColors.current.warning else LocalAppColors.current.success,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Send Kaspa (iOS `WithdrawKaspaView`): Chats > + > Send Kaspa, Donate, the Address Book, an
 * address's Send, paying a payment request - and, in [isCompoundMode], Compound UTXOs for one
 * address. Every send but Compound starts with From ([SendFromSection], iOS 6333dd3): Chatting
 * Address (the default) or Choose an Address. Fees, Max, coin control and the send follow the
 * chosen address: the chatting address signs with the identity key
 * ([WalletViewModel.onSendClicked]), a spending address with its own key
 * ([WalletViewModel.withdrawFromSpendingAddress]); a payment request to a chatting address is a
 * chat payment from the chosen address. Never the external-QR-signer round trip [ColdSendFlow]
 * uses for watch-only keys.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingAddressSendFlow(
    fromAddress: String,
    balanceSompi: Long,
    title: String,
    viewModel: WalletViewModel,
    onDone: () -> Unit,
    // Compound UTXOs only: the spending-chain index of [fromAddress], or null for the chatting
    // address. Every other send picks its address under From.
    spendingIndex: Int? = null,
    // Pre-fills the recipient with fromAddress itself (a self-send) and auto-fills Max, for the
    // "Compound UTXOs" entry point - merges every UTXO at this address into one. Locks the
    // recipient field instead of just pre-filling it, matching ColdSendFlow's identical
    // isCompoundMode behavior, since editing it away from fromAddress would defeat the point of
    // a compound send.
    isCompoundMode: Boolean = false,
    /**
     * Shown inside a sheet ([IosFullSheet] - every send entry, as iOS presents them): the bar's
     * leading control is "Cancel", as iOS's SpendingAddressWithdrawView and WithdrawKaspaView have
     * it, and the bars take no system insets of their own - the sheet already sits clear of them.
     */
    presentedAsSheet: Boolean = false,
    /** Pre-fills the recipient (Address Book > Send KAS, iOS `WithdrawKaspaView(prefillAddress:)`);
     *  it stays editable. */
    prefillAddress: String? = null,
    /** Someone's payment request (a tapped or pasted link, iOS c6851d6 `WithdrawKaspaView
     *  (paymentRequest:)`): its address is locked in, its amount and memo filled in. A request
     *  pasted into the recipient field does the same (not in Compound). */
    paymentRequest: com.kachat.app.models.PaymentRequest? = null,
    /** Opened from one address's history (its Send button, iOS 7a8102b `lockedSource`): the Kaspa
     *  leaves from that address and the From section offers no other. */
    lockedSource: SendSource? = null,
    portfolioViewModel: com.kachat.app.viewmodels.PortfolioViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val kaspaExplorer by viewModel.kaspaExplorer.collectAsState()
    val fiatPriceInCurrency by portfolioViewModel.currentPriceUsd.collectAsState()
    val fiatCurrencyCode by portfolioViewModel.currency.collectAsState()
    var recipientInput by remember(fromAddress) { mutableStateOf(prefillAddress.orEmpty()) }
    var amountInput by remember(fromAddress) { mutableStateOf("") }
    val fiatAmountState = com.kachat.app.util.rememberKaspaFiatAmountState(resetKey = fromAddress, onKasTextChange = { amountInput = it })
    var showScanner by remember { mutableStateOf(false) }
    var manualUtxos by remember { mutableStateOf<List<UtxoEntry>?>(null) }
    var showCoinControl by remember { mutableStateOf(false) }
    var isEstimatingMax by remember { mutableStateOf(false) }
    var feeTier by remember { mutableStateOf(ColdFeeTier.NORMAL) }
    var customExtraFeeSompi by remember { mutableStateOf<Long?>(null) }
    // Custom fee, edited in place on the fee row (SendFeeControls), as on iOS.
    var isEditingFee by remember { mutableStateOf(false) }
    var customFeeText by remember { mutableStateOf("") }
    /// The completed send, which the swipe-up screen shows as Sent (iOS wallet 59593af).
    var sentTransaction by remember { mutableStateOf<SentTransaction?>(null) }
    /** Why the last send failed, for the swipe-up screen's Failed (iOS wallet 59593af). */
    var sendError by remember { mutableStateOf<String?>(null) }
    // This presentation's own sends (iOS's per-sheet isSending/sentTransaction): one closed
    // mid-send leaves its send running in the view model, and its outcome is never shown here
    // or on the next send screen.
    val sendId = remember { java.util.UUID.randomUUID().toString() }
    DisposableEffect(sendId) {
        viewModel.sends.open(sendId)
        onDispose { viewModel.sends.close(sendId) }
    }
    val isSending = sendId in viewModel.sends.inFlight.collectAsState().value
    val sendResult = viewModel.sends.results.collectAsState().value[sendId]
    val coroutineScope = rememberCoroutineScope()

    /** The payment request this send pays, if any ([paymentRequest], or one pasted in). */
    var activeRequest by remember { mutableStateOf<com.kachat.app.models.PaymentRequest?>(null) }
    /** Fills in a payment request: the address (locked), the exact amount and the memo. The
     *  amount stays editable; whatever it says is what is sent, through the same exact parser. */
    fun applyRequest(request: com.kachat.app.models.PaymentRequest) {
        activeRequest = request
        recipientInput = request.address
        // the exact amount is what's sent; the field shows it in whichever unit it is in
        fiatAmountState.setExactKas(request.amountText, fiatPriceInCurrency)
    }

    // Where the Kaspa leaves from (the From section, iOS 6333dd3): the chatting address by
    // default. Compound UTXOs has no From - it merges [fromAddress] ([spendingIndex]) itself.
    var source by remember { mutableStateOf<SendSource>(lockedSource ?: SendSource.Chatting) }
    /** The From address's balance, loaded by [SendFromSection]. */
    var sourceBalanceSompi by remember { mutableStateOf<Long?>(null) }
    val chattingAddress by viewModel.address.collectAsState()
    val sourceIndex = if (isCompoundMode) spendingIndex else source.spendingIndex
    val sourceAddress = if (isCompoundMode) fromAddress else (source.address(chattingAddress) ?: fromAddress)
    // A quantum-safe (KIP-22) From prices every input with its redeem script too (iOS
    // signatureScriptSize: kip22SignatureScriptSize).
    val sourceSignatureScriptSize = com.kachat.app.services.WalletManager.signatureScriptSize(sourceIndex)

    var isResolvingKns by remember { mutableStateOf(false) }
    var knsResolvedAddress by remember { mutableStateOf<String?>(null) }
    var knsResolvedDomain by remember { mutableStateOf<String?>(null) }
    var knsError by remember { mutableStateOf<String?>(null) }
    // Every service's answer for a typed name and the one in use: the rest go under "Other
    // domains" (iOS 6ac48a7).
    var nameResolutions by remember { mutableStateOf<List<com.kachat.app.services.NameResolution>>(emptyList()) }
    var selectedTld by remember { mutableStateOf<com.kachat.app.services.NameServiceTLD?>(null) }
    // Use one service's answer (the priority one, or a pick from "Other domains").
    val selectResolution: (com.kachat.app.services.NameResolution) -> Unit = { resolution ->
        resolution.address?.let {
            knsResolvedAddress = it
            knsResolvedDomain = resolution.display
            selectedTld = resolution.tld
            knsError = null
        }
    }

    // The fee, as iOS prices it (SendKaspaComponents / SpendingAddressWithdrawView) and as the chat
    // Send KAS sheet already pays it: a base fee at the live quoted rate on the real inputs, plus
    // Fast / Priority / a custom fee as a flat extra on top (SendFeeModel). The send goes out with
    // no rate override (the engine prices the base at the quote) and the extra paid exactly - so a
    // multi-input send no longer scales the chosen fee by its input count.
    //
    // The quote is fetched once when the screen opens; until then the base is priced at the
    // network minimum, the floor of any quote.
    var liveFeeRateSompiPerGram by remember { mutableStateOf<Long?>(null) }
    val baseFeeRateSompiPerGram = liveFeeRateSompiPerGram ?: KaspaMass.MINIMUM_FEE_RATE_SOMPI_PER_GRAM
    // What automatic selection spends and pays for the amount typed (coin control off) - see the
    // preview LaunchedEffect below. Null while there is no amount, while it is being worked out,
    // or when the coins cannot cover it.
    var feePreview by remember { mutableStateOf<com.kachat.app.util.SendFeeModel.Preview?>(null) }
    var isPreviewingFee by remember { mutableStateOf(false) }
    // The amount Max last wrote, and the fee choice it left room for: changing the fee while the
    // field still holds that amount works Max out again (see setMaxAmount).
    var maxFill by remember { mutableStateOf<Triple<String, ColdFeeTier, Long?>?>(null) }
    // The one exact parser for typed KAS (iOS 16b64bc): comma or dot, at most 8 decimals.
    val previewAmountSompi = KaspaUnit.sompiFromUserText(amountInput)
    // Base fee on the real inputs: coin control's exact set, else the automatic-selection preview,
    // else (no amount yet) one input.
    // The recipient's real script length when it is known (a P2SH or ECDSA address is a byte
    // longer than the standard one), so the base priced here is the base the send is priced at.
    val feeRecipient = (knsResolvedAddress ?: recipientInput).trim()
    val recipientScriptLen = remember(feeRecipient) {
        if (KaspaAddress.isValid(feeRecipient)) {
            runCatching { KaspaAddress.getScriptPublicKey(feeRecipient).length / 2 }
                .getOrDefault(com.kachat.app.util.SendFeeModel.STANDARD_SCRIPT_LEN)
        } else {
            com.kachat.app.util.SendFeeModel.STANDARD_SCRIPT_LEN
        }
    }
    val manualInputCount = manualUtxos?.size?.takeIf { it > 0 }
    val defaultFeeSompi = when {
        manualInputCount != null -> com.kachat.app.util.SendFeeModel.baseFee(manualInputCount, baseFeeRateSompiPerGram, recipientScriptLen = recipientScriptLen, signatureScriptSize = sourceSignatureScriptSize)
        feePreview != null -> feePreview!!.baseFeeSompi
        else -> com.kachat.app.util.SendFeeModel.baseFee(1, baseFeeRateSompiPerGram, signatureScriptSize = sourceSignatureScriptSize)
    }
    // The preview settled its extra together with the inputs it needs; passing exactly that extra
    // makes the send pick those same inputs and pay the total shown.
    val extraFeeSompi = if (manualInputCount == null && feePreview != null) {
        feePreview!!.extraFeeSompi
    } else {
        com.kachat.app.util.SendFeeModel.extraFee(defaultFeeSompi, feeTier.multiplier, customExtraFeeSompi)
    }
    val effectiveFeeSompi = defaultFeeSompi + extraFeeSompi

    // Debounced KNS domain resolution - lets typing "name.kas" here resolve the same way Create
    // Chat's own address field already does. Skipped entirely in compound mode, where the
    // recipient is always the locked self-address, never user-typed.
    val resolveContext = LocalContext.current
    LaunchedEffect(recipientInput) {
        knsResolvedAddress = null
        knsResolvedDomain = null
        knsError = null
        nameResolutions = emptyList()
        selectedTld = null
        if (isCompoundMode) {
            isResolvingKns = false
            return@LaunchedEffect
        }
        val trimmed = recipientInput.trim()
        if (trimmed.isEmpty() || trimmed.startsWith("kaspa:", ignoreCase = true) ||
            trimmed.startsWith("kaspatest:", ignoreCase = true) || !com.kachat.app.services.NameServicesClient.looksLikeName(trimmed)
        ) {
            isResolvingKns = false
            return@LaunchedEffect
        }
        isResolvingKns = true
        kotlinx.coroutines.delay(500)
        // Every name service, in priority: the ending typed, else .kachat only (the others wait
        // under Other domains, iOS 5a5122d). The resolved line names which one answered; the
        // others are offered under "Other domains" (iOS 6ac48a7).
        val results = viewModel.resolveEverywhere(trimmed)
        nameResolutions = results
        isResolvingKns = false
        val primary = com.kachat.app.services.NameServicesClient.primary(results, trimmed)
        if (primary?.address != null) {
            selectResolution(primary)
        } else {
            knsError = com.kachat.app.services.NameServicesClient.notFoundMessage(resolveContext, trimmed, results)
        }
    }

    // Automatic selection's preview (coin control off): which coins this amount needs and what
    // they cost, so the fee shown is priced on the real inputs rather than an assumed one -
    // mirrors ColdSendFlow's previewSelection. Debounced; Send waits for it, so it never goes out
    // with an extra worked out for a different amount.
    LaunchedEffect(previewAmountSompi, baseFeeRateSompiPerGram, feeTier, customExtraFeeSompi, manualUtxos, sourceAddress, recipientScriptLen, sourceSignatureScriptSize) {
        feePreview = null
        if (manualUtxos.isNullOrEmpty() && previewAmountSompi != null && previewAmountSompi > 0) {
            isPreviewingFee = true
            kotlinx.coroutines.delay(400)
            feePreview = try {
                viewModel.previewAutomaticSelection(
                    address = sourceAddress,
                    amountSompi = previewAmountSompi,
                    feeRateSompiPerGram = baseFeeRateSompiPerGram,
                    feeMultiplier = feeTier.multiplier,
                    customExtraFeeSompi = customExtraFeeSompi,
                    recipientScriptLen = recipientScriptLen,
                    signatureScriptSize = sourceSignatureScriptSize,
                )
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w("SendFlow", "Fee preview failed for ${sourceAddress.redactedForLog()}", e)
                null
            }
        }
        isPreviewingFee = false
    }

    LaunchedEffect(Unit) {
        if (isCompoundMode) {
            recipientInput = fromAddress
            isEstimatingMax = true
        } else if (paymentRequest != null && activeRequest == null) {
            applyRequest(paymentRequest)
        }
        // The live quote, once (see baseFeeRateSompiPerGram). Falls back to the network minimum
        // on any failure.
        val quotedRate = viewModel.fetchQuotedFeeRateSompiPerGram()
        liveFeeRateSompiPerGram = quotedRate
        if (isCompoundMode) {
            try {
                // Consolidation is bounded by Kaspa's per-transaction mass cap, so pin the send to
                // one transaction's worth of UTXOs (largest-first, ≤MAX_INPUTS_PER_TRANSACTION) and
                // estimate Max over exactly those. This is why Max works even when the address has
                // more UTXOs than a single tx can hold - it consolidates one batch; repeat to reduce.
                val chunk = viewModel.maxConsolidatableChunk(fromAddress, quotedRate, feeTier.multiplier, customExtraFeeSompi, signatureScriptSize = sourceSignatureScriptSize)
                if (chunk != null) {
                    manualUtxos = chunk.second
                    fiatAmountState.setMaxKas(chunk.first / 100_000_000.0, fiatPriceInCurrency)
                    maxFill = Triple(com.kachat.app.util.formatKasAmount(chunk.first / 100_000_000.0), feeTier, customExtraFeeSompi)
                }
            } catch (e: Exception) {
                // Leave the field untouched on failure - same as the Max button itself.
            } finally {
                isEstimatingMax = false
            }
        }
    }

    LaunchedEffect(sendResult) {
        val result = sendResult ?: return@LaunchedEffect
        if (result.isSuccess) {
            result.getOrNull()?.let { txId ->
                sentTransaction = SentTransaction(
                    txId = txId,
                    amountSompi = KaspaUnit.sompiFromUserText(amountInput)?.takeIf { it > 0 },
                    // A compound is a self-send: naming the address it just came from reads as a
                    // mistake, so it just says how much moved. A resolved name shows as the name.
                    recipient = (knsResolvedDomain ?: recipientInput.trim()).takeIf { !isCompoundMode },
                )
            }
        } else {
            sendError = UserFacingError.message(result.exceptionOrNull(), context.getString(R.string.withdrawal_failed))
        }
        viewModel.sends.consume(sendId)
    }

    // Back is Cancel, and Cancel closes the send at any time, as iOS's does - mid-send too.
    BackHandler { onDone() }

    // No confirmation sheet: the swipe-up screen itself shows Sent or Failed (iOS wallet 59593af).

    // The recipient scanner and coin control are sheets over the send, as iOS's
    // SpendingAddressWithdrawView and WithdrawKaspaView present QRScannerView and CoinControlView.
    if (showScanner) {
        QrScannerSheet(
            onScanned = { scanned -> recipientInput = KaspaAddress.fromScanned(scanned) },
            onDismiss = { showScanner = false }
        )
    }

    if (showCoinControl) {
        CoinControlSheet(
            fromAddress = sourceAddress,
            fetchUtxos = { addr -> viewModel.fetchUtxosForCoinControl(addr) },
            initialSelection = manualUtxos,
            onDone = { selection -> manualUtxos = selection },
            onDismiss = { showCoinControl = false }
        )
    }

    val amountSompi = KaspaUnit.sompiFromUserText(amountInput)
    // Only an address of the network the app runs on: the other network's is the same key on
    // another chain, refused with its own reason under the field (iOS ce20e87).
    val isValidAddress = remember(recipientInput) { KaspaAddress.isValidOnActiveNetwork(recipientInput.trim()) }
    val effectiveAddress = knsResolvedAddress ?: recipientInput
    val hasValidRecipient = if (knsResolvedAddress != null) true else (isValidAddress && !isResolvingKns)

    // Max: fee-aware, from the funds this send actually spends. Compound pins one mass-safe
    // transaction's worth of UTXOs (largest-first, <=cap) as the input set - see the compound
    // LaunchedEffect above.
    val setMaxAmount: () -> Unit = {
        coroutineScope.launch {
            isEstimatingMax = true
            try {
                // Room for the base fee and the Fast / Priority / custom extra, the extra derived
                // from Max's own base - never from a fee estimated for whatever amount was typed
                // before, which is how Max used to leave too little room for the extra.
                val maxTier = feeTier
                val maxCustomExtra = customExtraFeeSompi
                if (isCompoundMode) {
                    val chunk = viewModel.maxConsolidatableChunk(fromAddress, liveFeeRateSompiPerGram, maxTier.multiplier, maxCustomExtra, signatureScriptSize = sourceSignatureScriptSize)
                    if (chunk != null) {
                        manualUtxos = chunk.second
                        fiatAmountState.setMaxKas(chunk.first / 100_000_000.0, fiatPriceInCurrency)
                        maxFill = Triple(com.kachat.app.util.formatKasAmount(chunk.first / 100_000_000.0), maxTier, maxCustomExtra)
                    } else {
                        // Same silence as the plain Max had: nothing to consolidate, or nothing
                        // known yet.
                        IosToasts.error("Nothing to consolidate here yet.")
                    }
                } else {
                    // null = the wallet cannot answer yet (the REST client is created a moment
                    // after launch); 0 = it answered, and the fee eats the balance. Neither may
                    // write "0" into the field, which is what made this read as a dead button.
                    when (val maxSompi = viewModel.estimateMaxSendableAmount(sourceAddress, liveFeeRateSompiPerGram, manualUtxos, maxTier.multiplier, maxCustomExtra, signatureScriptSize = sourceSignatureScriptSize)) {
                        null -> IosToasts.error("Still connecting. Try Max again in a moment.")
                        0L -> IosToasts.error("Not enough here to cover the network fee.")
                        else -> {
                            fiatAmountState.setMaxKas(maxSompi / 100_000_000.0, fiatPriceInCurrency)
                            maxFill = Triple(com.kachat.app.util.formatKasAmount(maxSompi / 100_000_000.0), maxTier, maxCustomExtra)
                        }
                    }
                }
            } catch (e: Exception) {
                // Said out loud, not swallowed. A silent catch here is indistinguishable from a
                // button that does nothing, which is exactly how this was reported - and it left
                // no trace in the log to diagnose it from either.
                Log.w("SendFlow", "Max estimate failed for ${sourceAddress.redactedForLog()}", e)
                IosToasts.error(UserFacingError.message(e, "Could not work out the maximum."))
            } finally {
                isEstimatingMax = false
            }
        }
    }
    // The fee changed after Max filled the field (a speed picked, a custom fee set): Max is worked
    // out again for the new fee, or the send would need more than the coins hold. Only while the
    // field still holds what Max wrote - an amount typed since is left alone.
    LaunchedEffect(feeTier, customExtraFeeSompi) {
        val filled = maxFill ?: return@LaunchedEffect
        if (filled.first == amountInput && (filled.second != feeTier || filled.third != customExtraFeeSompi)) {
            setMaxAmount()
        }
    }
    // Not while the fee preview is being worked out: the send passes the preview's extra.
    val canSend = !isSending && hasValidRecipient && (amountSompi ?: 0) > 0 && !isPreviewingFee
    val send: () -> Unit = {
        sendError = null
        amountSompi?.takeIf { canSend }?.let {
            val recipient = effectiveAddress.trim()
            // No rate override: the engine prices the base at the live quote, and the speed or
            // custom fee goes on top as the flat extra shown (SendFeeModel).
            // A chatting-address request is a plain send too: the wallet has no chats to file a
            // chat payment in (iOS wallet 301ebfb).
            if (sourceIndex != null) {
                viewModel.withdrawFromSpendingAddress(sendId, sourceIndex, recipient, it, feeRateOverride = null, manualUtxos = manualUtxos, extraFeeSompi = extraFeeSompi)
            } else {
                viewModel.onSendClicked(sendId, recipient, it, feeRateOverride = null, manualUtxos = manualUtxos, extraFeeSompi = extraFeeSompi)
            }
        }
    }

    // Swipe the bar up to send: the whole screen moves up into the BlockDAG while it goes out,
    // then Sent with the transaction, or Failed with the error and Try Again (iOS wallet 59593af
    // SwipeUpToSendScrollView). Its header (Cancel, the title) moves with the swipe.
    SwipeUpToSendScreen(
        screenTitle = when {
            isCompoundMode -> stringResource(R.string.compound_utxos)
            activeRequest != null -> stringResource(R.string.pr_payment_request)
            else -> title
        },
        onCancel = onDone,
        barTitle = stringResource(if (isCompoundMode) R.string.swipe_up_to_consolidate else R.string.swipe_up_to_send),
        isBusy = isSending,
        isEnabled = canSend,
        amountSompi = amountSompi,
        recipient = if (isCompoundMode) null else (knsResolvedDomain ?: effectiveAddress.trim()).takeIf { it.isNotEmpty() },
        sent = sentTransaction,
        failureMessage = sendError,
        explorerUrl = { kaspaExplorer.txUrl(it) },
        onDone = {
            sentTransaction = null
            onDone()
        },
        onSwipe = send,
        insets = if (presentedAsSheet) WindowInsets(0, 0, 0, 0) else WindowInsets.systemBars,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isCompoundMode) {
                SendFromSection(
                    source = source,
                    onSourceChange = { picked ->
                        if (picked != source) {
                            source = picked
                            // coins picked on one address can't pay from another
                            manualUtxos = null
                        }
                    },
                    availableSompi = sourceBalanceSompi,
                    onAvailableSompiChange = { sourceBalanceSompi = it },
                    walletViewModel = viewModel,
                    enabled = !isSending,
                    isLocked = lockedSource != null,
                )
            }

            SendRecipientCard(
                input = recipientInput,
                onInputChange = { input ->
                    // A payment request pasted in fills in everything it carries (iOS c6851d6),
                    // as iOS's WithdrawKaspaView.
                    val pasted = if (isCompoundMode) null
                    else (KaChatLink.findFirst(input.trim())?.ref as? KaChatLinkRef.PaymentRequest)?.request
                    if (pasted != null) applyRequest(pasted) else recipientInput = input
                },
                lockedAddress = if (isCompoundMode) fromAddress else activeRequest?.address,
                isResolving = isResolvingKns,
                resolvedAddress = knsResolvedAddress,
                resolvedName = knsResolvedDomain,
                nameResolutions = nameResolutions,
                selectedTld = selectedTld,
                onSelectResolution = selectResolution,
                lookupError = knsError,
                isValidAddress = isValidAddress,
                onScan = { showScanner = true },
                enabled = !isSending,
            )

            activeRequest?.let { PaymentRequestSummary(request = it) }

            KaspaAmountEntry(
                fiatAmountState = fiatAmountState,
                priceInCurrency = fiatPriceInCurrency,
                currencyCode = fiatCurrencyCode,
                isEstimatingMax = isEstimatingMax,
                maxEnabled = hasValidRecipient && !isSending,
                onMax = setMaxAmount,
                modifier = Modifier.padding(top = 8.dp),
            )

            // Compound UTXOs has no From (it merges this one address); the balance shows here.
            if (isCompoundMode) {
                SendInfoPill {
                    Text(KaspaUnit.label("Available: ${com.kachat.app.services.AddressActivityNotifier.formatKas(balanceSompi)} KAS"))
                }
            }

            SendFeeControls(
                feeTier = feeTier,
                onFeeTierChange = {
                    feeTier = it
                    customExtraFeeSompi = null
                    isEditingFee = false
                },
                isEditingFee = isEditingFee,
                customFeeText = customFeeText,
                onCustomFeeTextChange = { customFeeText = it },
                isEstimatingFee = isPreviewingFee,
                feeText = "${com.kachat.app.services.AddressActivityNotifier.formatKas(effectiveFeeSompi)} ${KaspaUnit.symbol}",
                onStartEditing = {
                    customFeeText = com.kachat.app.services.AddressActivityNotifier.formatKas(effectiveFeeSompi)
                    isEditingFee = true
                },
                onCommit = {
                    // The typed total fee; below the computed one it clamps up to it rather than
                    // being refused (iOS commitCustomFee).
                    KaspaUnit.sompiFromUserText(customFeeText)?.let { totalSompi ->
                        customExtraFeeSompi = (totalSompi - defaultFeeSompi).coerceAtLeast(0L)
                    }
                    isEditingFee = false
                },
                coinControlSummary = coinControlSummary(manualUtxos),
                onCoinControl = { if (!isSending) showCoinControl = true },
            )

            sendError?.let {
                Text(it, color = LocalAppColors.current.danger, fontSize = 13.sp, textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * On-chain transaction history + UTXOs for one spending address, with direct Send/Receive —
 * reached by tapping an address row in [ManageAddressesScreen]/[ManageAddressesHiddenScreen].
 * Visually mirrors [ColdStorageTxHistoryScreen], but Send goes through [SpendingAddressSendFlow]
 * (direct signing with the address's own already-held private key via
 * [WalletViewModel.withdrawFromSpendingAddress]) rather than Cold Storage's external-QR-signer
 * flow, which only makes sense for a watch-only key with no private key on this device.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendingAddressTxHistoryScreen(
    index: Int,
    onBack: () -> Unit,
    viewModel: WalletViewModel,
    // Created here (not left to SpendingAddressSendFlow's own default) so its live-price fetch -
    // KaspaFiatAmountState.toggleMode silently no-ops until a price has arrived - gets a head
    // start of however long the user spends on this screen before ever tapping Send, instead of
    // starting from zero the instant the send flow itself first composes.
    portfolioViewModel: com.kachat.app.viewmodels.PortfolioViewModel = hiltViewModel(),
    /** Message on a .kachat name's owner, from its detail. */
    /** The row resolved by the History sheet a tapped "Received" notification opens (iOS
     *  13046ad), used only while the shared list hasn't got this index (a failed load). */
    fallbackEntry: com.kachat.app.services.WalletService.SpendingAddressEntry? = null,
    /** Opened as the root of the "Received" History sheet (iOS 13046ad `OwnAddressHistorySheet`):
     *  Done closes it, top left, in place of the back arrow. */
    closesWithDone: Boolean = false,
) {
    val addresses by viewModel.manageAddresses.collectAsState()
    // Looked up from the already-loaded address list (shared with ManageAddressesScreen) rather
    // than threading label/balance through the route - keeps the nav arg down to just the index
    // withdrawFromSpendingAddress actually signs with.
    val entry = remember(addresses, index, fallbackEntry) {
        addresses.firstOrNull { it.index == index } ?: fallbackEntry?.takeIf { it.index == index }
    }
    val address = entry?.address.orEmpty()

    val txHistory by viewModel.spendingAddressTxHistory.collectAsState()
    val isLoadingTxHistory by viewModel.loadingSpendingAddressTxHistory.collectAsState()
    val txHistoryFailed by viewModel.spendingAddressTxHistoryFailed.collectAsState()
    val utxos by viewModel.spendingAddressUtxos.collectAsState()
    val isLoadingUtxos by viewModel.loadingSpendingAddressUtxos.collectAsState()
    val utxosLoadError by viewModel.spendingAddressUtxosError.collectAsState()
    val utxoListAddress: String? = (address as String?)?.takeIf { it.isNotEmpty() }
    val kaspaExplorer by viewModel.kaspaExplorer.collectAsState()
    val biometricSpendingKeyEnabled by viewModel.biometricSpendingKeyEnabled.collectAsState()
    val uriHandler = LocalUriHandler.current
    // The tapped transaction, while its action chooser is up.
    var transactionActionTarget by remember { mutableStateOf<ColdStorageAddressDiscovery.AddressTransaction?>(null) }
    // The transaction being filed into a portfolio, if any - see [AddToPortfolioSheet].
    var portfolioCandidate by remember { mutableStateOf<ColdStorageAddressDiscovery.AddressTransaction?>(null) }
    var addedPortfolioName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    var showQr by remember { mutableStateOf(false) }
    var showWithdraw by remember { mutableStateOf(false) }
    var showCompoundFlow by remember { mutableStateOf(false) }
    var showPrivateKey by remember { mutableStateOf(false) }
    var utxoLabels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var labelingUtxoKey by remember { mutableStateOf<String?>(null) }
    var labelInput by remember { mutableStateOf("") }
    // The .kachat name opened from the Domains tab's .kachat screen (iOS 881ada6).
    var openedKachat by remember { mutableStateOf<com.kachat.app.services.kachatnames.NameInfo?>(null) }
    // The name service opened from the Domains tab (iOS fe52376).
    var openedDomainService by remember { mutableStateOf<com.kachat.app.services.NameServiceTLD?>(null) }

    LaunchedEffect(address) {
        if (address.isNotEmpty()) {
            viewModel.loadSpendingAddressTxHistory(address)
            viewModel.loadSpendingAddressUtxos(address)
            utxoLabels = viewModel.getSpendingUtxoLabels(address)
        }
    }

    // In-place full-screen swap - not a nav route, not an overlay dialog - mirroring
    // ColdStorageTxHistoryScreen's own `if (showSendFlow) { ...; return }` idiom exactly, so Send
    // takes over the whole screen the same way on both the spending and Cold Storage paths.
    // A .kachat name opened from this address's .kachat tab: its live detail, which knows which of
    // your addresses holds it (iOS 881ada6).
    openedKachat?.let { info ->
        AboveDock { KachatLiveNameDetailScreen(info, onBack = { openedKachat = null }) }
        return
    }
    // A name service opened from this address's Domains tab: its names (iOS fe52376).
    openedDomainService?.let { tld ->
        AboveDock {
            AddressDomainServiceScreen(
                address = address,
                tld = tld,
                onBack = { openedDomainService = null },
                onOpenKachat = { openedKachat = it },
            )
        }
        return
    }

    // Send and Compound are sheets over this screen that only Cancel closes, as iOS presents
    // them (.interactiveDismissDisabled()). Send is the Send Kaspa every send is (iOS 6333dd3):
    // From starts at the Chatting Address, and this address is under Choose an Address.
    val sendChattingAddress by viewModel.address.collectAsState()
    val sendChattingBalance by viewModel.balanceSompi.collectAsState()
    val sendFrom = sendChattingAddress
    if (showWithdraw && entry != null && sendFrom != null) {
        IosFullSheet(
            onDismissed = {
                showWithdraw = false
                viewModel.loadSpendingAddressTxHistory(address)
                viewModel.loadSpendingAddressUtxos(address)
            },
            swipeToDismiss = false,
        ) { close ->
            SpendingAddressSendFlow(
                fromAddress = sendFrom,
                balanceSompi = sendChattingBalance,
                title = "Send Kaspa",
                viewModel = viewModel,
                portfolioViewModel = portfolioViewModel,
                onDone = close,
                presentedAsSheet = true,
                // Send from this address's own page: it is the From, with no other to pick.
                lockedSource = SendSource.Spending(entry.index, entry.address, spendingAddressDisplayLabel(entry.index, entry.label)),
            )
        }
    }

    if (showCompoundFlow && entry != null) {
        IosFullSheet(
            onDismissed = {
                showCompoundFlow = false
                viewModel.loadSpendingAddressTxHistory(address)
                viewModel.loadSpendingAddressUtxos(address)
            },
            swipeToDismiss = false,
        ) { close ->
            SpendingAddressSendFlow(
                fromAddress = entry.address,
                balanceSompi = entry.balanceSompi,
                title = "Send from ${entry.displayTitle}",
                spendingIndex = entry.index,
                viewModel = viewModel,
                portfolioViewModel = portfolioViewModel,
                isCompoundMode = true,
                onDone = close,
                presentedAsSheet = true,
            )
        }
    }

    val displayName = entry?.label?.takeIf { it.isNotBlank() } ?: com.kachat.app.services.WalletManager.spendingAddressTitle(index)

    Scaffold(
        containerColor = LocalAppColors.current.background,
        // Runs on under Receive / Send and the dock, as iOS's list runs on under its
        // safeAreaInset bar and tab bar.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(displayName, color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // At the root of the "Received" History sheet: Done, as iOS's
                    // OwnAddressHistorySheet puts it (13046ad); a back arrow everywhere else.
                    if (closesWithDone) {
                        SheetBarButton(stringResource(R.string.done), onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                    } else {
                        SheetBarBackButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                    }
                },
                actions = {
                    // iOS 26 puts neighbouring trailing items on one piece of glass.
                    Row(
                        modifier = Modifier.padding(end = 12.dp).heightIn(min = 44.dp).barGlass(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ToolbarIconButton(Icons.Default.IosShare, stringResource(R.string.export), onClick = {
                            if (biometricSpendingKeyEnabled) {
                                context.authenticateWithDeviceCredential(
                                    title = "Unlock to View Private Key",
                                    onSuccess = { showPrivateKey = true }
                                )
                            } else {
                                showPrivateKey = true
                            }
                        })
                        ToolbarIconButton(Icons.Default.Public, stringResource(R.string.view_in_explorer), onClick = { uriHandler.openUri(kaspaExplorer.addressUrl(address)) })
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        },
        bottomBar = {
            AddressReceiveSendBar(
                receiveEnabled = address.isNotEmpty(),
                sendEnabled = (entry?.balanceSompi ?: 0L) > 0L,
                onReceive = { showQr = true },
                onSend = { showWithdraw = true },
            )
        }
    ) { padding ->
        // What Receive / Send and the dock cover: the lists scroll clear of it.
        val barInset = padding.calculateBottomPadding()
        Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            AddressPageBalanceHeader(entry?.balanceSompi ?: 0L)
            // The app's tab look (Chats / Group Chats / Public Chats), not a segmented control;
            // plain names - History, UTXOs, Domains (iOS b96d727/718b88c, fe52376).
            UnderlineTabBar(
                titles = listOf(stringResource(R.string.address_tab_history), stringResource(R.string.utxos), stringResource(R.string.address_tab_domains)),
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it },
                modifier = Modifier.padding(top = 4.dp),
            )
            when (selectedTab) {
                0 -> AddressHistoryList(
                    txHistory = txHistory,
                    isLoading = isLoadingTxHistory,
                    failed = txHistoryFailed,
                    onRetry = { viewModel.loadSpendingAddressTxHistory(address) },
                    onTransactionClick = { transactionActionTarget = it },
                    bottomPadding = barInset,
                )
                2 -> AddressDomainsTab(address, onOpenService = { openedDomainService = it }, bottomPadding = barInset)
                else -> AddressUtxoList(
                    utxos = utxos,
                    isLoading = isLoadingUtxos,
                    loadError = utxosLoadError,
                    labels = utxoLabels,
                    onRetry = { utxoListAddress?.let { viewModel.loadSpendingAddressUtxos(it) } },
                    onCompound = { showCompoundFlow = true },
                    onRename = { key ->
                        labelingUtxoKey = key
                        labelInput = utxoLabels[key] ?: ""
                    },
                    bottomPadding = barInset,
                )
            }
        }
    }

    if (showQr && address.isNotEmpty()) {
        QrCodeOverlay(value = address, onDismiss = { showQr = false })
    }

    if (showPrivateKey) {
        SpendingAddressPrivateKeyOverlay(privateKeyHex = viewModel.getSpendingPrivateKeyHex(index), onDismiss = { showPrivateKey = false })
    }

    labelingUtxoKey?.let { key ->
        com.kachat.app.ui.theme.IosAlertDialog(
            onDismissRequest = { labelingUtxoKey = null },
            containerColor = LocalAppColors.current.surface,
            title = { Text(stringResource(R.string.rename_utxo), color = LocalAppColors.current.textPrimary) },
            text = {
                com.kachat.app.ui.theme.IosTextField(
                    value = labelInput,
                    onValueChange = { labelInput = it },
                    label = { Text(stringResource(R.string.name)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LocalAppColors.current.textPrimary,
                        unfocusedTextColor = LocalAppColors.current.textPrimary,
                        focusedBorderColor = KaspaTeal,
                        unfocusedBorderColor = LocalAppColors.current.textSecondary,
                        focusedLabelColor = KaspaTeal,
                        unfocusedLabelColor = LocalAppColors.current.textSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setSpendingUtxoLabel(address, key, labelInput)
                    utxoLabels = viewModel.getSpendingUtxoLabels(address)
                    labelingUtxoKey = null
                }) {
                    Text(stringResource(R.string.save), color = KaspaTeal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { labelingUtxoKey = null }) {
                    Text(stringResource(R.string.cancel), color = LocalAppColors.current.textSecondary)
                }
            }
        )
    }

    transactionActionTarget?.let { tapped ->
        TransactionActionsSheet(
            tx = tapped,
            onOpenExplorer = {
                transactionActionTarget = null
                uriHandler.openUri(kaspaExplorer.txUrl(tapped.txId))
            },
            onAddToPortfolio = {
                transactionActionTarget = null
                portfolioCandidate = tapped
            },
            onDismiss = { transactionActionTarget = null },
        )
    }

    portfolioCandidate?.let { candidate ->
        AddToPortfolioSheet(
            tx = candidate,
            address = address,
            viewModel = portfolioViewModel,
            onDismiss = { portfolioCandidate = null },
            onAdded = { addedPortfolioName = it },
        )
    }
    addedPortfolioName?.let { name ->
        PortfolioAddedSnackbar(name) { addedPortfolioName = null }
    }
}

/**
 * On-chain transaction history + UTXOs for the wallet's identity/chatting address, with direct
 * Send/Receive/Export - reached from the Profile screen's "Chatting Address" section's "Manage
 * Address" row. Field-for-field the same screen as [SpendingAddressTxHistoryScreen] (balance
 * header, Transaction History/UTXOs tabs, Compound UTXOs, Export, Explorer, Receive/Send), just
 * for the single fixed identity address instead of one spending-chain index - reuses the same
 * address-keyed StateFlows/functions on [WalletViewModel] (spendingAddressTxHistory/Utxos,
 * getSpendingUtxoLabels, etc. - all keyed by address string, nothing spending-chain-specific
 * about them) rather than duplicating a second copy of this state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityAddressDetailScreen(
    onBack: () -> Unit,
    viewModel: WalletViewModel,
    portfolioViewModel: com.kachat.app.viewmodels.PortfolioViewModel = hiltViewModel(),
    /** Message on a .kachat name's owner, from its detail. */
    /** Opened as the root of the "Received" History sheet (iOS 13046ad `OwnAddressHistorySheet`):
     *  Done closes it, top left, in place of the back arrow. */
    closesWithDone: Boolean = false,
) {
    val address by viewModel.address.collectAsState()
    val balanceSompi by viewModel.balanceSompi.collectAsState()

    val txHistory by viewModel.spendingAddressTxHistory.collectAsState()
    val isLoadingTxHistory by viewModel.loadingSpendingAddressTxHistory.collectAsState()
    val txHistoryFailed by viewModel.spendingAddressTxHistoryFailed.collectAsState()
    val utxos by viewModel.spendingAddressUtxos.collectAsState()
    val isLoadingUtxos by viewModel.loadingSpendingAddressUtxos.collectAsState()
    val utxosLoadError by viewModel.spendingAddressUtxosError.collectAsState()
    val utxoListAddress: String? = (address as String?)?.takeIf { it.isNotEmpty() }
    val kaspaExplorer by viewModel.kaspaExplorer.collectAsState()
    // The identity key is at least as sensitive as the seed phrase itself (it IS the wallet's
    // main spending key) - gated behind the same biometric flag Settings > View Seed Phrase
    // uses, not the lower-stakes biometricSpendingKeyEnabled a derived spending address uses.
    val biometricSeedPhraseEnabled by viewModel.biometricSeedPhraseEnabled.collectAsState()
    val uriHandler = LocalUriHandler.current
    // The tapped transaction, while its action chooser is up.
    var transactionActionTarget by remember { mutableStateOf<ColdStorageAddressDiscovery.AddressTransaction?>(null) }
    // The transaction being filed into a portfolio, if any - see [AddToPortfolioSheet].
    var portfolioCandidate by remember { mutableStateOf<ColdStorageAddressDiscovery.AddressTransaction?>(null) }
    var addedPortfolioName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    var showQr by remember { mutableStateOf(false) }
    var showWithdraw by remember { mutableStateOf(false) }
    var showCompoundFlow by remember { mutableStateOf(false) }
    var showPrivateKey by remember { mutableStateOf(false) }
    // The "Address Actions" sheet, and the public-key screen it can open.
    var showAddressActions by remember { mutableStateOf(false) }
    var showPublicKey by remember { mutableStateOf(false) }
    var utxoLabels by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var labelingUtxoKey by remember { mutableStateOf<String?>(null) }
    var labelInput by remember { mutableStateOf("") }
    // The .kachat name opened from the Domains tab's .kachat screen (iOS 881ada6).
    var openedKachat by remember { mutableStateOf<com.kachat.app.services.kachatnames.NameInfo?>(null) }
    // The name service opened from the Domains tab (iOS fe52376).
    var openedDomainService by remember { mutableStateOf<com.kachat.app.services.NameServiceTLD?>(null) }

    LaunchedEffect(address) {
        val addr = address
        if (!addr.isNullOrEmpty()) {
            viewModel.loadSpendingAddressTxHistory(addr)
            viewModel.loadSpendingAddressUtxos(addr)
            utxoLabels = viewModel.getSpendingUtxoLabels(addr)
        }
    }

    // A .kachat name opened from this address's .kachat tab: its live detail, which knows which of
    // your addresses holds it (iOS 881ada6).
    openedKachat?.let { info ->
        AboveDock { KachatLiveNameDetailScreen(info, onBack = { openedKachat = null }) }
        return
    }
    // A name service opened from this address's Domains tab: its names (iOS fe52376).
    openedDomainService?.let { tld ->
        AboveDock {
            AddressDomainServiceScreen(
                address = address.orEmpty(),
                tld = tld,
                onBack = { openedDomainService = null },
                onOpenKachat = { openedKachat = it },
            )
        }
        return
    }

    val flowAddress = address
    // Send and Compound are sheets over this screen that only Cancel closes, as iOS presents
    // WithdrawKaspaView (.interactiveDismissDisabled()).
    if (showWithdraw && flowAddress != null) {
        IosFullSheet(
            onDismissed = {
                showWithdraw = false
                viewModel.loadSpendingAddressTxHistory(flowAddress)
                viewModel.loadSpendingAddressUtxos(flowAddress)
            },
            swipeToDismiss = false,
        ) { close ->
            SpendingAddressSendFlow(
                fromAddress = flowAddress,
                balanceSompi = balanceSompi,
                title = "Send Kaspa",
                viewModel = viewModel,
                portfolioViewModel = portfolioViewModel,
                onDone = close,
                presentedAsSheet = true,
                lockedSource = SendSource.Chatting,
            )
        }
    }

    if (showCompoundFlow && flowAddress != null) {
        IosFullSheet(
            onDismissed = {
                showCompoundFlow = false
                viewModel.loadSpendingAddressTxHistory(flowAddress)
                viewModel.loadSpendingAddressUtxos(flowAddress)
            },
            swipeToDismiss = false,
        ) { close ->
            SpendingAddressSendFlow(
                fromAddress = flowAddress,
                balanceSompi = balanceSompi,
                title = "Send Kaspa",
                viewModel = viewModel,
                portfolioViewModel = portfolioViewModel,
                isCompoundMode = true,
                onDone = close,
                presentedAsSheet = true,
            )
        }
    }

    Scaffold(
        containerColor = LocalAppColors.current.background,
        // Runs on under Receive / Send and the dock, as iOS's list runs on under its
        // safeAreaInset bar and tab bar.
        contentWindowInsets = WindowInsets(0),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chatting Address", color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // At the root of the "Received" History sheet: Done, as iOS's
                    // OwnAddressHistorySheet puts it (13046ad); a back arrow everywhere else.
                    if (closesWithDone) {
                        SheetBarButton(stringResource(R.string.done), onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                    } else {
                        SheetBarBackButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp))
                    }
                },
                // Export and Explorer used to be two unlabelled glyphs here. A pair of icons has
                // room for no words at all, so neither said what it did, and there was nowhere to
                // put a third thing - they live in the Address Actions sheet below now.
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        },
        bottomBar = {
            AddressReceiveSendBar(
                receiveEnabled = !address.isNullOrEmpty(),
                sendEnabled = balanceSompi > 0L,
                onReceive = { showQr = true },
                onSend = { showWithdraw = true },
            )
        }
    ) { padding ->
        // What Receive / Send and the dock cover: the lists scroll clear of it.
        val barInset = padding.calculateBottomPadding()
        Column(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            AddressPageBalanceHeader(balanceSompi)
            Button(
                onClick = { showAddressActions = true },
                enabled = !address.isNullOrEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = KaspaTeal, contentColor = Color.Black),
                shape = CircleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.address_actions), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            // Right above what it switches, the way Chats has it: History, UTXOs, Domains
            // (iOS b96d727/718b88c).
            UnderlineTabBar(
                titles = listOf(stringResource(R.string.address_tab_history), stringResource(R.string.utxos), stringResource(R.string.address_tab_domains)),
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it },
            )
            when (selectedTab) {
                0 -> AddressHistoryList(
                    txHistory = txHistory,
                    isLoading = isLoadingTxHistory,
                    failed = txHistoryFailed,
                    onRetry = { address?.let { viewModel.loadSpendingAddressTxHistory(it) } },
                    onTransactionClick = { transactionActionTarget = it },
                    bottomPadding = barInset,
                )
                2 -> AddressDomainsTab(address.orEmpty(), onOpenService = { openedDomainService = it }, bottomPadding = barInset)
                else -> AddressUtxoList(
                    utxos = utxos,
                    isLoading = isLoadingUtxos,
                    loadError = utxosLoadError,
                    labels = utxoLabels,
                    onRetry = { utxoListAddress?.let { viewModel.loadSpendingAddressUtxos(it) } },
                    onCompound = { showCompoundFlow = true },
                    onRename = { key ->
                        labelingUtxoKey = key
                        labelInput = utxoLabels[key] ?: ""
                    },
                    bottomPadding = barInset,
                )
            }
        }
    }

    val qrAddress = address
    if (showQr && !qrAddress.isNullOrEmpty()) {
        // iOS ChattingAddressManageView shows ChattingAddressQRView with its default caption -
        // the chatting caption Profile uses (since 492aa53: "for chatting and domains only").
        // In a sheet with Close leading, as iOS's ChattingAddressManageView presents it.
        QrPageSheet(onDismiss = { showQr = false }, white = true) {
            ChattingAddressQrPage(
                address = qrAddress,
                balanceSompi = balanceSompi,
                dismiss = QrPageDismiss.CLOSE,
                onDismiss = { showQr = false },
            )
        }
    }

    if (showAddressActions) {
        IdentityAddressActionsSheet(
            onPrivateKey = {
                showAddressActions = false
                if (biometricSeedPhraseEnabled) {
                    context.authenticateWithDeviceCredential(
                        title = "Unlock to View Private Key",
                        onSuccess = { showPrivateKey = true }
                    )
                } else {
                    showPrivateKey = true
                }
            },
            onPublicKey = {
                showAddressActions = false
                showPublicKey = true
            },
            onExplorer = {
                showAddressActions = false
                address?.let { uriHandler.openUri(kaspaExplorer.addressUrl(it)) }
            },
            onDismiss = { showAddressActions = false },
        )
    }

    if (showPublicKey) {
        IdentityAddressPublicKeyOverlay(address = address.orEmpty(), onDismiss = { showPublicKey = false })
    }

    if (showPrivateKey) {
        SpendingAddressPrivateKeyOverlay(privateKeyHex = viewModel.getPrivateKeyHex(), onDismiss = { showPrivateKey = false })
    }

    labelingUtxoKey?.let { key ->
        com.kachat.app.ui.theme.IosAlertDialog(
            onDismissRequest = { labelingUtxoKey = null },
            containerColor = LocalAppColors.current.surface,
            title = { Text(stringResource(R.string.rename_utxo), color = LocalAppColors.current.textPrimary) },
            text = {
                com.kachat.app.ui.theme.IosTextField(
                    value = labelInput,
                    onValueChange = { labelInput = it },
                    label = { Text(stringResource(R.string.name)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LocalAppColors.current.textPrimary,
                        unfocusedTextColor = LocalAppColors.current.textPrimary,
                        focusedBorderColor = KaspaTeal,
                        unfocusedBorderColor = LocalAppColors.current.textSecondary,
                        focusedLabelColor = KaspaTeal,
                        unfocusedLabelColor = LocalAppColors.current.textSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val addr = address ?: return@TextButton
                    viewModel.setSpendingUtxoLabel(addr, key, labelInput)
                    utxoLabels = viewModel.getSpendingUtxoLabels(addr)
                    labelingUtxoKey = null
                }) {
                    Text(stringResource(R.string.save), color = KaspaTeal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { labelingUtxoKey = null }) {
                    Text(stringResource(R.string.cancel), color = LocalAppColors.current.textSecondary)
                }
            }
        )
    }

    transactionActionTarget?.let { tapped ->
        TransactionActionsSheet(
            tx = tapped,
            onOpenExplorer = {
                transactionActionTarget = null
                uriHandler.openUri(kaspaExplorer.txUrl(tapped.txId))
            },
            onAddToPortfolio = {
                transactionActionTarget = null
                portfolioCandidate = tapped
            },
            onDismiss = { transactionActionTarget = null },
        )
    }

    portfolioCandidate?.let { candidate ->
        AddToPortfolioSheet(
            tx = candidate,
            address = address.orEmpty(),
            viewModel = portfolioViewModel,
            onDismiss = { portfolioCandidate = null },
            onAdded = { addedPortfolioName = it },
        )
    }
    addedPortfolioName?.let { name ->
        PortfolioAddedSnackbar(name) { addedPortfolioName = null }
    }
}

/**
 * The half sheet behind "Address Actions" on the Chatting Address screen. Same three-row shape as
 * Manage Addresses' and Cold Storage's sheets of the same name, so all three read as one object.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdentityAddressActionsSheet(
    onPrivateKey: () -> Unit,
    onPublicKey: () -> Unit,
    onExplorer: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalAppColors.current
    com.kachat.app.ui.theme.IosSheetColors {
        val colors = LocalAppColors.current
        ModalBottomSheet(
            scrimColor = com.kachat.app.ui.theme.IosSheetScrim,
            shape = com.kachat.app.ui.theme.IosSheetShape,
            tonalElevation = com.kachat.app.ui.theme.IosSheetTonalElevation,
            windowInsets = androidx.compose.foundation.layout.WindowInsets.statusBars,
            onDismissRequest = onDismiss,
            // Expanded, not half-height: a partially-expanded sheet cuts the last row off.
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = colors.background,
        ) {
            // The sheet runs down behind the navigation bar, as iOS's does behind the home indicator;
            // its content stays above it.
            Column(Modifier.navigationBarsPadding()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(R.string.address_actions),
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )
                    ActionSheetRow(
                        icon = Icons.Default.Key,
                        title = "View Private Key",
                        subtitle = "The key that spends this address. Never share it.",
                        onClick = onPrivateKey,
                    )
                    ActionSheetRow(
                        icon = Icons.Default.Numbers,
                        title = "View Public Key",
                        subtitle = "The public half of this address, for anyone who asks for it.",
                        onClick = onPublicKey,
                    )
                    ActionSheetRow(
                        icon = Icons.Default.Public,
                        title = stringResource(R.string.view_in_explorer),
                        subtitle = "Opens this address on your chosen block explorer.",
                        onClick = onExplorer,
                    )
                }
            }
        }
    }
}

/**
 * The chatting address's public key, from the Address Actions sheet.
 *
 * Nothing is derived or unlocked to show this: a Kaspa P2PK address IS its public key in bech32,
 * so this decodes the address the screen already has. No FLAG_SECURE, no reveal gate and no
 * clipboard expiry either - unlike the private key alongside it, this is a value you hand out on
 * purpose. Mirrors iOS's `ChattingAddressPublicKeyView`.
 */
@Composable
private fun IdentityAddressPublicKeyOverlay(address: String, onDismiss: () -> Unit) {
    val keyCopyHaptic = com.kachat.app.util.rememberHaptics()
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    // null only for an address shape that carries no key (script-hash), which the wallet's own
    // chatting address never is.
    val publicKeyHex = remember(address) {
        runCatching {
            com.kachat.app.util.KaspaAddress.decode(address).second.joinToString("") { "%02x".format(it) }
        }.getOrNull()
    }

    BackHandler(onBack = onDismiss)

    Box(modifier = Modifier.fillMaxSize().background(colors.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SheetBarBackButton(onClick = onDismiss)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Public Key",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(KaspaTeal.copy(alpha = 0.1f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, null, tint = KaspaTeal, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Safe to share", color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Text(
                    "This is the public half of your chatting address. It identifies you and cannot spend anything.",
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                )
            }

            Spacer(Modifier.height(24.dp))

            if (publicKeyHex != null) {
                Text(
                    publicKeyHex,
                    color = colors.textPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surface)
                        .padding(16.dp),
                )
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = {
                    clipboard.setText(AnnotatedString(publicKeyHex))
                    keyCopyHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                    IosToasts.show("Public key copied")
                }) {
                    Icon(Icons.Default.ContentCopy, null, tint = KaspaTeal, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Copy Public Key", color = KaspaTeal, fontWeight = FontWeight.Bold)
                }
            } else {
                Text(
                    "This address does not carry a public key.",
                    color = colors.textSecondary,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

/**
 * Reveals a single spending address's own derived private key - not the wallet's seed phrase -
 * so a specific address's spending capability can be exported/backed up without exposing the
 * rest of the wallet. Mirrors SeedPhraseScreen's reveal flow at the same sensitivity level
 * (FLAG_SECURE screenshot/recording block, tap-to-reveal with a 7s auto-hide timer) rather than
 * inventing a lighter-weight pattern for equally sensitive key material. The caller already
 * gates presentation behind biometrics, same as View Seed Phrase's own entry point.
 */
@Composable
private fun SpendingAddressPrivateKeyOverlay(privateKeyHex: String, onDismiss: () -> Unit) {
    val keyCopyHaptic = com.kachat.app.util.rememberHaptics()
    var revealed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val window = (LocalContext.current as? Activity)?.window
    DisposableEffect(window) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    LaunchedEffect(revealed) {
        if (revealed) {
            delay(7000)
            revealed = false
        }
    }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                SheetBarButton(stringResource(R.string.done), onClick = onDismiss)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2C1E1E))
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Warning, null, tint = LocalAppColors.current.warning, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        stringResource(R.string.security_warning),
                        color = LocalAppColors.current.warning,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Anyone with this address's private key can spend its funds. Never share it with anyone.",
                        color = Color(0xFF948B8B),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            if (!revealed) {
                Column(
                    modifier = Modifier
                        .clickable { revealed = true }
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.VisibilityOff,
                        null,
                        tint = LocalAppColors.current.textSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Tap to reveal private key",
                        color = LocalAppColors.current.textSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Text(
                    privateKeyHex,
                    color = LocalAppColors.current.textPrimary,
                    fontFamily = FontFamily.Monospace,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LocalAppColors.current.surface)
                        .padding(16.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            if (revealed) {
                TextButton(onClick = {
                    copyPrivateKeyWithAutoWipe(context, privateKeyHex)
                    keyCopyHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                    IosToasts.show("Private key copied. Clipboard will clear in 30s.")
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContentCopy, null, tint = KaspaTeal, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.copy_private_key_hex), color = KaspaTeal)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SpendingAddressTxHistoryRow(
    tx: ColdStorageAddressDiscovery.AddressTransaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    // iOS `direction(for:)` is nil - "Transaction", grey, no amount - when nothing left for
    // someone else (sent) or arrived here (received): a self-send or consolidation.
    val directionKnown = tx.amountSompi > 0
    val directionColor = when {
        !directionKnown -> colors.textSecondary
        tx.sent -> colors.danger
        else -> colors.success
    }
    // iOS `.formatted(date: .abbreviated, time: .shortened)`: "Sep 24, 2026 at 2:25 PM".
    val dateStr = tx.blockTimeMillis?.let {
        java.text.SimpleDateFormat("MMM d, yyyy 'at' h:mm a", java.util.Locale.US).format(java.util.Date(it))
    }

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // "arrow.up.circle.fill" / "arrow.down.circle.fill" at title2: a 22 disc, the arrow cut
        // out of it in the card's colour.
        FilledCircleArrow(up = directionKnown && tx.sent, color = directionColor, cutout = Color(0xFF1C1C1E), size = 22.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                when {
                    !directionKnown -> "Transaction"
                    tx.sent -> "Sent"
                    else -> "Received"
                },
                color = directionColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            MiddleEllipsisText(
                tx.txId,
                color = colors.textSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
            )
            dateStr?.let {
                Text(it, color = colors.textSecondary, fontSize = 11.sp, maxLines = 1)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (directionKnown) {
                Text(
                    "${if (tx.sent) "-" else "+"}%.8f ${KaspaUnit.symbol}".format(java.util.Locale.US, tx.amountSompi / 100_000_000.0),
                    color = directionColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
            // What this transaction paid the network (iOS 9839906).
            transactionFeeText(tx.feeSompi)?.let {
                Text(it, color = colors.textSecondary, fontSize = 11.sp, maxLines = 1)
            }
            // iOS "arrow.up.right.square" at caption, accent.
            Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = KaspaTeal, modifier = Modifier.size(13.dp))
        }
    }
}

@Composable
private fun SpendingAddressUtxoRow(
    utxo: ColdStorageAddressDiscovery.AddressUtxo,
    label: String? = null,
    onRenameClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    // iOS `utxoRow`: the coin glyph at title2, the label / amount / outpoint, Coinbase, the pencil.
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (utxo.isCoinbase) {
            // "cube.fill"
            Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ViewInAr, null, tint = KaspaTeal, modifier = Modifier.requiredSize(28.dp))
            }
        } else {
            // "circle.grid.2x2.fill": four dots in a square.
            androidx.compose.foundation.Canvas(Modifier.size(26.dp)) {
                val r = size.minDimension * 0.2f
                for (cx in listOf(0.27f, 0.73f)) for (cy in listOf(0.27f, 0.73f)) {
                    drawCircle(KaspaTeal, radius = r, center = Offset(size.width * cx, size.height * cy))
                }
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (!label.isNullOrBlank()) {
                Text(
                    label,
                    color = KaspaTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            Text(
                "%.8f ${KaspaUnit.symbol}".format(java.util.Locale.US, utxo.amountSompi / 100_000_000.0),
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            MiddleEllipsisText(
                "${utxo.transactionId}:${utxo.index}",
                color = colors.textSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
            )
        }
        if (utxo.isCoinbase) {
            Text(
                stringResource(R.string.coinbase),
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Box(
            modifier = Modifier.size(32.dp).clickable(onClick = onRenameClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.Edit, stringResource(R.string.rename), tint = KaspaTeal, modifier = Modifier.size(21.dp))
        }
    }
}

/** iOS 26 `.insetGrouped` corner radius and the gap above a list's first section. */
private val InsetGroupedRadius = 26.dp
private val InsetGroupedTopGap = 35.dp

/**
 * One row of an iOS 26 `.insetGrouped` section: the section is a single rounded card 16 in from
 * each edge, so only the first row rounds its top corners and the last its bottom ones, and every
 * row after the first draws the inset hairline separator above itself (from [separatorStart] to
 * 16 short of the trailing edge).
 */
@Composable
private fun Modifier.insetGroupedCell(index: Int, count: Int, separatorStart: Dp = 16.dp): Modifier {
    val colors = LocalAppColors.current
    val first = index == 0
    val last = index == count - 1
    val shape = RoundedCornerShape(
        topStart = if (first) InsetGroupedRadius else 0.dp,
        topEnd = if (first) InsetGroupedRadius else 0.dp,
        bottomStart = if (last) InsetGroupedRadius else 0.dp,
        bottomEnd = if (last) InsetGroupedRadius else 0.dp,
    )
    val divider = colors.divider
    return this
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clip(shape)
        .background(colors.surface)
        .then(
            if (first) Modifier
            else Modifier.drawBehind {
                val stroke = 0.5.dp.toPx().coerceAtLeast(1f)
                drawLine(
                    color = divider,
                    start = Offset(separatorStart.toPx(), stroke / 2f),
                    end = Offset(size.width - 16.dp.toPx(), stroke / 2f),
                    strokeWidth = stroke,
                )
            }
        )
}

/** A one-row `.insetGrouped` list - the loading, empty and failed states iOS puts in the list. */
@Composable
private fun InsetGroupedSingleRowList(content: @Composable () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = InsetGroupedTopGap, bottom = 16.dp),
    ) {
        item {
            Box(
                modifier = Modifier
                    .insetGroupedCell(0, 1)
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            ) {
                content()
            }
        }
    }
}

/** "Balance" over the amount at the top of an address's page (iOS: caption, then title3 semibold). */
@Composable
private fun AddressPageBalanceHeader(balanceSompi: Long) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            stringResource(R.string.balance),
            color = LocalAppColors.current.textSecondary,
            fontSize = 12.sp
        )
        Text(
            "%.8f ${KaspaUnit.symbol}".format(java.util.Locale.US, balanceSompi / 100_000_000.0),
            color = LocalAppColors.current.textPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )
    }
}

/**
 * An address page's History tab - iOS `transactionsList`: one `.insetGrouped` card of
 * transactions, or the spinner, "Could not load transactions." with Try Again, or "No
 * transactions yet." as the list's only row.
 */
@Composable
private fun AddressHistoryList(
    txHistory: List<ColdStorageAddressDiscovery.AddressTransaction>,
    isLoading: Boolean,
    failed: Boolean,
    onRetry: () -> Unit,
    onTransactionClick: (ColdStorageAddressDiscovery.AddressTransaction) -> Unit,
    /** Extra room at the list's end for what floats over the page's foot (Receive / Send, the dock). */
    bottomPadding: Dp = 0.dp,
) {
    val colors = LocalAppColors.current
    when {
        isLoading && txHistory.isEmpty() -> InsetGroupedSingleRowList {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal)
            }
        }
        // A failed fetch is not an empty history. Saying "No transactions yet." here was a
        // confident answer about someone's money the app had not got.
        txHistory.isEmpty() && failed -> InsetGroupedSingleRowList {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Could not load transactions.", color = colors.textSecondary, fontSize = 17.sp)
                Text(
                    stringResource(R.string.try_again),
                    color = KaspaTeal,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onRetry),
                )
            }
        }
        txHistory.isEmpty() -> InsetGroupedSingleRowList {
            Text(stringResource(R.string.no_transactions_yet), color = colors.textSecondary, fontSize = 17.sp)
        }
        else -> LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = InsetGroupedTopGap, bottom = 16.dp + bottomPadding),
        ) {
            itemsIndexed(txHistory, key = { _, tx -> tx.txId }) { i, tx ->
                SpendingAddressTxHistoryRow(
                    tx = tx,
                    // Tapping a transaction asks what to do with it. It used to open the
                    // explorer outright, which left "Add to Portfolio" on a button most people
                    // never looked for.
                    onClick = { onTransactionClick(tx) },
                    // The separator starts under the text, past the 26 icon and its 12 gap.
                    modifier = Modifier.insetGroupedCell(i, txHistory.size, separatorStart = 54.dp),
                )
            }
        }
    }
}

/**
 * An address page's UTXOs tab - iOS `utxosList`: Compound UTXOs as its own section with its
 * footer when there is more than one coin, then one `.insetGrouped` card of coins, or the
 * spinner, the load-failure row or "No UTXOs." as that section's only row.
 */
@Composable
private fun AddressUtxoList(
    utxos: List<ColdStorageAddressDiscovery.AddressUtxo>,
    isLoading: Boolean,
    loadError: String?,
    labels: Map<String, String>,
    onRetry: () -> Unit,
    onCompound: () -> Unit,
    onRename: (String) -> Unit,
    /** Extra room at the list's end for what floats over the page's foot (Receive / Send, the dock). */
    bottomPadding: Dp = 0.dp,
) {
    val colors = LocalAppColors.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = InsetGroupedTopGap, bottom = 16.dp + bottomPadding),
    ) {
        if (utxos.size > 1) {
            item(key = "compound") {
                Column(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .insetGroupedCell(0, 1)
                            .clickable(onClick = onCompound)
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Default.CallMerge, null, tint = KaspaTeal, modifier = Modifier.size(20.dp))
                        Text(
                            stringResource(R.string.compound_utxos),
                            color = KaspaTeal,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    // The section footer.
                    Text(
                        stringResource(R.string.compound_utxos_description),
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 7.dp, bottom = 24.dp)
                    )
                }
            }
        }
        when {
            isLoading && utxos.isEmpty() -> item(key = "loading") {
                Box(
                    modifier = Modifier.insetGroupedCell(0, 1).padding(horizontal = 16.dp, vertical = 15.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal)
                }
            }
            // The load-failure row when the node could not be asked, otherwise "No UTXOs.".
            utxos.isEmpty() -> item(key = "empty") {
                if (loadError != null) {
                    UtxoLoadFailureRow(detail = loadError, retry = onRetry, modifier = Modifier.insetGroupedCell(0, 1))
                } else {
                    Text(
                        stringResource(R.string.no_utxos),
                        color = colors.textSecondary,
                        fontSize = 17.sp,
                        modifier = Modifier.insetGroupedCell(0, 1).padding(horizontal = 16.dp, vertical = 15.dp),
                    )
                }
            }
            else -> itemsIndexed(utxos, key = { _, u -> "${u.transactionId}:${u.index}" }) { i, utxo ->
                val key = "${utxo.transactionId}:${utxo.index}"
                SpendingAddressUtxoRow(
                    utxo = utxo,
                    label = labels[key],
                    onRenameClick = { onRename(key) },
                    modifier = Modifier.insetGroupedCell(i, utxos.size, separatorStart = 54.dp),
                )
            }
        }
    }
}

/**
 * Receive and Send at the foot of an address's page (iOS `safeAreaInset(edge: .bottom)`): two
 * accent capsules 12 apart, 16 in and 16 off the bottom. Send is "arrow.up.circle.fill" and, with
 * nothing to send, disabled at half opacity as iOS draws it.
 */
@Composable
private fun AddressReceiveSendBar(
    receiveEnabled: Boolean,
    sendEnabled: Boolean,
    onReceive: () -> Unit,
    onSend: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            // Above the dock the page runs under (0 where there is none).
            .padding(bottom = LocalDockInset.current),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AddressBarCapsule(Icons.Default.QrCode, stringResource(R.string.receive), FontWeight.SemiBold, receiveEnabled, onReceive, Modifier.weight(1f))
        AddressBarCapsule(null, stringResource(R.string.send), FontWeight.Bold, sendEnabled, onSend, Modifier.weight(1f))
    }
}

@Composable
private fun AddressBarCapsule(
    /** Null: iOS's "arrow.up.circle.fill" - a black disc with the arrow cut out in the accent. */
    icon: ImageVector?,
    title: String,
    weight: FontWeight,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp).alpha(if (enabled) 1f else 0.5f),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = KaspaTeal,
            contentColor = Color.Black,
            disabledContainerColor = KaspaTeal,
            disabledContentColor = Color.Black,
        )
    ) {
        if (icon != null) Icon(icon, null, modifier = Modifier.size(18.dp))
        else FilledCircleArrow(up = true, color = Color.Black, cutout = KaspaTeal, size = 17.dp)
        Spacer(Modifier.width(8.dp))
        Text(title, fontSize = 15.sp, fontWeight = weight)
    }
}

/** SF Symbols' "arrow.up.circle.fill" / "arrow.down.circle.fill": a [color] disc with the arrow
 *  cut out of it, drawn in [cutout] (what is behind the symbol). */
@Composable
private fun FilledCircleArrow(up: Boolean, color: Color, cutout: Color, size: Dp) {
    Box(Modifier.size(size).background(color, CircleShape), contentAlignment = Alignment.Center) {
        Icon(
            if (up) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            null,
            tint = cutout,
            modifier = Modifier.size(size * 0.68f),
        )
    }
}

/**
 * One spending address row in [ManageAddressesScreen] — shared by the main and "Hidden" sections,
 * differing only in whether [WalletService.SpendingAddressEntry.hidden] shows a hide or an unhide
 * swipe action. Hiding is a swipe-left action (matching Chats' swipe-to-delete and Cold Storage's
 * address rows) rather than a permanent icon button, since it's reached for less often than the
 * actions in the overflow menu. Unhiding is always available, but an address can't be hidden while
 * it still holds a balance or is the primary ("Pay in Kaspa") spending address — see
 * [WalletViewModel.setManageAddressHidden], which enforces the same rule as a backstop.
 *
 * Everything besides hide/unhide (copy, QR, set primary, rename) lives behind a single overflow
 * button's actions sheet rather than a row of icons, so the address itself has room to sit
 * on its own line instead of being squeezed by four icon buttons. Send/receive for a specific
 * address live in [SpendingAddressTxHistoryScreen] instead (reached via `onClick`).
 */
/** "Contains domain" capsule tag shown on spending/cold address rows that own at least one KNS
 *  domain — shared component ported from iOS's ContainsDomainTag. */
@Composable
fun ContainsDomainTag() {
    Text(
        text = "Contains domain",
        color = KaspaTeal,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(KaspaTeal.copy(alpha = 0.15f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
private fun ManageAddressRow(
    entry: com.kachat.app.services.WalletService.SpendingAddressEntry,
    onClick: () -> Unit,
    onCopyClick: () -> Unit,
    onQrClick: () -> Unit,
    onRenameClick: () -> Unit,
    /** Row-menu "Hide Address" (iOS parity) — same flag the Address Visibility checklist edits.
     *  Null hides the menu entry (e.g. the picker variant of this row). */
    onHideClick: (() -> Unit)? = null,
    /** "Contains domain" tag — this address owns at least one KNS domain (batched lookup). */
    showsDomainTag: Boolean = false,
    /** A quantum-safe (KIP-22) row whose key a spend has put on chain - see [QuantumShield]. */
    quantumSpent: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    var menuAnchor by remember { mutableStateOf(Offset.Zero) }

    // Swipe-to-hide was retired in favor of the bulk Address Visibility checklist (iOS parity).
    // iOS `addressRow`: a glass card (radius 18), padding 16, the text stacked 6 apart.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .manageAddressGlass()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            // iOS: the label and, on a quantum row, its shield, 6 apart.
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = entry.displayLabel,
                    color = LocalAppColors.current.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                if (entry.isQuantum) {
                    QuantumShield(spent = quantumSpent, funded = entry.balanceSompi > 0L)
                }
            }
            Text(
                text = "${entry.address.take(14)}...${entry.address.takeLast(6)}",
                color = LocalAppColors.current.textPrimary,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
            Text(
                text = "%.8f ${KaspaUnit.symbol}".format(java.util.Locale.US, entry.balanceSompi / 100_000_000.0),
                color = LocalAppColors.current.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Three honest states, never a guess: "Used" is monotonic (persisted used-memory
                // plus live probes, so a confirmed-used address can never flip back) and a
                // balance counts as used, "Unused" only when THIS load's live check actually
                // confirmed it, and a neutral "Checking" (iOS's word) when the probe failed or
                // the row was painted from the snapshot - a failed check must not masquerade as
                // a fresh address.
                val (usedTagText, usedTagColor) = when {
                    entry.everUsed || entry.balanceSompi > 0 -> "Used" to LocalAppColors.current.warning
                    entry.liveChecked -> "Unused" to LocalAppColors.current.success
                    else -> "Checking" to LocalAppColors.current.textSecondary
                }
                Text(
                    text = usedTagText,
                    color = usedTagColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (showsDomainTag) {
                    ContainsDomainTag()
                }
            }
        }
        // iOS: a bold "ellipsis" turned upright, secondary, in a 24 frame.
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable(onClick = { showMenu = true })
                .onGloballyPositioned { coords ->
                    menuAnchor = coords.positionInWindow() + Offset(0f, coords.size.height.toFloat())
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.MoreVert, "Address actions", tint = LocalAppColors.current.textSecondary, modifier = Modifier.requiredSize(28.dp))
        }
    }

    // Half sheet rather than a popup menu (Cold Storage parity): the rows have room to say what
    // each one does, which a menu of bare verbs cannot.
    if (showMenu) {
        ManageAddressActionsSheet(
            title = entry.displayLabel,
            subtitle = entry.address,
            onDismiss = { showMenu = false },
        ) {
            ActionSheetRow(
                icon = Icons.Default.Edit,
                title = stringResource(R.string.rename_address),
                subtitle = "Gives this address a label of your own.",
            ) { showMenu = false; onRenameClick() }
            ActionSheetRow(
                icon = Icons.Default.ContentCopy,
                title = stringResource(R.string.copy_address),
                subtitle = "Puts the full address on the clipboard.",
            ) { showMenu = false; onCopyClick() }
            ActionSheetRow(
                icon = Icons.Default.QrCode,
                title = stringResource(R.string.show_qr_code),
                subtitle = "Full screen, for scanning with another device.",
            ) { showMenu = false; onQrClick() }
            // Hide straight from the row - same effect as unchecking it in Address Visibility,
            // without opening that screen. Any address, funded too: it comes back from there
            // (iOS 245a5d2).
            if (onHideClick != null) {
                ActionSheetRow(
                    icon = Icons.Default.VisibilityOff,
                    title = "Hide Address",
                    subtitle = "Removes it from this list. Re-enable it in Address Visibility.",
                    tint = LocalAppColors.current.warning,
                ) { showMenu = false; onHideClick() }
            }
        }
    }
}

/**
 * The quantum (KIP-22) state of a Manage Addresses row, told honestly (iOS `quantumShield`):
 * - shield: the key has never been on chain (receiving reveals nothing) - quantum-safe.
 * - slashed shield: this address SPENT once, so its key is public forever; orange when funds sit
 *   on it again (move them to a fresh address), gray when it is empty.
 * iOS draws SF Symbols "shield.fill" / "shield.slash.fill" at .caption2 (11 pt).
 */
@Composable
private fun QuantumShield(spent: Boolean, funded: Boolean) {
    if (spent) {
        Icon(
            Icons.Filled.RemoveModerator,
            contentDescription = stringResource(R.string.key_revealed_by_a_spend),
            tint = if (funded) LocalAppColors.current.warning else LocalAppColors.current.textSecondary,
            modifier = Modifier.size(11.dp),
        )
    } else {
        Icon(
            Icons.Filled.Shield,
            contentDescription = stringResource(R.string.quantum_safe),
            tint = LocalAppColors.current.success,
            modifier = Modifier.size(11.dp),
        )
    }
}

/** Shared shell for Manage Addresses' per-row half sheets - name and address on top, then rows. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageAddressActionsSheet(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalAppColors.current
    com.kachat.app.ui.theme.IosSheetColors {
        val colors = LocalAppColors.current
        ModalBottomSheet(
            scrimColor = com.kachat.app.ui.theme.IosSheetScrim,
            shape = com.kachat.app.ui.theme.IosSheetShape,
            tonalElevation = com.kachat.app.ui.theme.IosSheetTonalElevation,
            windowInsets = androidx.compose.foundation.layout.WindowInsets.statusBars,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = colors.background,
        ) {
            // The sheet runs down behind the navigation bar, as iOS's does behind the home indicator;
            // its content stays above it.
            Column(Modifier.navigationBarsPadding()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(title, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(
                        subtitle,
                        color = colors.textSecondary,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    content()
                }
            }
        }
    }
}

/**
 * The chatting address's row at the top of [ManageAddressesScreen] (iOS 6333dd3
 * `chattingAddressRow`): "Chatting Address" with its icon, the short address, its balance and
 * "Used", and a chevron - it opens the chatting address's own screen.
 */
@Composable
private fun ManageChattingAddressRow(address: String, balanceSompi: Long, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .manageAddressGlass()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.chatting_address),
                    color = LocalAppColors.current.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(Icons.Default.Forum, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(14.dp))
            }
            Text(
                text = "${address.take(14)}...${address.takeLast(6)}",
                color = LocalAppColors.current.textPrimary,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
            Text(
                text = "%.8f ${KaspaUnit.symbol}".format(java.util.Locale.US, balanceSompi / 100_000_000.0),
                color = LocalAppColors.current.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Used",
                color = LocalAppColors.current.warning,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        // iOS: "chevron.right" at caption size, secondary.
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = LocalAppColors.current.textSecondary,
            modifier = Modifier.size(20.dp).offset(x = 6.dp)
        )
    }
}

/**
 * iOS ManageAddressesView `glassBackground(cornerRadius: 18)` behind the address cards:
 * `.regularMaterial` (it reads #202020 on the black list), a 0.8 white hairline at 18% and a soft
 * shadow (black 12%, radius 10, 5 down).
 */
@Composable
private fun Modifier.manageAddressGlass(): Modifier {
    val colors = LocalAppColors.current
    val shape = RoundedCornerShape(18.dp)
    val fill = if (colors.isDark) Color(0xFF202020) else colors.surface
    return this
        .iosShadow(18.dp, Color.Black.copy(alpha = 0.12f), 10.dp, 5.dp)
        .clip(shape)
        .background(fill)
        .border(0.8.dp, Color.White.copy(alpha = 0.18f), shape)
}

/**
 * "Edit KaChat Profile": the live address profile editor ([KachatLiveProfileEditorScreen], iOS
 * 5df42b4) on both networks - it writes the `kchat:1:profile:` record, a self-send that needs no
 * registry, so it saves on mainnet too; only the primary name waits for mainnet's registry (iOS
 * d36fc42). The placeholder below (iOS 09e0403) is the switch-back.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KachatProfileEditorScreen(
    onBack: () -> Unit,
    showSetupGuides: Boolean = true,
    onSetupGuide: () -> Unit = {},
) {
    // The live editor on every network since iOS 7227d69, and it saves there too since d36fc42.
    // The placeholder below is kept, unreachable, as the switch-back.
    if (com.kachat.app.services.kachatnames.KachatNamesService.isEnabled) {
        KachatLiveProfileEditorScreen(onBack = onBack)
        return
    }
    val colors = LocalAppColors.current
    @Composable
    fun placeholderRow(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
            }
            Text(text, color = colors.textSecondary)
        }
    }
    Scaffold(
        containerColor = colors.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.edit_kachat_profile), color = colors.textPrimary, fontWeight = FontWeight.Bold) },
                actions = {
                    SheetBarButton(stringResource(R.string.done), onClick = onBack, modifier = Modifier.padding(end = 12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            // The setup guide lives here since 5.2 (iOS b000310) - .kachat is the name KaChat sets
            // up for you; .kas profiles are edited field by field in Your Domains.
            if (showSetupGuides) {
                SettingsSection(title = null) {
                    SettingsNavigationItem(stringResource(R.string.kg_setup_guide), null, showIcon = false, onClick = onSetupGuide)
                }
                SettingsFooter(stringResource(R.string.kg_names_setup_footer))
            }
            SettingsSection(title = null) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.kachat_profile_coming_body), color = colors.textPrimary, style = MaterialTheme.typography.bodyMedium)
                }
            }
            SettingsSection(title = stringResource(R.string.avatar)) { placeholderRow(stringResource(R.string.choose_avatar), Icons.Default.Photo) }
            SettingsSection(title = stringResource(R.string.banner)) { placeholderRow(stringResource(R.string.choose_banner), Icons.Default.Image) }
            SettingsSection(title = stringResource(R.string.profile)) {
                listOf(R.string.bio, R.string.x_handle, R.string.website, R.string.telegram, R.string.discord_user_id, R.string.email, R.string.github, R.string.redirect_url)
                    .forEachIndexed { index, res ->
                        if (index > 0) HorizontalDivider(color = colors.background)
                        placeholderRow(stringResource(res))
                    }
            }
            SettingsSection(title = stringResource(R.string.kachat_name_section)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.name), color = colors.textPrimary, modifier = Modifier.weight(1f))
                    Text(stringResource(R.string.none_yet), color = colors.textSecondary)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * A .kas domain's own profile, from Your Domains > the domain > Customize Profile (iOS 09e0403):
 * the full editor, loaded for that domain on its own and saved to it. The app no longer SHOWS
 * .kas profiles, but a domain's profile is still its owner's to change.
 */
@Composable
fun KnsDomainProfileEditorScreen(
    domain: com.kachat.app.services.KnsAsset,
    viewModel: WalletViewModel,
    onBack: () -> Unit,
) {
    val loading by viewModel.editingDomainLoading.collectAsState()
    val current by viewModel.editingDomain.collectAsState()
    LaunchedEffect(domain.assetId) { viewModel.beginEditingDomain(domain) }
    DisposableEffect(Unit) { onDispose { viewModel.endEditingDomain() } }
    BackHandler(onBack = onBack)
    // The fields seed once per domain, so they wait for its profile rather than seeding empty.
    if (loading || current?.assetId != domain.assetId) {
        Box(Modifier.fillMaxSize().background(LocalAppColors.current.background), contentAlignment = Alignment.Center) {
            com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal)
        }
        return
    }
    EditKnsProfileScreen(viewModel = viewModel, onBack = onBack, editingDomain = domain)
}

@Composable
private fun InscribeProgressRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        Spacer(Modifier.width(12.dp))
        Text(text, color = LocalAppColors.current.textPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditKnsProfileScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onNavigateToDomains: () -> Unit = {},
    onNavigateToSetupGuide: () -> Unit = {},
    /** Edit THIS .kas domain's profile (from Your Domains) rather than the primary's - see
     *  [KnsDomainProfileEditorScreen]. */
    editingDomain: com.kachat.app.services.KnsAsset? = null,
) {
    val primaryProfile by viewModel.knsProfile.collectAsState()
    val domainProfile by viewModel.editingDomainProfile.collectAsState()
    val knsProfile = if (editingDomain != null) domainProfile else primaryProfile
    val primaryDomainName by viewModel.activeProfileDomainName.collectAsState()
    val activeProfileDomainName = editingDomain?.asset ?: primaryDomainName
    val ownedDomainAssets by viewModel.ownedDomainAssets.collectAsState()
    val pendingAvatarUri by viewModel.pendingAvatarUri.collectAsState()
    val pendingBannerUri by viewModel.pendingBannerUri.collectAsState()
    val avatarCleared by viewModel.avatarCleared.collectAsState()
    val bannerCleared by viewModel.bannerCleared.collectAsState()
    val editState by viewModel.editProfileState.collectAsState()
    val showSetupGuides by viewModel.showSetupGuides.collectAsState()

    var bio by remember { mutableStateOf("") }
    var x by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var telegram by remember { mutableStateOf("") }
    var discord by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var github by remember { mutableStateOf("") }
    var redirect by remember { mutableStateOf("") }
    var seeded by remember { mutableStateOf(false) }
    // The domain the fields were last read from. A KNS profile belongs to a DOMAIN, so promoting a
    // different one to primary swaps which avatar, banner and details are in effect - and these
    // fields used to seed exactly once, so the screen kept showing the old domain's values until
    // it was closed and reopened. Keyed on the domain rather than on the profile object so an
    // ordinary refresh of the SAME profile never overwrites what the user is part-way through
    // typing.
    var seededDomain by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(knsProfile, activeProfileDomainName) {
        if (!seeded || seededDomain != activeProfileDomainName) {
            bio = knsProfile?.bio ?: ""
            x = knsProfile?.x ?: ""
            website = knsProfile?.website ?: ""
            telegram = knsProfile?.telegram ?: ""
            discord = knsProfile?.discord ?: ""
            email = knsProfile?.contactEmail ?: ""
            github = knsProfile?.github ?: ""
            redirect = knsProfile?.redirectUrl ?: ""
            // Staged image picks belonged to the previous domain's profile.
            if (seeded) viewModel.clearPendingProfileImages()
            seeded = true
            seededDomain = activeProfileDomainName
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.resetEditProfileState() }
    }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) viewModel.setPendingAvatar(uri) }
    val bannerPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> if (uri != null) viewModel.setPendingBanner(uri) }

    var showSaveDialog by remember { mutableStateOf(false) }

    // What "Save" is actually about to submit — each entry becomes its own on-chain commit/reveal
    // transaction, so this doubles as both the confirm dialog's change list and its cost count.
    val pendingChanges = remember(bio, x, website, telegram, discord, email, github, redirect, pendingAvatarUri, pendingBannerUri, avatarCleared, bannerCleared, knsProfile) {
        buildList {
            if (pendingAvatarUri != null) add("Avatar")
            else if (avatarCleared && !knsProfile?.avatarUrl.isNullOrEmpty()) add("Avatar (removed)")
            if (pendingBannerUri != null) add("Banner")
            else if (bannerCleared && !knsProfile?.bannerUrl.isNullOrEmpty()) add("Banner (removed)")
            if (bio.trim() != (knsProfile?.bio ?: "")) add("Bio")
            if (x.trim() != (knsProfile?.x ?: "")) add("X")
            if (website.trim() != (knsProfile?.website ?: "")) add("Website")
            if (telegram.trim() != (knsProfile?.telegram ?: "")) add("Telegram")
            if (discord.trim() != (knsProfile?.discord ?: "")) add("Discord")
            if (email.trim() != (knsProfile?.contactEmail ?: "")) add("Email")
            if (github.trim() != (knsProfile?.github ?: "")) add("GitHub")
            if (redirect.trim() != (knsProfile?.redirectUrl ?: "")) add("Redirect")
        }
    }

    val inFlight = editState.step !in listOf(
        WalletViewModel.EditProfileStep.IDLE,
        WalletViewModel.EditProfileStep.SUCCESS,
        WalletViewModel.EditProfileStep.PARTIAL_FAILURE,
        WalletViewModel.EditProfileStep.FAILED
    )

    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.edit_kns_profile), color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    SheetBarButton(stringResource(R.string.cancel), onClick = onBack, enabled = !inFlight, modifier = Modifier.padding(start = 12.dp))
                },
                actions = {
                    SheetBarButton(
                        stringResource(R.string.save),
                        onClick = { if (pendingChanges.isNotEmpty()) showSaveDialog = true },
                        enabled = !inFlight && pendingChanges.isNotEmpty(),
                        modifier = Modifier.padding(end = 12.dp),
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Re-enters the same guided wizard used to create a profile from scratch - it
            // already knows (via the domain/profile it fetches) to offer skipping domain
            // registration and pre-fill the banner/avatar/detail steps with whatever's already
            // inscribed, so this is a safe re-entry point regardless of how much of a profile
            // already exists. Lives here (rather than next to "KNS Profile" on the Profile tab)
            // since that spot sits directly beside the banner image, which made it unclickable
            // whenever a banner was set.
            // No setup guide here since 5.2 (iOS b000310): the guide is .kachat's, in Edit .kachat
            // Profile. A .kas profile is edited field by field below.

            SettingsSection(title = stringResource(R.string.avatar)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    ContactAvatar(
                        imageUrl = if (avatarCleared) null else (pendingAvatarUri?.toString() ?: knsProfile?.avatarUrl),
                        fallbackText = activeProfileDomainName ?: "?",
                        size = 64.dp
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TextButton(onClick = { avatarPicker.launch("image/*") }, enabled = !inFlight) {
                            Text(stringResource(R.string.choose_avatar), color = KaspaTeal, fontWeight = FontWeight.Bold)
                        }
                        if (pendingAvatarUri != null || (!avatarCleared && !knsProfile?.avatarUrl.isNullOrEmpty())) {
                            TextButton(
                                onClick = {
                                    if (pendingAvatarUri != null) viewModel.setPendingAvatar(null) else viewModel.clearExistingAvatar()
                                },
                                enabled = !inFlight
                            ) {
                                Text(stringResource(R.string.remove), color = LocalAppColors.current.danger)
                            }
                        }
                    }
                }
            }

            SettingsSection(title = stringResource(R.string.banner)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val previewUrl = if (bannerCleared) null else (pendingBannerUri?.toString() ?: knsProfile?.bannerUrl)
                    if (previewUrl != null) {
                        SubcomposeAsyncImage(
                            model = previewUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LocalAppColors.current.surface)
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        TextButton(onClick = { bannerPicker.launch("image/*") }, enabled = !inFlight) {
                            Text(stringResource(R.string.choose_banner), color = KaspaTeal, fontWeight = FontWeight.Bold)
                        }
                        if (pendingBannerUri != null || (!bannerCleared && !knsProfile?.bannerUrl.isNullOrEmpty())) {
                            TextButton(
                                onClick = {
                                    if (pendingBannerUri != null) viewModel.setPendingBanner(null) else viewModel.clearExistingBanner()
                                },
                                enabled = !inFlight
                            ) {
                                Text(stringResource(R.string.remove), color = LocalAppColors.current.danger)
                            }
                        }
                    }
                }
            }

            SettingsSection(title = stringResource(R.string.profile)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditProfileTextField(stringResource(R.string.bio), bio, { bio = it }, enabled = !inFlight, singleLine = false)
                    EditProfileTextField(stringResource(R.string.x), x, { x = it }, enabled = !inFlight)
                    EditProfileTextField(stringResource(R.string.website), website, { website = it }, enabled = !inFlight)
                    EditProfileTextField(stringResource(R.string.telegram), telegram, { telegram = it }, enabled = !inFlight)
                    EditProfileTextField(stringResource(R.string.discord), discord, { discord = it }, enabled = !inFlight)
                    EditProfileTextField(stringResource(R.string.email), email, { email = it }, enabled = !inFlight)
                    EditProfileTextField(stringResource(R.string.github), github, { github = it }, enabled = !inFlight)
                    EditProfileTextField(stringResource(R.string.redirect), redirect, { redirect = it }, enabled = !inFlight)
                }
            }

            Spacer(Modifier.height(60.dp))
        }
    }

    if (showSaveDialog) {
        val terminal = editState.step in listOf(
            WalletViewModel.EditProfileStep.SUCCESS,
            WalletViewModel.EditProfileStep.PARTIAL_FAILURE,
            WalletViewModel.EditProfileStep.FAILED
        )
        fun closeDialog() {
            showSaveDialog = false
            viewModel.resetEditProfileState()
        }
        com.kachat.app.ui.theme.IosAlertDialog(
            onDismissRequest = {
                when (editState.step) {
                    WalletViewModel.EditProfileStep.IDLE -> showSaveDialog = false
                    else -> if (terminal) closeDialog()
                }
            },
            containerColor = LocalAppColors.current.surface,
            title = {
                Text(
                    when (editState.step) {
                        WalletViewModel.EditProfileStep.IDLE -> "Confirm Changes"
                        WalletViewModel.EditProfileStep.SUCCESS -> "Saved"
                        WalletViewModel.EditProfileStep.PARTIAL_FAILURE -> "Some Changes Failed"
                        WalletViewModel.EditProfileStep.FAILED -> "Save Failed"
                        else -> "Saving..."
                    },
                    color = LocalAppColors.current.textPrimary
                )
            },
            text = {
                when (editState.step) {
                    WalletViewModel.EditProfileStep.IDLE -> Column {
                        Text(
                            "${pendingChanges.size} change${if (pendingChanges.size == 1) "" else "s"}. Each is submitted as its own on-chain transaction from your chatting address:",
                            color = LocalAppColors.current.textPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        pendingChanges.forEach { Text("• $it", color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.bodySmall) }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            KaspaUnit.label(stringResource(R.string.each_transaction_temporarily_uses_2_kas)),
                            color = LocalAppColors.current.textSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    WalletViewModel.EditProfileStep.UPLOADING_AVATAR,
                    WalletViewModel.EditProfileStep.UPLOADING_BANNER,
                    WalletViewModel.EditProfileStep.SUBMITTING_FIELD -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            when (editState.step) {
                                WalletViewModel.EditProfileStep.UPLOADING_AVATAR -> "Uploading avatar..."
                                WalletViewModel.EditProfileStep.UPLOADING_BANNER -> "Uploading banner..."
                                else -> "Submitting ${editState.currentFieldLabel}..."
                            },
                            color = LocalAppColors.current.textSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    WalletViewModel.EditProfileStep.SUCCESS -> Text(
                        if (editState.fieldResults.isEmpty()) "Nothing to save." else "All changes saved.",
                        color = LocalAppColors.current.success,
                        fontWeight = FontWeight.Bold
                    )
                    WalletViewModel.EditProfileStep.PARTIAL_FAILURE -> Column {
                        Text(stringResource(R.string.some_changes_failed_to_save), color = LocalAppColors.current.danger, fontWeight = FontWeight.Bold)
                        editState.fieldResults.filter { !it.success }.forEach {
                            Text("${it.fieldKey}: ${it.errorMessage ?: "failed"}", color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    WalletViewModel.EditProfileStep.FAILED -> Text(
                        editState.fieldResults.firstOrNull { !it.success }?.errorMessage ?: "Save failed",
                        color = LocalAppColors.current.danger
                    )
                }
            },
            confirmButton = {
                when (editState.step) {
                    WalletViewModel.EditProfileStep.IDLE -> TextButton(
                        onClick = {
                            viewModel.saveKnsProfile(
                                mapOf(
                                    "bio" to bio,
                                    "x" to x,
                                    "website" to website,
                                    "telegram" to telegram,
                                    "discord" to discord,
                                    "contactEmail" to email,
                                    "github" to github,
                                    "redirectUrl" to redirect
                                )
                            )
                        }
                    ) {
                        Text(stringResource(R.string.confirm), color = KaspaTeal, fontWeight = FontWeight.Bold)
                    }
                    WalletViewModel.EditProfileStep.SUCCESS,
                    WalletViewModel.EditProfileStep.PARTIAL_FAILURE,
                    WalletViewModel.EditProfileStep.FAILED -> TextButton(onClick = { closeDialog() }) {
                        Text(stringResource(R.string.done), color = KaspaTeal, fontWeight = FontWeight.Bold)
                    }
                    else -> {}
                }
            },
            dismissButton = {
                if (editState.step == WalletViewModel.EditProfileStep.IDLE) {
                    TextButton(onClick = { showSaveDialog = false }) {
                        Text(stringResource(R.string.cancel), color = LocalAppColors.current.textSecondary)
                    }
                }
            }
        )
    }
}

@Composable
private fun EditProfileTextField(label: String, value: String, onValueChange: (String) -> Unit, enabled: Boolean, singleLine: Boolean = true) {
    com.kachat.app.ui.theme.IosTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = LocalAppColors.current.textPrimary,
            unfocusedTextColor = LocalAppColors.current.textPrimary,
            disabledTextColor = LocalAppColors.current.textSecondary,
            focusedBorderColor = KaspaTeal,
            unfocusedBorderColor = LocalAppColors.current.textSecondary,
            focusedLabelColor = KaspaTeal,
            unfocusedLabelColor = LocalAppColors.current.textSecondary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SeedPhraseScreen(viewModel: WalletViewModel, onBack: () -> Unit) {
    val secretCopyHaptic = com.kachat.app.util.rememberHaptics()
    var revealed by remember { mutableStateOf(false) }
    val mnemonic = remember { viewModel.getActiveMnemonic() ?: "" }
    // A passphrase (the "25th word") changes every key: the words alone restore a different,
    // empty wallet, so the backup screen must say so and show it (iOS 3076ab7, IOS-017).
    val passphrase = remember { viewModel.getActivePassphrase() }
    val privateKey = remember { viewModel.getPrivateKeyHex() }
    val words = remember { mnemonic.split(" ") }
    val context = LocalContext.current

    // Blocks screenshots and screen recording of the seed phrase / private key for as long as
    // this screen is on-screen (window-level flag, the standard Android mechanism - unlike iOS,
    // which has no API to block a screenshot outright, only to detect active screen recording
    // after the fact via UIScreen.isCaptured). Cleared on leaving so it doesn't leak onto other
    // screens.
    val window = (LocalContext.current as? Activity)?.window
    DisposableEffect(window) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    // Auto-hides the words again after a short window instead of leaving them on screen
    // indefinitely once revealed — someone glancing at the phone later shouldn't still see them.
    LaunchedEffect(revealed) {
        if (revealed) {
            delay(7000)
            revealed = false
        }
    }

    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.seed_phrase), color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
                actions = {
                    SheetBarButton(stringResource(R.string.done), onClick = onBack, modifier = Modifier.padding(end = 12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LocalAppColors.current.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2C1E1E))
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = LocalAppColors.current.warning,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.security_warning),
                        color = LocalAppColors.current.warning,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.anyone_with_your_seed_phrase_can),
                        color = Color(0xFF948B8B),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (passphrase != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = LocalAppColors.current.warning,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.seed_phrase_also_uses_passphrase),
                        color = LocalAppColors.current.warning,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!revealed) {
                Column(
                    modifier = Modifier
                        .clickable { revealed = true }
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = LocalAppColors.current.textSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.tap_to_reveal_seed_phrase),
                        color = LocalAppColors.current.textSecondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    words.forEachIndexed { index, word ->
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LocalAppColors.current.surface)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = LocalAppColors.current.textSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.width(20.dp)
                            )
                            Text(
                                text = word,
                                color = LocalAppColors.current.textPrimary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                // Under the words, with the same FLAG_SECURE window and 7-second auto-hide.
                if (passphrase != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(LocalAppColors.current.surface)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.seed_phrase_passphrase_label),
                            color = LocalAppColors.current.textSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = passphrase,
                            color = LocalAppColors.current.textPrimary,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (revealed) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Both copies go through the sensitive path: marked secret for the system
                    // clipboard and wiped 30 seconds later (copyPrivateKeyWithAutoWipe). Copying
                    // the phrase is how it reaches a password manager, which is a safer home for
                    // it than a photograph of a notebook (iOS f9beba1).
                    TextButton(onClick = {
                        copyPrivateKeyWithAutoWipe(context, mnemonic, label = "recovery phrase")
                        secretCopyHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                        IosToasts.show("Seed phrase copied. Clipboard will clear in 30s.")
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCopy, null, tint = KaspaTeal, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Copy Seed Phrase", color = KaspaTeal)
                        }
                    }
                    TextButton(onClick = {
                        copyPrivateKeyWithAutoWipe(context, privateKey)
                        // iOS SettingsView: the haptic and the 30-second notice, as the phrase has.
                        secretCopyHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                        IosToasts.show("Private key hex copied. Clipboard will clear in 30s.")
                    }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tag, null, tint = KaspaTeal, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.copy_private_key_hex), color = KaspaTeal)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    // 4.0 (matches iOS): null renders the settings HUB (one row per category); a key renders
    // just that category's items as its own page.
    sectionKey: String? = null,
    walletViewModel: WalletViewModel = hiltViewModel(),
    connectionViewModel: ConnectionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    /** KaPay shows Settings as its own dock tab (iOS wallet 040f91a `isDockTab`): no Done button,
     *  the large "Settings" title under the bar. */
    isDockTab: Boolean = false,
) {
    val balance by walletViewModel.fullBalance.collectAsState()
    val dotColorHex by connectionViewModel.dotColorHex.collectAsState()
    val biometricSeedPhraseEnabled by walletViewModel.biometricSeedPhraseEnabled.collectAsState()
    val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
    // The switch is only true when Android agrees: a stored "on" with the OS permission revoked
    // would show as enabled while nothing could ever arrive.
    val notificationSettingsContext = LocalContext.current
    var notificationPermissionGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    notificationSettingsContext,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notificationPermissionGranted = granted
        // Only flip the setting on when Android actually agreed - otherwise the switch would
        // sit on with nothing behind it.
        if (granted) settingsViewModel.setNotificationsEnabled(true)
    }
    val requestNotificationPermission: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notificationSettingsContext.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, notificationSettingsContext.packageName)
            )
        }
    }
    val kaspaExplorer by settingsViewModel.kaspaExplorer.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        walletViewModel.refreshBalance()
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        containerColor = LocalAppColors.current.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (sectionKey != null) {
                // A category page is pushed inside the sheet's navigation stack: iOS's inline bar
                // with the back chevron on the left and the page's title in the middle - no
                // connection dot, no balance.
                SettingsSubpageBar(
                    title = settingsSectionTitle(sectionKey),
                    onBack = { navController.popBackStack() },
                )
            } else if (isDockTab) {
                // The Settings tab (iOS wallet 040f91a): the connection dot and the balance in the
                // bar, "Settings" large under it, no Done.
                MainPageHeader(title = stringResource(R.string.settings))
            } else {
            // iOS's Settings sheet toolbar: the connection dot and Done in glass, the balance
            // between them - no title.
            Row(
                modifier = Modifier
                    .background(LocalAppColors.current.background)
                    .statusBarsPadding()
                    .fillMaxWidth()
                    // measured on the iPhone: the bar's centre 37 below the sheet's top
                    .padding(horizontal = 16.dp, vertical = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .barGlass()
                        .clip(CircleShape)
                        .clickable { ConnectionStatusOverlayState.open() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(Modifier.size(12.dp).background(Color(dotColorHex), CircleShape))
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { BalanceLabelRow(balance, copyOnTap = true) }
                SheetBarButton(stringResource(R.string.done), onClick = { navController.popBackStack() })
            }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            // The hub keeps its own measured spacing; a category page is an iOS Form, its
            // sections spaced by SettingsFormSection.
            verticalArrangement = if (sectionKey == null) Arrangement.spacedBy(24.dp) else Arrangement.Top
        ) {
            // A Form's first header sits 4 under the inline bar.
            if (sectionKey != null) Spacer(Modifier.height(4.dp))
            if (sectionKey == null) {
                // Flat hub list (matches iOS): no section titles - just the categories, the
                // seed-phrase action, and Danger Zone in one card - its top 122 below the
                // sheet's, as on the iPhone.
                Spacer(Modifier.height(24.dp))
                Surface(
                    color = LocalAppColors.current.surface,
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                    SettingsNavigationItem(stringResource(R.string.customization), Icons.Outlined.Brush, onClick = { navController.navigate("settings_section/customization") })
                    SettingsDivider(inset = IosRowTextInset)
                    SettingsNavigationItem(stringResource(R.string.security), com.kachat.app.ui.theme.IosSymbols.LockShield, onClick = { navController.navigate("settings_section/security") })
                    SettingsDivider(inset = IosRowTextInset)
                    SettingsNavigationItem(stringResource(R.string.connection), Icons.Outlined.CellTower, onClick = { navController.navigate("settings_section/connection") })
                    SettingsDivider(inset = IosRowTextInset)
                    // Top-level Notifications hub (4.0, matches iOS): Chats / Wallet / KaPosts
                    // subpages - sits between Connection and Chats like iOS's settings list.
                    SettingsNavigationItem(stringResource(R.string.notifications), Icons.Outlined.NotificationsActive, onClick = { navController.navigate("settings_section/notifications") })
                    SettingsDivider(inset = IosRowTextInset)
                    // No Chats page: KaChat Wallet has no chats.
                    SettingsNavigationItem(stringResource(R.string.storage), Icons.Outlined.Storage, onClick = { navController.navigate("settings_section/storage") })
                    SettingsDivider(inset = IosRowTextInset)
                    // No Chat History page (export/import of chats): KaChat Wallet has no chats.
                    SettingsNavigationItem(stringResource(R.string.diagnostics), Icons.Outlined.MonitorHeart, onClick = { navController.navigate("settings_section/diagnostics") })
                    SettingsDivider(inset = IosRowTextInset)
                    SettingsActionItem(stringResource(R.string.view_seed_phrase), Icons.Outlined.Key, LocalAppColors.current.danger, labelColor = LocalAppColors.current.danger) {
                        if (biometricSeedPhraseEnabled) {
                            context.authenticateWithDeviceCredential(
                                title = "Unlock to View Seed Phrase",
                                onSuccess = { navController.navigate("seed_phrase") }
                            )
                        } else {
                            navController.navigate("seed_phrase")
                        }
                    }
                    // No Danger Zone: iOS has none (KaPay's wipes are the account list's Delete).
                    }
                }
                // Log Out, its own section at the bottom - moved here from the Wallet tab (iOS
                // wallet 040f91a): a red label and its red icon, no chevron.
                var showLogoutConfirmation by remember { mutableStateOf(false) }
                Surface(
                    color = LocalAppColors.current.surface,
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SettingsActionItem(stringResource(R.string.log_out), Icons.AutoMirrored.Filled.Logout, LocalAppColors.current.danger, labelColor = LocalAppColors.current.danger) {
                        showLogoutConfirmation = true
                    }
                }
                // Half sheet rather than a dialog, matching every other chooser in the app - and
                // it gives the consequence a row of its own beside the action.
                if (showLogoutConfirmation) {
                    ConfirmActionSheet(
                        title = stringResource(R.string.log_out),
                        confirmTitle = stringResource(R.string.log_out),
                        confirmSubtitle = "Signs out of this account. Its data stays on this device.",
                        confirmIcon = Icons.AutoMirrored.Filled.Logout,
                        onConfirm = { walletViewModel.logout() },
                        onDismiss = { showLogoutConfirmation = false },
                    )
                }
                Spacer(Modifier.height(24.dp))
            }

            if (sectionKey == "customization") {
                // iOS customizationPage: one section - the three pickers.
                SettingsFormSection(title = stringResource(R.string.customization)) {
                    // Appearance / Language / Currency are shared with the accounts screen's App
                    // Settings (iOS AppWideCustomizationPickers).
                    AppWideCustomizationPickers(walletViewModel)
                    // No Customize Dock: KaChat Wallet's tabs are fixed.
                    // No Show Fee Estimate: it only governed the chat composers' fee label; the
                    // wallet always shows the fee wherever KAS is sent (owner's call, iOS 1314382).
                }
            }

            if (sectionKey == "security") {
                SettingsFormSection(title = stringResource(R.string.security)) {
                    // Shared with the accounts screen's App Settings (see AppSettingsScreen.kt) -
                    // every row is app-wide, so both entry points render the exact same items.
                    SecuritySettingsItems(walletViewModel = walletViewModel)
                }
            }

            if (sectionKey == "connection") {
                ConnectionHubSections(
                    explorerName = kaspaExplorer.displayName,
                    onOpenConnectionSettings = { navController.navigate("connection_settings") },
                    onOpenExplorer = { navController.navigate("kaspa_explorer_settings") },
                )
            }

            if (sectionKey == "notifications") {
                // The master switch, at the top of the page it governs (iOS NotificationsHubPage):
                // push carries wallet activity, group mentions, broadcasts and KaPosts too, so it
                // sits above the per-feature pages rather than inside one of them. No header, as
                // on iOS; the footer says what it does.
                SettingsFormSection(
                    title = null,
                    first = true,
                    footer = if (notificationsEnabled && notificationPermissionGranted) {
                        "Everything you have switched on below can reach you, even when the app is closed. Turning this off silences all of it."
                    } else if (!notificationPermissionGranted) {
                        // Android only: the OS permission is a separate switch here.
                        "Android has notifications switched off for KaPay. Turning this on will ask for permission."
                    } else {
                        "Turn this on to receive anything you have switched on below, even when the app is closed."
                    },
                ) {
                    SettingsSwitchItem(
                        stringResource(R.string.push_notifications),
                        notificationsEnabled && notificationPermissionGranted,
                    ) { enabled ->
                        if (enabled && !notificationPermissionGranted) {
                            requestNotificationPermission()
                        } else {
                            settingsViewModel.setNotificationsEnabled(enabled)
                        }
                    }
                }

                // KaPay's Notifications is one page (iOS wallet 59593af NotificationsHubPage): the
                // master switch above, then Address Activity, then sound and vibration.
                val addressActivityEnabled by settingsViewModel.addressActivityNotificationsEnabled.collectAsState()
                val soundEnabled by settingsViewModel.notificationSoundEnabled.collectAsState()
                val vibrationEnabled by settingsViewModel.notificationVibrationEnabled.collectAsState()
                SettingsFormSection(title = null, footer = stringResource(R.string.address_activity_footer)) {
                    SettingsSwitchItem(stringResource(R.string.address_activity), addressActivityEnabled) {
                        settingsViewModel.setAddressActivityNotificationsEnabled(it)
                    }
                }
                SettingsFormSection(
                    title = stringResource(R.string.sound_vibration),
                    footer = stringResource(R.string.wallet_notification_sound_footer),
                ) {
                    SettingsSwitchItem(stringResource(R.string.play_sound), soundEnabled) {
                        settingsViewModel.setNotificationSoundEnabled(it)
                    }
                    SettingsDivider()
                    SettingsSwitchItem(stringResource(R.string.vibration), vibrationEnabled) {
                        settingsViewModel.setNotificationVibrationEnabled(it)
                    }
                }
            }

            if (sectionKey == "storage") {
                // Storage hub (iOS storagePage): Address Book Photos, Cache and the one cloud
                // provider - each section with iOS's footer. No message retention or on-device
                // message store: the wallet has none (iOS wallet 301ebfb).
                // Your own data, not cache: photos you assigned in the Address Book (iOS cda0d99).
                // Kept apart from Cache, whose "clear" must never delete anything you'd miss.
                run {
                    val addressBook = com.kachat.app.services.AddressBookManager.shared
                    var addressBookPhotoBytes by remember { mutableStateOf<Long?>(null) }
                    var confirmRemoveAddressBookPhotos by remember { mutableStateOf(false) }
                    val photoVersion = addressBook?.photoVersion ?: 0
                    LaunchedEffect(photoVersion) {
                        addressBookPhotoBytes = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                            addressBook?.photosBytesOnDevice() ?: 0L
                        }
                    }
                    SettingsFormSection(title = stringResource(R.string.on_this_device), footer = stringResource(R.string.ab_photos_footer)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Book, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(stringResource(R.string.ab_photos_title), color = LocalAppColors.current.textPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                            val bytes = addressBookPhotoBytes
                            if (bytes != null) {
                                Text(
                                    com.kachat.app.util.IosByteCount.format(context, bytes),
                                    color = LocalAppColors.current.textSecondary,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontFeatureSettings = "tnum")
                                )
                            } else {
                                com.kachat.app.ui.theme.IosActivityIndicator(modifier = Modifier.size(16.dp), color = LocalAppColors.current.textSecondary, strokeWidth = 2.dp)
                            }
                        }
                        if ((addressBookPhotoBytes ?: 0L) > 0L) {
                            SettingsDivider(inset = IosRowTextInset)
                            Text(
                                stringResource(R.string.ab_photos_remove),
                                color = LocalAppColors.current.danger,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { confirmRemoveAddressBookPhotos = true }
                                    .heightIn(min = 52.dp)
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            )
                        }
                    }
                    if (confirmRemoveAddressBookPhotos) {
                        ConfirmActionSheet(
                            title = stringResource(R.string.ab_photos_remove_question),
                            confirmTitle = stringResource(R.string.remove),
                            confirmSubtitle = stringResource(
                                R.string.ab_photos_frees,
                                com.kachat.app.util.IosByteCount.format(context, addressBookPhotoBytes ?: 0L)
                            ),
                            confirmIcon = Icons.Default.Delete,
                            onConfirm = { addressBook?.removeAllPhotos() },
                            onDismiss = { confirmRemoveAddressBookPhotos = false },
                        )
                    }
                }

                // Kept apart from the rest of Storage on purpose: cloud backup decides what happens
                // to things you would miss, and this decides what happens to things you would not.
                SettingsFormSection(
                    title = null,
                    footer = "Images and files the app can always download again. Clearing them frees space and loses nothing.",
                ) {
                    SettingsNavigationItem("Cache", Icons.Outlined.Delete, onClick = {
                        navController.navigate("cache_settings")
                    })
                }

                // What Nextcloud is for in KaPay: a backup of the Address Book and the Portfolio
                // (iOS wallet 59593af).
                SettingsFormSection(
                    title = stringResource(R.string.ncw_backup),
                    footer = stringResource(R.string.storage_nextcloud_footer),
                ) {
                    SettingsNavigationItem(stringResource(R.string.nextcloud), Icons.Default.Cloud, onClick = {
                        navController.navigate("storage_nextcloud")
                    })
                }
            }

            if (sectionKey == "diagnostics") {
                // Shared with the accounts screen's App Settings (see AppSettingsScreen.kt).
                DiagnosticsSettingsSections(settingsViewModel = settingsViewModel)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    }
}

/** A Settings category page's inline title - iOS's `.navigationTitle` of each page. */
@Composable
private fun settingsSectionTitle(sectionKey: String): String = when (sectionKey) {
    "customization" -> stringResource(R.string.customization)
    "security" -> stringResource(R.string.security)
    "connection" -> stringResource(R.string.connection)
    "notifications" -> stringResource(R.string.notifications)
    "storage" -> stringResource(R.string.storage)
    "diagnostics" -> stringResource(R.string.diagnostics)
    else -> ""
}

/**
 * A Form button row with a leading symbol, both in [color] (iOS tints a Button's Label), and while
 * [busy] the activity indicator at the trailing edge. Disabled, it greys out as iOS's does.
 */
@Composable
private fun SettingsBusyActionItem(
    label: String,
    icon: ImageVector,
    busy: Boolean,
    enabled: Boolean,
    color: Color = KaspaTeal,
    onClick: () -> Unit,
) {
    val tint = if (enabled) color else LocalAppColors.current.textSecondary.copy(alpha = 0.6f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = tint, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (busy) com.kachat.app.ui.theme.IosActivityIndicator(Modifier.size(20.dp))
    }
}

@Composable
fun SettingsSection(title: String?, headerAction: (@Composable () -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    // iOS 26's inset-grouped Form section: the header in title case, 17pt semibold in the
    // secondary label colour (no longer iOS's old small uppercase), over a card with 26pt corners.
    Column {
        if (title != null) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocalAppColors.current.textSecondary,
                    modifier = Modifier.weight(1f).padding(start = 16.dp, bottom = 8.dp)
                )
                headerAction?.invoke()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(LocalAppColors.current.surface)
        ) {
            content()
        }
    }
}

/** iOS Form spacing, measured on the iPhone: 28 from a card to the next section's header, 15
 *  from a footer to it. */
val IosFormSectionGap = 28.dp
val IosFormFooterGap = 15.dp

/**
 * One section of an iOS Form page: [SettingsSection] (header, card), the [footer] under the card
 * rather than in it, and the gap to the next section ([IosFormSectionGap], or [IosFormFooterGap]
 * after a footer). Lay these out in a plain Column (no spacedBy) - see [SettingsFormPage].
 */
@Composable
fun SettingsFormSection(
    title: String?,
    footer: String? = null,
    /** A footer that reports an outcome (iOS colours an error red, a success green). */
    footerColor: Color = Color.Unspecified,
    /** The page's first section: without a header, iOS still starts its card where a first
     *  header's card would sit. */
    first: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        // A section without a header keeps part of a header's room above its card on the
        // iPhone: 32 from the footer above it, against 23 here (measured) - and the whole of it
        // when it opens the page.
        if (title == null) Spacer(Modifier.height(if (first) 24.dp else 9.dp))
        SettingsSection(title = title, content = content)
        if (footer != null) {
            SettingsFormFooter(footer, color = footerColor)
            Spacer(Modifier.height(IosFormFooterGap))
        } else {
            Spacer(Modifier.height(IosFormSectionGap))
        }
    }
}

/** An iOS section footer under its card: 13pt secondary, 16 in, 7 below the card. */
@Composable
fun SettingsFormFooter(text: String, color: Color = Color.Unspecified) {
    Text(
        text = text,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = if (color != Color.Unspecified) color else LocalAppColors.current.textSecondary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 7.dp)
    )
}

/**
 * A pushed settings page's bar, iOS's inline navigation bar: the back chevron on a glass disc at
 * the left, [title] centred in headline (17 semibold), and the page's own [trailing] items.
 * No connection dot and no balance - those are the Settings hub's alone.
 */
@Composable
fun SettingsSubpageBar(
    title: String,
    onBack: () -> Unit,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    SheetNavBar(
        title = title,
        // Inside the Settings sheet the status bar is already consumed; on a full-screen page
        // (the accounts screen's App Settings) this keeps the bar below it.
        modifier = Modifier.background(LocalAppColors.current.background).statusBarsPadding(),
        leading = { SheetBarBackButton(onClick = onBack) },
        trailing = trailing,
    )
}

/**
 * A pushed settings page laid out as iOS's inset-grouped Form: [SettingsSubpageBar], then a
 * scrolling column of [SettingsFormSection]s 16 in from the edges, the first header 4 under the bar.
 */
@Composable
fun SettingsFormPage(
    title: String,
    onBack: () -> Unit,
    trailing: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = { SettingsSubpageBar(title = title, onBack = onBack, trailing = trailing) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(4.dp))
            content()
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * iOS's inline Picker row (`.pickerStyle(.menu)` in a Form): [label] at the left, the selected
 * option in the accent colour with the menu's up/down chevrons at the right. A tap opens a small
 * menu of [options], a checkmark on the selected one.
 */
@Composable
fun <T> SettingsPickerItem(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = LocalAppColors.current.textPrimary,
            fontSize = 17.sp,
            maxLines = 1,
        )
        Spacer(Modifier.width(12.dp))
        Spacer(Modifier.weight(1f))
        Box {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    optionLabel(selected),
                    color = KaspaTeal,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.UnfoldMore, null, tint = KaspaTeal, modifier = Modifier.size(16.dp))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        leadingIcon = {
                            if (option == selected) {
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                            } else {
                                Spacer(Modifier.size(20.dp))
                            }
                        },
                        onClick = {
                            expanded = false
                            if (option != selected) onSelect(option)
                        }
                    )
                }
            }
        }
    }
}

/**
 * iOS's `AppWideCustomizationPickers`: Appearance, Language and Currency as menu Pickers, shared by
 * Settings > Customization and the accounts screen's App Settings (one source of truth).
 */
@Composable
fun AppWideCustomizationPickers(walletViewModel: WalletViewModel) {
    val appearance by walletViewModel.appearance.collectAsState()
    val currencyCode by walletViewModel.currency.collectAsState()
    var language by remember { mutableStateOf(currentAppLanguage()) }

    SettingsPickerItem(
        label = stringResource(R.string.appearance),
        options = com.kachat.app.models.AppAppearance.entries,
        selected = appearance,
        optionLabel = {
            when (it) {
                com.kachat.app.models.AppAppearance.SYSTEM -> stringResource(R.string.appearance_system)
                com.kachat.app.models.AppAppearance.LIGHT -> stringResource(R.string.appearance_light)
                com.kachat.app.models.AppAppearance.DARK -> stringResource(R.string.appearance_dark)
            }
        },
        onSelect = { walletViewModel.setAppearance(it) },
    )
    SettingsDivider()
    SettingsPickerItem(
        label = stringResource(R.string.language),
        options = AppLanguage.entries,
        selected = language,
        optionLabel = { if (it == AppLanguage.SYSTEM) stringResource(R.string.appearance_system) else it.displayName },
        onSelect = {
            language = it
            applyAppLanguage(it)
        },
    )
    SettingsDivider()
    SettingsPickerItem(
        label = stringResource(R.string.currency),
        options = AppCurrency.entries,
        selected = AppCurrency.fromCode(currencyCode),
        optionLabel = { it.displayName },
        onSelect = { walletViewModel.setCurrency(it.code) },
    )
}

/**
 * A circular icon button with its label centered underneath — used in pairs on [ProfileScreen]
 * (Receive Kaspa / Chatting Address). iOS qrCircleIcon: an 80pt accent circle with a black 26pt
 * medium qrcode, then the label in subheadline, 8 below.
 */
@Composable
private fun ProfileCircleAction(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(KaspaTeal)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Color.Black, modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            color = LocalAppColors.current.textPrimary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

/**
 * Lays [this] out [amount] higher than where it would sit and gives that height back, the way
 * SwiftUI's negative top padding does: what follows moves up with it.
 */
private fun Modifier.profilePulledUp(amount: Dp): Modifier = layout { measurable, constraints ->
    val pull = amount.roundToPx()
    val placeable = measurable.measure(constraints)
    layout(placeable.width, (placeable.height - pull).coerceAtLeast(0)) {
        placeable.place(0, -pull)
    }
}

/**
 * SF Symbols' chevron.right as iOS draws it at a small text size: a thin open chevron of the
 * given glyph [height] (caption ~11, footnote ~12), in secondary grey.
 */
@Composable
private fun ProfileChevron(height: Dp, stroke: Dp) {
    val color = LocalAppColors.current.textSecondary
    androidx.compose.foundation.Canvas(modifier = Modifier.size(width = height * 0.55f, height = height)) {
        val inset = stroke.toPx() / 2f
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(inset, inset)
            lineTo(size.width - inset, size.height / 2f)
            lineTo(inset, size.height - inset)
        }
        drawPath(
            path,
            color = color,
            style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
    }
}

/**
 * Profile's Your Domains / Settings / Help cards (iOS yourDomainsSection, settingsSection,
 * helpSection): a card of its own each (glassBackground 18), padded 16, holding a Label - the
 * primary-coloured outline symbol, the body title 28 in from the symbol's edge - then, for Your
 * Domains, the count in secondary, and the caption chevron.
 */
@Composable
private fun ProfileLinkRow(label: String, icon: ImageVector, value: String? = null, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .iosGlass(18.dp)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = colors.textPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = colors.textPrimary, fontSize = 17.sp, maxLines = 1, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, color = colors.textSecondary, fontSize = 17.sp, maxLines = 1)
            Spacer(Modifier.width(8.dp))
        }
        ProfileChevron(height = 11.dp, stroke = 1.6.dp)
    }
}

/**
 * One row of Profile's About card (iOS aboutSection): the body label, then the value in secondary
 * grey (Support Email in the accent, as iOS draws a mail link) or, for a row with no value, the
 * caption-semibold chevron. Padded 16 all round.
 */
@Composable
private fun ProfileAboutRow(
    label: String,
    value: String?,
    valueColor: Color = LocalAppColors.current.textSecondary,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = LocalAppColors.current.textPrimary, fontSize = 17.sp, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        if (value != null) {
            Text(value, color = valueColor, fontSize = 17.sp, maxLines = 1)
        } else {
            ProfileChevron(height = 11.dp, stroke = 2.dp)
        }
    }
}

/** iOS `Divider().padding(.leading, 16)`: a hairline from the row text to the card's right edge. */
@Composable
private fun ProfileAboutDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = LocalAppColors.current.divider,
        thickness = 0.5.dp,
    )
}

/**
 * Profile's Manage Addresses card (iOS 6333dd3 addressDropdownsSection): the accent
 * list.bullet.rectangle in a 48pt material circle, "Manage Addresses" (subheadline semibold) with
 * the account total under it (caption semibold, accent; a spinner until it is known), and a
 * footnote-semibold chevron - padded 16 x 14 on a glass card with 18pt corners. The whole card
 * opens Manage Addresses.
 */
@Composable
private fun ProfileManageAddressesCard(totalSompi: Long?, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .iosGlass(18.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                // The material circle drawn on the material card: one step lighter than it.
                .background(colors.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(26.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                stringResource(R.string.manage_addresses),
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
            )
            if (totalSompi != null) {
                Text(
                    "%.8f %s".format(java.util.Locale.US, totalSompi / 100_000_000.0, KaspaUnit.symbol),
                    color = KaspaTeal,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    maxLines = 1,
                )
            } else {
                com.kachat.app.ui.theme.IosActivityIndicator(modifier = Modifier.size(14.dp), color = colors.textSecondary, strokeWidth = 2.dp)
            }
        }
        ProfileChevron(height = 12.dp, stroke = 2.dp)
    }
}

@Composable
fun SettingsSwitchItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = LocalAppColors.current.textPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f).padding(end = 12.dp)
        )
        com.kachat.app.ui.theme.IosSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsNavigationItem(
    label: String,
    icon: ImageVector?,
    value: String = "",
    showIcon: Boolean = true,
    /** iOS tints a row's symbol with the accent; a Label given `.primary` draws it white. */
    iconTint: Color = KaspaTeal,
    /** iOS writes a link row's trailing value in `.caption` (12): pass 12.sp for that. */
    valueFontSize: androidx.compose.ui.unit.TextUnit = androidx.compose.ui.unit.TextUnit.Unspecified,
    valueColor: Color = LocalAppColors.current.textSecondary,
    onClick: () -> Unit = {},
) {
    // iOS 26 list row: 52pt tall, a 24pt symbol in its own column, the label lined up with the
    // separator that starts under it (see SettingsDivider(inset)).
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon && icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(text = label, color = LocalAppColors.current.textPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (value.isNotEmpty()) {
            Text(text = value, color = valueColor, style = MaterialTheme.typography.bodyMedium, fontSize = valueFontSize, modifier = Modifier.padding(horizontal = 8.dp))
        }
        IosDisclosureChevron()
    }
}

/** iOS's disclosure chevron at the end of a navigation row. */
@Composable
fun IosDisclosureChevron() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = LocalAppColors.current.textSecondary,
        modifier = Modifier.size(24.dp)
    )
}

/** Where a row's text starts when it has a leading symbol: 16 + 24 + 16. Separators inset to it. */
val IosRowTextInset = 56.dp

@Composable
fun SettingsActionItem(
    label: String,
    icon: ImageVector,
    color: Color,
    /** Defaults to [color]; set it when the label should read differently from the icon. */
    labelColor: Color = color,
    /** A row that opens a page (iOS's Danger Zone) carries the disclosure chevron. */
    showChevron: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = labelColor, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (showChevron) IosDisclosureChevron()
    }
}

@Composable
fun SettingsInfoItem(label: String, value: String, valueColor: Color = LocalAppColors.current.textSecondary, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = LocalAppColors.current.textPrimary, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, color = valueColor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SettingsDivider(inset: androidx.compose.ui.unit.Dp = 16.dp) {
    // iOS separators start where the row's text starts and stop short of the right edge.
    HorizontalDivider(modifier = Modifier.padding(start = inset, end = 16.dp), color = LocalAppColors.current.divider, thickness = 0.5.dp)
}

/**
 * Settings > Connection (iOS `ConnectionHubPage`, 0c1af03; 010167f): the Testnet switch first -
 * with the restart note while the running network differs, and no footer - then Connection
 * Settings, then Kaspa Explorer in its own section. Shared by the in-account
 * Settings page and the accounts screen's App Settings.
 */
@Composable
fun ConnectionHubSections(
    explorerName: String,
    onOpenConnectionSettings: () -> Unit,
    onOpenExplorer: () -> Unit,
    connectionViewModel: com.kachat.app.viewmodels.ConnectionViewModel = hiltViewModel(),
) {
    val selected by com.kachat.app.util.KaspaNetwork.selected.collectAsState()
    val isTestnet = selected == com.kachat.app.util.KaspaNetwork.Type.TESTNET
    val haptics = androidx.compose.ui.platform.LocalHapticFeedback.current
    val colors = LocalAppColors.current
    SettingsFormSection(title = stringResource(R.string.connection)) {
        // Mainnet <-> testnet, first in the section (no footer). The choice applies at the next
        // launch, all at once (iOS e6f664f): until then the running app stays on its network.
        // Each network keeps its own connection settings, so Connection Settings below shows
        // the chosen network's values - the ones the app uses after the restart.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Science, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(
                stringResource(R.string.testnet),
                color = colors.textPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            com.kachat.app.ui.theme.IosSwitch(
                checked = isTestnet,
                onCheckedChange = { on ->
                    connectionViewModel.switchNetwork(if (on) com.kachat.app.util.KaspaNetwork.Type.TESTNET else com.kachat.app.util.KaspaNetwork.Type.MAINNET)
                    haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                },
                onColor = colors.warning,
            )
        }
        if (selected != com.kachat.app.util.KaspaNetwork.launch) {
            SettingsDivider(inset = IosRowTextInset)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = colors.warning, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(16.dp))
                Text(
                    stringResource(if (isTestnet) R.string.testnet_restart_to_testnet else R.string.testnet_restart_to_mainnet),
                    color = colors.textPrimary,
                    fontSize = 15.sp
                )
            }
        }
        SettingsDivider(inset = IosRowTextInset)
        SettingsNavigationItem(stringResource(R.string.connection_settings), Icons.Default.Language, onClick = onOpenConnectionSettings)
    }
    // Its own section, no header; the explorer's name in iOS's caption.
    SettingsFormSection(title = null) {
        SettingsNavigationItem(
            stringResource(R.string.kaspa_explorer),
            Icons.Default.Explore,
            explorerName,
            valueFontSize = 12.sp,
            onClick = onOpenExplorer,
        )
    }
}

@Composable
fun SettingsFooter(text: String) {
    // iOS section footer: 13pt secondary label.
    Text(
        text = text,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = LocalAppColors.current.textSecondary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

/**
 * The Security section's rows. The biometric toggles and Child Mode are app-wide, so the same
 * items render both in the in-account Settings > Security page and in the accounts screen's App
 * Settings sheet (see AppSettingsScreen.kt), one source of truth. Caller supplies the enclosing [SettingsSection] and the navigation to the Child Mode screen
 * (each host has its own NavHost).
 */
@Composable
fun SecuritySettingsItems(
    walletViewModel: WalletViewModel,
) {
    val biometricSeedPhraseEnabled by walletViewModel.biometricSeedPhraseEnabled.collectAsState()
    val biometricAccountLoginEnabled by walletViewModel.biometricAccountLoginEnabled.collectAsState()
    val biometricSpendingKeyEnabled by walletViewModel.biometricSpendingKeyEnabled.collectAsState()
    val securityContext = LocalContext.current
    // A biometric lock that can be switched off without passing the lock is not a lock - anyone
    // holding an unlocked phone could have turned all three off and then read the seed phrase.
    // So the switch itself is gated, in BOTH directions: turning one on is a claim about who is
    // holding the device too. The setting is only written after auth succeeds, so a cancelled
    // prompt leaves the switch exactly where it was rather than flipping and flipping back.
    fun gated(reason: String, apply: (Boolean) -> Unit): (Boolean) -> Unit = { enabled ->
        securityContext.authenticateWithDeviceCredential(
            title = reason,
            onSuccess = { apply(enabled) },
        )
    }
    SettingsSwitchItem(
        stringResource(R.string.biometrics_for_seed_phrase),
        biometricSeedPhraseEnabled,
        onCheckedChange = gated(stringResource(R.string.unlock_change_seed_phrase_lock)) {
            walletViewModel.setBiometricSeedPhraseEnabled(it)
        },
    )
    SettingsDivider()
    SettingsSwitchItem(
        stringResource(R.string.biometrics_for_account_login),
        biometricAccountLoginEnabled,
        onCheckedChange = gated(stringResource(R.string.unlock_change_account_login_lock)) {
            walletViewModel.setBiometricAccountLoginEnabled(it)
        },
    )
    SettingsDivider()
    SettingsSwitchItem(
        stringResource(R.string.biometrics_for_address_private_keys),
        biometricSpendingKeyEnabled,
        onCheckedChange = gated(stringResource(R.string.unlock_change_private_key_lock)) {
            walletViewModel.setBiometricSpendingKeyEnabled(it)
        },
    )
    // No Simple (Child) Mode: KaPay removed it (iOS wallet 59593af).
}

/**
 * Settings > Diagnostics as iOS's DiagnosticsSettingsPage: the export button (its explanation in
 * the row, under the title) in a "Diagnostics" section, then Verbose API Logging in a section of
 * its own with its footer. The export gathers app/device/settings/node-pool info and is
 * account-tolerant (see DiagnosticsExportService's no-active-account fallbacks), so the same
 * sections render in the in-account Settings and in the accounts screen's App Settings.
 */
@Composable
fun DiagnosticsSettingsSections(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    connectionViewModel: ConnectionViewModel = hiltViewModel(),
) {
    val diagnosticsExportState by settingsViewModel.diagnosticsExportState.collectAsState()
    val verboseApiLogging by connectionViewModel.verboseApiLogging.collectAsState()
    val context = LocalContext.current
    val diagnosticsExportInFlight = diagnosticsExportState.status == SettingsViewModel.ExportStatus.IN_PROGRESS
    // iOS reports a failed export as a toast; only a run started here is reported.
    var exportArmed by remember { mutableStateOf(false) }
    LaunchedEffect(diagnosticsExportState.status) {
        when (diagnosticsExportState.status) {
            SettingsViewModel.ExportStatus.IN_PROGRESS -> exportArmed = true
            SettingsViewModel.ExportStatus.FAILED -> if (exportArmed) {
                exportArmed = false
                IosToasts.error(diagnosticsExportState.message ?: "Failed to export diagnostics.")
            }
            else -> exportArmed = false
        }
    }

    SettingsFormSection(title = stringResource(R.string.diagnostics)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = !diagnosticsExportInFlight) {
                    settingsViewModel.exportDiagnostics { uri ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/zip"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Export Diagnostics Archive"))
                    }
                }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // iOS: ladybug.fill in title3 and the title in body medium, both accent, 10 apart,
            // the spinner at the end while it runs; then the explanation in footnote.
            val tint = if (diagnosticsExportInFlight) KaspaTeal.copy(alpha = 0.5f) else KaspaTeal
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BugReport, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    stringResource(R.string.export_diagnostics_archive),
                    color = tint,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                if (diagnosticsExportInFlight) com.kachat.app.ui.theme.IosActivityIndicator(Modifier.size(20.dp))
            }
            Text(
                stringResource(R.string.exports_app_device_info_connection_settings),
                color = LocalAppColors.current.textSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
            )
        }
    }
    // Moved here from Connection Settings, as on iOS: it is a troubleshooting switch.
    SettingsFormSection(title = null, footer = stringResource(R.string.verbose_api_logging_footer)) {
        SettingsSwitchItem(stringResource(R.string.verbose_api_logging), verboseApiLogging) {
            connectionViewModel.setVerboseApiLogging(it)
        }
    }
}

/**
 * Kaspa-logo total-balance row — Android port of iOS's BalanceToolbarLabel
 * (SettingsView.swift): 15dp Kaspa logo + full 8-decimal balance in bold with
 * tabular figures and secondary color, so the header balance reads identically
 * on every main page (Chats/Settings via TopStatusBar; Broadcasts, Cold Storage,
 * Swap, Portfolio and KaPosts via BalanceTopBarLabel below).
 */
@Composable
fun BalanceLabelRow(
    balance: String,
    modifier: Modifier = Modifier,
    /** Chats, Profile and Settings: a tap copies the exact figure (iOS's balance toolbar label,
     *  "Balance copied to clipboard."). */
    copyOnTap: Boolean = false,
) {
    val balanceClipboard = LocalClipboardManager.current
    val balanceHaptic = com.kachat.app.util.rememberHaptics()
    Row(
        modifier = modifier.then(
            if (copyOnTap) Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                balanceClipboard.setText(AnnotatedString(balance.substringBefore(' ')))
                balanceHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                IosToasts.show("Balance copied to clipboard.")
            } else Modifier
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painterResource(R.drawable.ic_kaspa_logo),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = balance,
            color = LocalAppColors.current.textSecondary,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFeatureSettings = "tnum"
            )
        )
    }
}

/**
 * Self-contained variant for screens that don't already collect the wallet balance:
 * observes WalletViewModel.fullBalance and refreshes it once on appearance (matches
 * the iOS label's `.task { refreshBalance() }`).
 */
@Composable
fun BalanceTopBarLabel(
    modifier: Modifier = Modifier,
    walletViewModel: WalletViewModel = hiltViewModel()
) {
    val balance by walletViewModel.fullBalance.collectAsState()
    LaunchedEffect(Unit) { walletViewModel.refreshBalance() }
    BalanceLabelRow(balance, modifier)
}

@Composable
fun TopStatusBar(
    balance: String,
    onStatusClick: () -> Unit,
    onAddClick: () -> Unit = {},
    dotColorHex: Long = 0xFF4CD964,
    // Chats has this moved to a floating action button instead (see ChatsScreen.kt), and
    // Profile/Settings have no "add chat" action of their own - every current caller passes
    // false explicitly, so this default only matters for a future screen that doesn't.
    showAddButton: Boolean = true,
    showEditButton: Boolean = false,
    isEditing: Boolean = false,
    onEditClick: () -> Unit = {},
    selectAllLabel: String? = null,
    onSelectAllClick: () -> Unit = {},
    // Rendered at the trailing end when there is no Add button - a slot rather than a fixed
    // button so a page can decide what belongs there (Profile puts its notification bell here).
    trailingContent: (@Composable () -> Unit)? = null,
) {
    // iOS's toolbar: the connection dot leading, the balance as the principal item (placed as
    // UIKit places one - see IosToolbarRow), and the trailing items (Select All, the page's own
    // button, Select/Cancel) together on one piece of glass.
    IosToolbarRow(
        modifier = Modifier.fillMaxWidth().height(52.dp),
        leading = { ConnectionDotButton(onClick = onStatusClick) },
        principal = { BalanceLabelRow(balance, copyOnTap = true) },
        trailing = { TopStatusBarTrailing(showAddButton, showEditButton, isEditing, onEditClick, selectAllLabel, onSelectAllClick, onAddClick, trailingContent) },
    )
}

@Composable
private fun TopStatusBarTrailing(
    showAddButton: Boolean,
    showEditButton: Boolean,
    isEditing: Boolean,
    onEditClick: () -> Unit,
    selectAllLabel: String?,
    onSelectAllClick: () -> Unit,
    onAddClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)?,
) {
    run {
        ToolbarGlassGroup {
            if (isEditing && selectAllLabel != null) {
                ToolbarTextButton(selectAllLabel, onSelectAllClick)
            }
            if (!isEditing) {
                if (showAddButton) {
                    ToolbarIconButton(Icons.Default.PersonAddAlt1, stringResource(R.string.add_contact), onAddClick, iconSize = 20.dp)
                } else if (trailingContent != null) {
                    trailingContent()
                }
            }
            if (showEditButton) {
                // Text toggle instead of a pen icon, matching iOS's "Select"/"Cancel" toolbar
                // button (ChatListView.swift) rather than a Material edit-pencil affordance.
                ToolbarTextButton(if (isEditing) "Cancel" else "Select", onEditClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionStatusScreen(onBack: () -> Unit, viewModel: ConnectionViewModel = hiltViewModel()) {
    val network by viewModel.network.collectAsState()
    // The indexer this launch is connected to (a Testnet switch waits for the next launch).
    val indexerUrl by viewModel.runningIndexerUrl.collectAsState()

    val activeNodes by viewModel.activeNodes.collectAsState()
    val allNodes by viewModel.allNodes.collectAsState()
    // Pool sections only make sense when the POOL is choosing the node. Pinned to a specific
    // node (the shipped default or the user's own), there is no pool at work - discovery is
    // off and the app only ever talks to that one address - so pool status, pool actions, and
    // the node lists would just show a pool of one. Null (not yet loaded) hides them too, so
    // a pinned user never sees them flash on screen entry.
    val nodeSelectionIsAutomatic by viewModel.nodeSelectionIsAutomatic.collectAsState()
    val showPoolSections = nodeSelectionIsAutomatic == true
    // "Other Nodes" means genuinely other than what's already listed above under Active
    // Nodes — allNodes includes every known node (active and not), so without this filter
    // the same active nodes showed up twice, once in each section.
    val otherNodes = remember(allNodes) { allNodes.filterNot { it.status == "Active" } }
    val status by viewModel.status.collectAsState()
    val dotColorHex by viewModel.dotColorHex.collectAsState()
    val lastSyncAt by viewModel.lastSyncAt.collectAsState()
    val nodeConnectionsBlocked by viewModel.nodeConnectionsBlocked.collectAsState()
    val scrollState = rememberScrollState()
    val isRefreshingPool by viewModel.isRefreshingPool.collectAsState()
    val isReconnecting by viewModel.isReconnecting.collectAsState()
    val poolActionsEnabled = !isRefreshingPool && !isReconnecting
    var showClearPoolConfirm by remember { mutableStateOf(false) }
    val connectionClipboard = LocalClipboardManager.current
    val connectionHaptic = com.kachat.app.util.rememberHaptics()
    val connectionContext = LocalContext.current
    // iOS: tapping an active node copies its host:port.
    val copyNodeEndpoint: (NodeInfo) -> Unit = { node ->
        connectionClipboard.setText(AnnotatedString(node.ip))
        connectionHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
        IosToasts.show(connectionContext.getString(R.string.node_endpoint_copied))
    }

    // status is derived from the exact same latency threshold as dotColorHex, so
    // the text here can never contradict the dot's color: green only says
    // Connected/Healthy, orange only says Degraded, red only says Disconnected/Unhealthy.
    val statusColor = Color(dotColorHex)
    val statusText = when (status) {
        ConnStatus.CONNECTED -> "Connected"
        ConnStatus.DEGRADED -> "Degraded"
        ConnStatus.DISCONNECTED -> "Disconnected"
    }
    val poolHealthText = when (status) {
        ConnStatus.CONNECTED -> "Healthy"
        ConnStatus.DEGRADED -> "Degraded"
        ConnStatus.DISCONNECTED -> "Unhealthy"
    }
    // "Verified" = currently reachable at all (Active or Suspect), a broader real count
    // than "Active" (Active additionally requires being in-sync and not recently failing).
    // Excludes nodes that haven't been probed even once yet (latency == "—") — those also
    // report as "Suspect" (see NodeRegistry.statusOf's lastProbe == null branch), but counting
    // them as "Verified" is misleading: right after a fresh launch/DNS-seed resolution this made
    // the whole pool look "Verified" before a single probe had actually completed.
    val verifiedCount = remember(allNodes) { allNodes.count { (it.status == "Active" || it.status == "Suspect") && it.latency != "—" } }

    if (showClearPoolConfirm) {
        com.kachat.app.ui.theme.IosAlertDialog(
            onDismissRequest = { showClearPoolConfirm = false },
            title = { Text(stringResource(R.string.clear_connection_pool_confirm_title), color = LocalAppColors.current.textPrimary) },
            text = { Text(stringResource(R.string.clear_connection_pool_confirm_message), color = LocalAppColors.current.textPrimary) },
            confirmButton = {
                TextButton(onClick = {
                    showClearPoolConfirm = false
                    viewModel.clearPool()
                }) { Text(stringResource(R.string.clear), color = LocalAppColors.current.danger) }
            },
            dismissButton = {
                TextButton(onClick = { showClearPoolConfirm = false }) {
                    Text(stringResource(R.string.cancel), color = KaspaTeal, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    // iOS presents Connection Status as a sheet holding a Form (ConnectionStatusDetailView): the
    // grouped sheet palette, rounded top 10 below the status bar, the screen behind dimmed, an
    // inline bar with Done on glass.
    val primaryHost = activeNodes.firstOrNull()?.ip?.let { ip ->
        com.kachat.app.services.grpc.parseNodeAddress(ip)?.host ?: ip.substringAfter("://")
    }
    // iOS writes "138 ms" and colours it by speed: green under 100, the primary colour under
    // 200, orange under 500, red above.
    val latencyMs = activeNodes.firstOrNull()?.latency?.removeSuffix("ms")?.trim()?.toIntOrNull()
    val latencyColor = when {
        latencyMs == null -> LocalAppColors.current.textSecondary
        latencyMs < 100 -> LocalAppColors.current.success
        latencyMs < 200 -> LocalAppColors.current.textPrimary
        latencyMs < 500 -> LocalAppColors.current.warning
        else -> LocalAppColors.current.danger
    }
    // iOS shows the indexer's host alone (URLComponents.host): no scheme, port or path.
    val indexerHost = indexerUrl.substringAfter("://").substringBefore("/").substringBefore(":")

    IosSheetPage(grouped = true) {
        SheetNavBar(
            title = stringResource(R.string.connection_status),
            trailing = { SheetBarButton(stringResource(R.string.done), onClick = onBack) },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            SettingsSection(title = stringResource(R.string.connection_status)) {
                ConnectionStatusRow(stringResource(R.string.status), statusText, dot = statusColor)
                SettingsDivider()
                ConnectionStatusRow(
                    stringResource(R.string.protocol),
                    if (activeNodes.firstOrNull()?.ip?.let { com.kachat.app.services.grpc.parseNodeAddress(it)?.secure } == true) {
                        "gRPC (secure)"
                    } else {
                        "gRPC (plaintext)"
                    }
                )
                // Honest all-blocked explanation: only shown once the pool has concluded that
                // NO node answers at all on this network (see NodePoolManager's
                // nodeConnectionsBlocked doc) - a plain red "Disconnected" with no reason
                // otherwise reads as an app bug on firewalled/captive networks. Laid out as
                // iOS's "Node Network: Blocked" row with its caption under it.
                if (nodeConnectionsBlocked && status == ConnStatus.DISCONNECTED) {
                    SettingsDivider()
                    ConnectionStatusRow("Node Network", "Blocked", valueColor = LocalAppColors.current.warning)
                    Text(
                        stringResource(R.string.node_connections_blocked),
                        color = LocalAppColors.current.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    )
                }
                // iOS leaves the node and latency rows out until there is one.
                if (primaryHost != null) {
                    SettingsDivider()
                    ConnectionStatusRow(stringResource(R.string.connected_node), primaryHost, monospaced = true)
                }
                if (latencyMs != null) {
                    SettingsDivider()
                    ConnectionStatusRow(stringResource(R.string.latency), "$latencyMs ms", valueColor = latencyColor)
                }
                SettingsDivider()
                ConnectionStatusRow(stringResource(R.string.indexer), indexerHost, monospaced = true)
                SettingsDivider()
                ConnectionStatusRow(stringResource(R.string.last_sync), lastSyncAt)
            }

            if (showPoolSections) {
                SettingsSection(title = stringResource(R.string.pool_status)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PoolStatItem(stringResource(R.string.active), activeNodes.size.toString(), LocalAppColors.current.success)
                        PoolStatItem(stringResource(R.string.verified), verifiedCount.toString(), Color(0xFF2196F3))
                        PoolStatItem(stringResource(R.string.total), allNodes.size.toString(), LocalAppColors.current.textSecondary)
                    }
                    SettingsDivider()
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.pool_health), color = LocalAppColors.current.textPrimary)
                        Text(poolHealthText, color = statusColor)
                    }
                }
            }

            // iOS's Actions section: plain text buttons, a spinner in the row while it runs, no
            // toast; Clear asks first. The primary node's host is the section's footer.
            Column {
                SettingsSection(title = stringResource(R.string.actions)) {
                    // Refresh/Clear operate on pool discovery, which is off while pinned to a
                    // specific node - only Reconnect still means something there.
                    if (showPoolSections) {
                        ConnectionActionRow(
                            stringResource(R.string.refresh_pool),
                            color = KaspaTeal,
                            busy = isRefreshingPool,
                            enabled = poolActionsEnabled,
                        ) { viewModel.refreshPool() }
                        SettingsDivider()
                        ConnectionActionRow(
                            stringResource(R.string.clear_connection_pool),
                            color = LocalAppColors.current.danger,
                            busy = false,
                            enabled = poolActionsEnabled,
                        ) { showClearPoolConfirm = true }
                        SettingsDivider()
                    }
                    ConnectionActionRow(
                        stringResource(R.string.reconnect),
                        color = KaspaTeal,
                        busy = isReconnecting,
                        enabled = poolActionsEnabled,
                    ) { viewModel.reconnect() }
                }
                primaryHost?.let { primary ->
                    Text(
                        text = "Primary: $primary",
                        color = LocalAppColors.current.textSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp)
                    )
                }
            }

            KaspaNodeQuickAccessSection(viewModel)

            if (showPoolSections) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(LocalAppColors.current.success, CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(R.string.active_nodes), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = LocalAppColors.current.textSecondary)
                    }
                    Text(text = activeNodes.size.toString(), color = LocalAppColors.current.textSecondary)
                }
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(LocalAppColors.current.surface)
                ) {
                    activeNodes.forEachIndexed { index, node ->
                        Box(Modifier.clickable { copyNodeEndpoint(node) }) { ActiveNodeRow(node) }
                        if (index < activeNodes.size - 1) SettingsDivider()
                    }
                }
                Text(
                    text = stringResource(R.string.tap_a_node_to_copy_host_port),
                    fontSize = 13.sp,
                    color = LocalAppColors.current.textSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(LocalAppColors.current.textSecondary, CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Text(text = stringResource(R.string.other_nodes), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = LocalAppColors.current.textSecondary)
                    }
                    Text(text = otherNodes.size.toString(), color = LocalAppColors.current.textSecondary)
                }
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(LocalAppColors.current.surface)
                ) {
                    otherNodes.forEachIndexed { index, node ->
                        AllNodeRow(node)
                        if (index < otherNodes.size - 1) SettingsDivider()
                    }
                }

                Text(
                    text = stringResource(R.string.all_discovered_nodes_sorted_by_state),
                    fontSize = 13.sp,
                    color = LocalAppColors.current.textSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }


            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

/** A label/value row of iOS ConnectionStatusDetailView's first section: the label 17pt, the value
 *  trailing in the secondary colour (or [valueColor]) - a host name in monospaced caption, as iOS
 *  writes the node and indexer - and, for Status, a 10pt dot in [dot]'s colour before it. */
@Composable
private fun ConnectionStatusRow(
    label: String,
    value: String,
    valueColor: Color = LocalAppColors.current.textSecondary,
    monospaced: Boolean = false,
    dot: Color? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = LocalAppColors.current.textPrimary, fontSize = 17.sp, maxLines = 1)
        Spacer(Modifier.width(12.dp))
        Spacer(Modifier.weight(1f))
        if (dot != null) {
            Box(modifier = Modifier.size(10.dp).background(dot, CircleShape))
            Spacer(Modifier.width(6.dp))
        }
        Text(
            value,
            color = valueColor,
            fontSize = if (monospaced) 12.sp else 17.sp,
            fontFamily = if (monospaced) FontFamily.Monospace else null,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** A row of iOS ConnectionStatusDetailView's Actions section: the label as a plain text button,
 *  and while [busy], the activity indicator at the trailing edge. Greyed out while disabled. */
@Composable
private fun ConnectionActionRow(label: String, color: Color, busy: Boolean, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .heightIn(min = 52.dp)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (enabled) color else LocalAppColors.current.textSecondary.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (busy) com.kachat.app.ui.theme.IosActivityIndicator(Modifier.size(20.dp))
    }
}

/**
 * Settings > Notifications > Chats, iOS's NotificationsSettingsView: Sound & Vibration with its
 * footer, then the Per Contact note. The master switch (and the permission it needs) is on the
 * Notifications page above this one; the push diagnostics are in the diagnostics export.
 */
@Composable
fun NotificationSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val soundEnabled by viewModel.notificationSoundEnabled.collectAsState()
    val vibrationEnabled by viewModel.notificationVibrationEnabled.collectAsState()

    // "Chats" since 4.0: this page is the Chats subpage of the top-level Notifications hub
    // (Settings > Notifications > Chats), matching iOS.
    SettingsFormPage(title = stringResource(R.string.chats), onBack = onBack) {
        SettingsFormSection(
            title = stringResource(R.string.sound_vibration),
            footer = stringResource(R.string.notification_sound_footer),
        ) {
            SettingsSwitchItem(stringResource(R.string.play_sound), soundEnabled) {
                viewModel.setNotificationSoundEnabled(it)
            }
            SettingsDivider()
            SettingsSwitchItem(stringResource(R.string.vibration), vibrationEnabled) {
                viewModel.setNotificationVibrationEnabled(it)
            }
        }
        SettingsFormSection(title = stringResource(R.string.per_contact)) {
            Text(
                stringResource(R.string.per_contact_notification_hint),
                color = LocalAppColors.current.textSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

/**
 * Settings > Notifications > Wallet — the Address Activity toggle (default ON). Gates
 * [com.kachat.app.services.AddressActivityNotifier]'s local notifications for external receipts
 * on any spending or cold storage address. Mirrors iOS's WalletNotificationSettingsView.
 */
@Composable
fun WalletNotificationSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val addressActivityEnabled by viewModel.addressActivityNotificationsEnabled.collectAsState()
    SettingsFormPage(title = stringResource(R.string.notifications_wallet), onBack = onBack) {
        SettingsFormSection(title = null, footer = stringResource(R.string.address_activity_footer)) {
            SettingsSwitchItem(stringResource(R.string.address_activity), addressActivityEnabled) {
                viewModel.setAddressActivityNotificationsEnabled(it)
            }
        }
    }
}

/** Settings > Connection > Kaspa Explorer, iOS's KaspaExplorerSettingsView: the explorers under an
 *  "Explorer" header, a checkmark on the chosen one, and iOS's footer under the card. */
@Composable
fun KaspaExplorerSettingsScreen(onBack: () -> Unit, settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val kaspaExplorer by settingsViewModel.kaspaExplorer.collectAsState()

    SettingsFormPage(title = stringResource(R.string.kaspa_explorer), onBack = onBack) {
        SettingsFormSection(
            title = stringResource(R.string.explorer),
            footer = stringResource(R.string.explorer_footer),
        ) {
            com.kachat.app.models.KaspaExplorer.entries.forEachIndexed { index, explorer ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { settingsViewModel.updateKaspaExplorer(explorer) }
                        .heightIn(min = 52.dp)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(explorer.displayName, color = LocalAppColors.current.textPrimary, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    if (kaspaExplorer == explorer) {
                        Icon(Icons.Default.Check, null, tint = KaspaTeal, modifier = Modifier.size(20.dp))
                    }
                }
                if (index < com.kachat.app.models.KaspaExplorer.entries.lastIndex) {
                    SettingsDivider()
                }
            }
        }
    }
}

/**
 * A single dropdown for quickly picking which node to connect to: the default node (recommended),
 * automatic discovery, or any address already saved in the IP Address Book. Used by both
 * [ConnectionStatusScreen] (tap the status dot) and [ConnectionSettingsScreen] (above the
 * [AddressBookSection], which is where the address book itself is managed - adding, removing, and
 * labeling entries) - there's no free-text entry anymore, only this dropdown.
 */
@Composable
private fun KaspaNodeQuickAccessSection(viewModel: ConnectionViewModel) {
    val trustedNodeAddress by viewModel.trustedNodeAddress.collectAsState()
    val savedNodeAddresses by viewModel.savedNodeAddresses.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val normalizedTrusted = trustedNodeAddress.trim()
    val defaultAddress = com.kachat.app.repository.AppSettingsRepository.DEFAULT_TRUSTED_NODE_ADDRESS
    val matchingSaved = savedNodeAddresses.firstOrNull { it.address.trim() == normalizedTrusted }

    val selectedLabel = when {
        normalizedTrusted.isEmpty() -> stringResource(R.string.automatic_scan)
        normalizedTrusted == defaultAddress.trim() -> stringResource(R.string.default_recommended)
        matchingSaved != null -> matchingSaved.label.ifBlank { matchingSaved.address }
        else -> normalizedTrusted
    }

    // iOS: a Picker row in a Form - the "Kaspa Node" label, the current choice trailing in the
    // accent colour with the menu's up/down chevrons - and the explanation as the section's
    // footer, under the card rather than in it.
    Column {
    SettingsSection(title = stringResource(R.string.kaspa_node)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .heightIn(min = 52.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                stringResource(R.string.kaspa_node),
                color = LocalAppColors.current.textPrimary,
                fontSize = 17.sp,
                maxLines = 1,
            )
            Spacer(Modifier.width(12.dp))
            Spacer(Modifier.weight(1f))
            Box {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        selectedLabel,
                        color = KaspaTeal,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.UnfoldMore, null, tint = KaspaTeal, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    // The shipped default node is a mainnet node - not offered on testnet (iOS bdae4b7).
                    val selectedNetwork by com.kachat.app.util.KaspaNetwork.selected.collectAsState()
                    if (selectedNetwork == com.kachat.app.util.KaspaNetwork.Type.MAINNET || normalizedTrusted == defaultAddress.trim()) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.default_recommended)) },
                        onClick = {
                            expanded = false
                            viewModel.setTrustedNodeAddress(defaultAddress)
                        }
                    )
                    }
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.automatic_scan)) },
                        onClick = {
                            expanded = false
                            viewModel.setTrustedNodeAddress("")
                        }
                    )
                    savedNodeAddresses.forEach { entry ->
                        DropdownMenuItem(
                            text = { Text(entry.label.ifBlank { entry.address }) },
                            onClick = {
                                expanded = false
                                viewModel.setTrustedNodeAddress(entry.address)
                            }
                        )
                    }
                }
            }
        }
        if (normalizedTrusted.isNotEmpty()) {
            SettingsDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.connected_only_to_this_node), color = KaspaTeal, fontSize = 13.sp)
            }
        }
    }
    Text(
        stringResource(R.string.kaspa_node_quick_access_footer),
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = LocalAppColors.current.textSecondary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp)
    )
    }
}

/**
 * "IP Address Book" - the full management UI (add/remove/label saved addresses), used only by
 * [ConnectionSettingsScreen], right below its own [KaspaNodeQuickAccessSection]. The trusted-node
 * override itself is now dropdown-only (default/automatic/a saved address) - no free-text entry -
 * so this section is just the address book, feeding entries into that dropdown.
 */
@Composable
private fun AddressBookSection(viewModel: ConnectionViewModel) {
    val savedNodeAddresses by viewModel.savedNodeAddresses.collectAsState()
    val pinnedNodeAddress by viewModel.trustedNodeAddress.collectAsState()
    val nodeBookHaptic = com.kachat.app.util.rememberHaptics()

    SettingsSection(title = stringResource(R.string.ip_address_book)) {
        var newLabel by remember { mutableStateOf("") }
        var newAddress by remember { mutableStateOf("") }
        var addError by remember { mutableStateOf<String?>(null) }
        val clipboardManager = LocalClipboardManager.current
        val context = LocalContext.current

        TextField(
            value = newLabel,
            onValueChange = { newLabel = it },
            placeholder = { Text(stringResource(R.string.label_optional), color = LocalAppColors.current.textTertiary) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).height(50.dp).clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = LocalAppColors.current.surfaceVariant,
                unfocusedContainerColor = LocalAppColors.current.surfaceVariant,
                focusedTextColor = LocalAppColors.current.textPrimary,
                unfocusedTextColor = LocalAppColors.current.textPrimary,
                cursorColor = KaspaTeal,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newAddress,
                onValueChange = { newAddress = it; addError = null },
                placeholder = { Text(stringResource(R.string.host_port_or_grpcs_host), color = LocalAppColors.current.textTertiary) },
                modifier = Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LocalAppColors.current.surfaceVariant,
                    unfocusedContainerColor = LocalAppColors.current.surfaceVariant,
                    focusedTextColor = LocalAppColors.current.textPrimary,
                    unfocusedTextColor = LocalAppColors.current.textPrimary,
                    cursorColor = KaspaTeal,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(Modifier.width(12.dp))
            IconButton(
                onClick = {
                    val trimmed = newAddress.trim()
                    if (trimmed.isEmpty()) return@IconButton
                    if (com.kachat.app.services.grpc.parseNodeAddress(trimmed) == null) {
                        addError = "Enter as host:port or grpcs://host"
                    } else {
                        addError = null
                        viewModel.addSavedNodeAddress(newLabel.trim(), trimmed)
                        newLabel = ""
                        newAddress = ""
                    }
                },
                modifier = Modifier.size(40.dp).background(KaspaTeal, CircleShape)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black)
            }
        }
        addError?.let {
            Text(it, color = LocalAppColors.current.danger, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }

        if (savedNodeAddresses.isEmpty()) {
            Text(
                stringResource(R.string.no_saved_addresses),
                color = LocalAppColors.current.textSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            savedNodeAddresses.forEachIndexed { index, entry ->
                if (index > 0) SettingsDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboardManager.setText(AnnotatedString(entry.address))
                            nodeBookHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                            IosToasts.show("Node address copied.")
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        if (entry.label.isNotBlank()) {
                            Text(entry.label, color = LocalAppColors.current.textPrimary)
                            Text(
                                entry.address,
                                color = LocalAppColors.current.textSecondary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Text(entry.address, color = LocalAppColors.current.textPrimary, fontFamily = FontFamily.Monospace)
                        }
                    }
                    // Immediate delete (matching this screen's convention: row taps copy with a
                    // toast, no confirm dialogs anywhere), so a toast confirms the removal.
                    // Deleting the entry the Kaspa Node dropdown currently pins does NOT touch
                    // node selection: only the book entry goes, the pinned address stays set
                    // and the dropdown falls back to showing the raw address (see
                    // KaspaNodeQuickAccessSection's selectedLabel).
                    IconButton(onClick = {
                        viewModel.removeSavedNodeAddress(entry.id)
                        nodeBookHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                        // iOS deleteSavedNodeAddress: which of the two cases it was.
                        IosToasts.show(
                            context.getString(
                                if (pinnedNodeAddress.trim().isNotEmpty() && pinnedNodeAddress.trim() == entry.address.trim()) R.string.node_removed_still_connected
                                else R.string.node_address_removed
                            )
                        )
                    }) {
                        Icon(Icons.Default.Delete, null, tint = LocalAppColors.current.textSecondary)
                    }
                }
            }
        }
        SettingsFooter(stringResource(R.string.save_your_own_node_addresses_here))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionSettingsScreen(onBack: () -> Unit, viewModel: ConnectionViewModel = hiltViewModel()) {
    val network by viewModel.network.collectAsState()
    val knsApiUrl by viewModel.knsApiUrl.collectAsState()
    // The .kachat indexer (Domains), editable (iOS wallet 59593af).
    val indexerUrl by viewModel.indexerUrl.collectAsState()
    var editingKachatIndexerUrl by remember { mutableStateOf(false) }
    val kaspaRestApiUrl by viewModel.kaspaRestApiUrl.collectAsState()
    val pushIndexerUrl by viewModel.pushIndexerUrl.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = LocalAppColors.current.background,
        topBar = {
            SettingsSubpageBar(
                title = stringResource(R.string.connection_settings),
                onBack = onBack,
                trailing = { SheetBarButton(stringResource(R.string.save), onClick = onBack) },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // One tab per kind of connection, so the page stays short as the app talks to more
            // services: push registration, the Kaspa node, name services, the block explorer
            // (iOS ee01f81). Every field keeps its value while you switch between them.
            // KaChat Wallet has no Indexer tab (no chats) and no Translation tab (no KaPosts) -
            // iOS wallet 783e13f. Their saved values are untouched: .kachat still reads the message
            // indexer's address, at its default.
            val connectionTabs = listOf(
                stringResource(R.string.connection_tab_push),
                stringResource(R.string.connection_tab_node),
                stringResource(R.string.connection_tab_domains),
                stringResource(R.string.connection_tab_explorer),
            )
            var selectedConnectionTab by androidx.compose.runtime.saveable.rememberSaveable { mutableIntStateOf(0) }
            UnderlineTabBar(
                titles = connectionTabs,
                selectedIndex = selectedConnectionTab,
                onSelect = { selectedConnectionTab = it },
            )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
        ) {
            // Each section spaces itself, its footer under the card (SettingsFormSection).
            Spacer(Modifier.height(16.dp))
            when (selectedConnectionTab) {
            0 -> {
            SettingsFormSection(title = "Push Registration", footer = "Used only for push registration and updates") {
                ConnectionUrlField(label = "Push Indexer URL", value = pushIndexerUrl)
            }

            // Verbose API Logging lives in Settings > Diagnostics, as on iOS.
            }
            1 -> {
            KaspaNodeQuickAccessSection(viewModel)
            Spacer(Modifier.height(24.dp))
            AddressBookSection(viewModel)
            }
            2 -> {
            // .kachat first: it is what every address field resolves before anything else (iOS 6ac48a7).
            // Names are read through this indexer when it serves this network's registry and keeps
            // up, else straight from the chain (KachatNamesRegistry.chooseSource) - editable so
            // anyone can run their own (iOS wallet 59593af), with where names are read from now.
            SettingsFormSection(title = stringResource(R.string.kn_kachat_names), footer = KACHAT_INDEXER_FOOTER) {
                ConnectionUrlField(
                    label = ".kachat Indexer URL",
                    value = indexerUrl,
                    onClick = { editingKachatIndexerUrl = true }
                )
                SettingsDivider()
                KachatNamesSourceRow()
            }
            // The other name services this app reads (see NameServicesClient). Shown, not
            // editable; the .kas URL follows the selected network.
            SettingsFormSection(title = stringResource(R.string.other_domains_title), footer = stringResource(R.string.other_domains_settings_footer)) {
                val isMainnet = network.equals("mainnet", ignoreCase = true)
                ConnectionUrlField(label = ".kas", value = knsApiUrl)
                listOf(com.kachat.app.services.NameServiceTLD.K, com.kachat.app.services.NameServiceTLD.KASPA).forEach { tld ->
                    ConnectionUrlField(
                        label = tld.suffix,
                        value = tld.apiBaseUrl(isMainnet) ?: stringResource(R.string.not_available_on_this_network),
                    )
                }
            }
            }
            else -> {
            SettingsFormSection(title = stringResource(R.string.kaspa_explorer_api), footer = stringResource(R.string.rest_api_for_transaction_history_and)) {
                ConnectionUrlField(label = "Kaspa REST API URL", value = kaspaRestApiUrl)
            }
            }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
        }
    }

    // The .kachat indexer, typed in (iOS wallet 59593af): https only, blank for KaChat's own.
    if (editingKachatIndexerUrl) {
        var draft by remember { mutableStateOf(indexerUrl) }
        var rejected by remember { mutableStateOf(false) }
        com.kachat.app.ui.theme.IosAlertDialog(
            onDismissRequest = { editingKachatIndexerUrl = false },
            containerColor = LocalAppColors.current.surface,
            title = { Text(".kachat Indexer URL", color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    com.kachat.app.ui.theme.IosTextField(
                        value = draft,
                        onValueChange = { draft = it; rejected = false },
                        singleLine = true,
                        isError = rejected,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (rejected) "The URL must start with https://" else "Leave blank for KaChat's own indexer",
                        color = if (rejected) Color(0xFFE57373) else LocalAppColors.current.textSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (viewModel.setKachatIndexerUrl(draft)) editingKachatIndexerUrl = false else rejected = true
                }) { Text("Save", color = KaspaTeal) }
            },
            dismissButton = {
                TextButton(onClick = { editingKachatIndexerUrl = false }) {
                    Text(stringResource(R.string.cancel), color = LocalAppColors.current.textSecondary)
                }
            }
        )
    }
}

/** Settings > Connection > Domains' .kachat footer (iOS wallet 59593af). */
private const val KACHAT_INDEXER_FOOTER =
    "Resolved first, everywhere you can type an address. Leave the indexer blank for KaChat's own, or " +
        "enter one you run yourself. If it can't be reached, serves a different registry or falls " +
        "behind the network, names are read straight from the chain instead, which is slower. " +
        "Buying, selling and claiming always re-check the chain."

/**
 * "Reading names from": the .kachat source KachatNamesRegistry last chose (iOS wallet 59593af
 * `kachatSourceLabel`) - the indexer and its host, or the chain when the indexer isn't usable.
 */
@Composable
private fun KachatNamesSourceRow() {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Reading names from", color = colors.textPrimary, fontSize = 17.sp, modifier = Modifier.weight(1f))
        if (!com.kachat.app.services.kachatnames.KachatNamesService.isLaunched) {
            Text("Not live on this network", color = colors.textSecondary, fontSize = 15.sp, textAlign = TextAlign.End)
            return@Row
        }
        val vm: KachatLiveViewModel = hiltViewModel()
        val source by vm.registry.source.collectAsState()
        val refreshing by vm.registry.isRefreshing.collectAsState()
        val lastError by vm.registry.lastError.collectAsState()
        LaunchedEffect(Unit) { runCatching { vm.registry.refreshIfStale() } }
        Column(horizontalAlignment = Alignment.End) {
            when (val s = source) {
                is com.kachat.app.services.kachatnames.KachatNamesRegistry.Source.Indexer -> {
                    Text("Indexer", color = colors.textSecondary, fontSize = 15.sp)
                    Text(runCatching { java.net.URI(s.base).host }.getOrNull() ?: s.base, color = colors.textSecondary, fontSize = 12.sp)
                }
                com.kachat.app.services.kachatnames.KachatNamesRegistry.Source.Chain -> {
                    Text("The chain", color = colors.textSecondary, fontSize = 15.sp)
                    Text("Indexer not usable right now", color = colors.textSecondary, fontSize = 12.sp)
                }
                null -> if (refreshing) {
                    com.kachat.app.ui.theme.IosActivityIndicator(color = colors.textSecondary, modifier = Modifier.size(18.dp))
                } else {
                    Text(if (lastError == null) "Checking..." else "Couldn't check", color = colors.textSecondary, fontSize = 15.sp)
                }
            }
        }
    }
}

/** Read-only for now — editing these let a mistyped URL crash the whole app (fixed at the NetworkService layer too, but not editable at all is safer). */
@Composable
fun ConnectionUrlField(label: String, value: String, onClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .let { if (onClick != null) it.clickable { onClick() } else it }
            .padding(16.dp)
    ) {
        Text(label, color = LocalAppColors.current.textSecondary, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(6.dp))
        // iOS shows an address in the body font, monospaced.
        Text(
            text = value,
            color = LocalAppColors.current.textPrimary,
            fontSize = 17.sp,
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Composable
fun ConnectionInfoRow(label: String, value: String, valueColor: Color = LocalAppColors.current.textSecondary) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = LocalAppColors.current.textPrimary, style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (label == "Status") {
                Box(modifier = Modifier.size(8.dp).background(valueColor, CircleShape))
                Spacer(Modifier.width(8.dp))
            }
            Text(value, color = valueColor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun PoolStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Spacer(Modifier.width(8.dp))
            Text(label, color = LocalAppColors.current.textPrimary, style = MaterialTheme.typography.bodySmall)
        }
        Text(value, color = color, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ActiveNodeRow(node: NodeInfo) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            // weight(1f) here (not on the status badge) so a long address — real discovered
            // peers can be IPv6 literals like [2601:680:cc80:5630:e1e5:e6fa:c86b:b946]:16111,
            // much longer than the old hardcoded IPv4 seeds — elides instead of squeezing the
            // badge down to near-zero width, which forced its own text to wrap letter by letter.
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.Shield, null, tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(node.ip, color = LocalAppColors.current.textPrimary, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.background(Color(0xFF1E3A1E), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text(node.status, color = Color(node.color), fontSize = 10.sp, maxLines = 1)
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(node.type, color = Color(0xFF2196F3), fontSize = 10.sp)
                Spacer(Modifier.width(8.dp))
                Text(node.latency, color = LocalAppColors.current.warning, fontSize = 10.sp)
            }
            Text("DAA: ${node.daaScore}", color = LocalAppColors.current.textSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun AllNodeRow(node: NodeInfo) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // weight(1f) here (not on the latency/status side) so a long address — real discovered
        // peers can be IPv6 literals like [2601:680:cc80:5630:e1e5:e6fa:c86b:b946]:16111, much
        // longer than the old hardcoded IPv4 seeds — elides instead of squeezing the status
        // badge down to near-zero width, which forced its own text to wrap letter by letter.
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(8.dp).background(Color(node.color), CircleShape))
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.Shield, null, tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(node.ip, color = LocalAppColors.current.textPrimary, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(node.latency, color = LocalAppColors.current.warning, fontSize = 10.sp)
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.background(Color(0x33FF3B30), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                Text(node.status, color = Color(node.color), fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

@Composable
fun rememberQrBitmapPainter(
    content: String,
    size: Int = 512,
    padding: Int = 0
): BitmapPainter {
    val density = LocalDensity.current
    val sizePx = with(density) { size.dp.roundToPx() }
    
    val bitmap = remember(content) {
        if (content.isEmpty()) {
            Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888).asImageBitmap()
        } else {
            val matrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                sizePx,
                sizePx,
                mapOf(EncodeHintType.MARGIN to padding)
            )
            val w = matrix.width
            val h = matrix.height
            // One setPixels() beats width*height individual bounds-checked setPixel() calls
            // (~262k for a 512px QR) by 1-2 orders of magnitude - this runs on the main thread
            // inside remember{}, and the animated (KSPT) variant re-encodes every 2.5s.
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val row = y * w
                for (x in 0 until w) {
                    pixels[row + x] = if (matrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            bitmap.asImageBitmap()
        }
    }
    return BitmapPainter(bitmap)
}

/**
 * Byte-mode QR encoding for raw binary payloads (e.g. a KSPT transaction frame) — ISO-8859-1 maps
 * every byte 1:1 to a char and back, so wrapping [bytes] this way and forcing ZXing's
 * `CHARACTER_SET` hint round-trips the exact bytes through its String-based encoder in byte mode,
 * matching KasSigner's own raw-byte QR encoding (`qrcode::QrCode::new(&[u8])`) on the other end.
 */
@Composable
fun rememberQrBitmapPainter(
    bytes: ByteArray,
    size: Int = 512,
    padding: Int = 0
): BitmapPainter {
    val density = LocalDensity.current
    val sizePx = with(density) { size.dp.roundToPx() }

    val bitmap = remember(bytes) {
        if (bytes.isEmpty()) {
            Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888).asImageBitmap()
        } else {
            val content = String(bytes, Charsets.ISO_8859_1)
            val matrix = QRCodeWriter().encode(
                content,
                BarcodeFormat.QR_CODE,
                sizePx,
                sizePx,
                mapOf(EncodeHintType.MARGIN to padding, EncodeHintType.CHARACTER_SET to "ISO-8859-1")
            )
            val w = matrix.width
            val h = matrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val row = y * w
                for (x in 0 until w) {
                    pixels[row + x] = if (matrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
                }
            }
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bmp.setPixels(pixels, 0, w, 0, 0, w, h)
            bmp.asImageBitmap()
        }
    }
    return BitmapPainter(bitmap)
}

/**
 * Cycles through [frames] at a fixed interval — the display half of KasSigner's animated multi-
 * frame QR protocol (see [com.kachat.app.util.QrFrameChunker]). A single-frame list just renders
 * a static code with no play/pause controls or frame counter.
 */
@Composable
fun AnimatedQrDisplay(frames: List<ByteArray>, modifier: Modifier = Modifier, frameDelayMs: Long = 2500L) {
    var frameIndex by remember(frames) { mutableStateOf(0) }
    // Matches KasSee's own 2.5s auto-advance (kassee/web/js/app.js's displayKsptQr) — a scanning
    // camera needs real time to lock onto and decode each frame; a faster cycle (this used to
    // default to 200ms) skips past frames before the scanner ever catches them.
    var isPlaying by remember(frames) { mutableStateOf(true) }

    LaunchedEffect(frames, isPlaying) {
        if (frames.size <= 1 || !isPlaying) return@LaunchedEffect
        while (true) {
            delay(frameDelayMs)
            frameIndex = (frameIndex + 1) % frames.size
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        val painter = rememberQrBitmapPainter(bytes = frames[frameIndex.coerceIn(frames.indices)])
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .border(2.dp, KaspaTeal, RoundedCornerShape(20.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painter, contentDescription = stringResource(R.string.qr_code_2), modifier = Modifier.fillMaxSize())
        }
        if (frames.size > 1) {
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                frames.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            // Fixed, not theme-based — this always sits on a forced-white card
                            // (see ColdSendFlow), so the inactive dot needs to read against white
                            // specifically rather than whatever the app's own surfaceVariant is.
                            .background(if (i == frameIndex) KaspaTeal else Color(0xFFD0D0D5))
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Manual stepping works whether playing or paused — tapping prev/next doesn't
                // require pausing first, matching KasSee's own frame-nav buttons.
                IconButton(onClick = { frameIndex = (frameIndex - 1 + frames.size) % frames.size }) {
                    Icon(Icons.Default.SkipPrevious, "Previous frame", tint = KaspaTeal)
                }
                IconButton(onClick = { isPlaying = !isPlaying }) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        if (isPlaying) "Pause" else "Play",
                        tint = KaspaTeal
                    )
                }
                IconButton(onClick = { frameIndex = (frameIndex + 1) % frames.size }) {
                    Icon(Icons.Default.SkipNext, "Next frame", tint = KaspaTeal)
                }
                Text("Frame ${frameIndex + 1} / ${frames.size}", color = Color(0xFF6B6B70), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/** How an [AddressQrPage] is closed - iOS puts a different control in its bar for each way it
 *  presents the page. */
enum class QrPageDismiss {
    /** Pushed (Profile): a back chevron, leading. */
    BACK,
    /** In a sheet with Close leading (Chatting Address details). */
    CLOSE,
    /** In a sheet with Done trailing (the Chats New sheet). */
    DONE,
}

/**
 * iOS's `ChattingAddressQRView`: the full white page with an address's QR code - the code in a
 * 3pt accent frame, the whole address under it, what the address is for, and "Tap anywhere to
 * copy" - placed a third of the way down. Its bar shows the address's balance in the title's
 * place (iOS's principal toolbar item, which takes precedence over the navigation title), or "—"
 * while it is unknown, with [dismiss]'s control. A tap anywhere copies the address.
 */
@Composable
fun AddressQrPage(
    address: String,
    balanceSompi: Long?,
    caption: String,
    dismiss: QrPageDismiss,
    onDismiss: () -> Unit,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val haptic = com.kachat.app.util.rememberHaptics()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                clipboardManager.setText(AnnotatedString(address))
                haptic(com.kachat.app.util.IosHaptic.SUCCESS)
                com.kachat.app.util.showAddressCopiedToast(context, address, haptic = false)
            },
    ) {
        AddressQrPageBar(dismiss = dismiss, onDismiss = onDismiss) {
            Text(
                balanceSompi?.let { "%.8f %s".format(java.util.Locale.US, it / 100_000_000.0, KaspaUnit.symbol) } ?: "—",
                // iOS's `.primary`, as iOS draws it: the page forces no colour scheme, so in dark
                // mode the balance is the theme's white over the white page, as on the iPhone.
                color = LocalAppColors.current.textPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                style = androidx.compose.ui.text.TextStyle(fontFeatureSettings = "tnum"),
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    // iOS strokes 3pt on the edge and clips it to the shape: 1.5 shows.
                    .border(1.5.dp, KaspaTeal, RoundedCornerShape(20.dp))
                    .padding(20.dp),
            ) {
                Image(
                    painter = rememberQrBitmapPainter(address),
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier.size(240.dp),
                )
            }
            Text(
                address,
                color = Color.Black,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 28.dp),
            )
            Text(
                caption,
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp),
            )
            Text(
                stringResource(R.string.tap_anywhere_to_copy),
                color = Color.Black.copy(alpha = 0.4f),
                fontSize = 13.sp,
            )
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
        }
    }
}

/** The page's navigation bar: [dismiss]'s control and [center] in the title's place. */
@Composable
private fun AddressQrPageBar(dismiss: QrPageDismiss, onDismiss: () -> Unit, center: @Composable () -> Unit) {
    // A sheet's bar is 56 tall on iOS, a pushed page's 44.
    // iOS 26: each control on its own glass, 16 in from the edge.
    Box(Modifier.fillMaxWidth().height(if (dismiss == QrPageDismiss.BACK) 52.dp else 60.dp).padding(horizontal = 16.dp)) {
        // iOS's glass takes on what is under it: over the white page the control is light glass
        // with a black chevron, in dark mode too. (The balance keeps the theme's colour, below.)
        CompositionLocalProvider(LocalAppColors provides com.kachat.app.ui.theme.LightAppColors) {
            when (dismiss) {
                QrPageDismiss.BACK -> SheetBarBackButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart))
                QrPageDismiss.CLOSE -> SheetBarButton(stringResource(R.string.close), onClick = onDismiss, modifier = Modifier.align(Alignment.CenterStart))
                QrPageDismiss.DONE -> SheetBarButton(stringResource(R.string.done), onClick = onDismiss, modifier = Modifier.align(Alignment.CenterEnd))
            }
        }
        Box(Modifier.align(Alignment.Center)) { center() }
    }
}

/**
 * The chatting address's QR page ([AddressQrPage] with the chatting caption, iOS
 * `ChattingAddressQRView`'s default).
 */
@Composable
fun ChattingAddressQrPage(address: String, balanceSompi: Long?, dismiss: QrPageDismiss, onDismiss: () -> Unit) {
    AddressQrPage(
        address = address,
        balanceSompi = balanceSompi,
        caption = stringResource(R.string.qr_chatting_address_subtitle),
        dismiss = dismiss,
        onDismiss = onDismiss,
    )
}

/**
 * Receive Kaspa's page (iOS `ProfileView.ReceiveKaspaQRView`): a fresh, never-used receive address
 * ([address], resolved by the caller with WalletViewModel.resolveFreshReceiveAddress) on the white
 * QR page, its balance looked up once it is known. Until then the same page says "Preparing a
 * fresh address" under a spinner, on the app's own background - in place, not over everything -
 * or, once the lookup has finished with nothing, that the spending address is still unlocking.
 * Showing an old address for a frame and swapping it is worse, since a QR is scanned the instant
 * it appears. [title] is the bar's title while there is no code (the Chats sheet names it).
 */
@Composable
fun ReceiveKaspaQrPage(
    address: String?,
    resolving: Boolean,
    dismiss: QrPageDismiss,
    onDismiss: () -> Unit,
    walletViewModel: WalletViewModel,
    title: String? = null,
) {
    if (address != null) {
        // A failed lookup leaves the balance unknown ("—") rather than 0 KAS.
        val balanceSompi by produceState<Long?>(initialValue = null, address) {
            value = runCatching {
                val coins = walletViewModel.fetchUtxosForCoinControl(address)
                com.kachat.app.util.UtxoMath.checkedTotal(coins.map { it.utxoEntry.amount })
            }.getOrNull()
        }
        AddressQrPage(
            address = address,
            balanceSompi = balanceSompi,
            caption = stringResource(R.string.qr_receive_address_subtitle),
            dismiss = dismiss,
            onDismiss = onDismiss,
        )
        return
    }
    val colors = LocalAppColors.current
    Column(Modifier.fillMaxSize().background(colors.background)) {
        AddressQrPageBar(dismiss = dismiss, onDismiss = onDismiss) {
            if (title != null) Text(title, color = colors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        }
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            if (resolving) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp),
                ) {
                    com.kachat.app.ui.theme.IosActivityIndicator(color = colors.textSecondary)
                    Text(stringResource(R.string.receive_preparing_fresh_address), color = colors.textSecondary, fontSize = 15.sp)
                }
            } else {
                Text(
                    stringResource(R.string.spending_address_unlocking),
                    color = colors.textSecondary,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

/**
 * A QR page presented the way iOS presents it from a sheet: a full-height sheet of its own over
 * the screen (the Chats New sheet's two, Chatting Address details' code). White while the code
 * shows, so the page reaches the sheet's edges.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrPageSheet(onDismiss: () -> Unit, white: Boolean, content: @Composable () -> Unit) {
    com.kachat.app.ui.theme.IosSheetColors(grouped = true) {
        ModalBottomSheet(
            scrimColor = com.kachat.app.ui.theme.IosSheetScrim,
            shape = com.kachat.app.ui.theme.IosSheetShape,
            tonalElevation = com.kachat.app.ui.theme.IosSheetTonalElevation,
            windowInsets = androidx.compose.foundation.layout.WindowInsets.statusBars,
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = if (white) Color.White else LocalAppColors.current.background,
            dragHandle = null,
        ) {
            // The sheet runs down behind the navigation bar, as iOS's does behind the home indicator;
            // its content stays above it.
            Column(Modifier.navigationBarsPadding()) {
                Box(Modifier.fillMaxSize()) {
                    content()
                    IosToastHost(bottomInsets = SheetToastInsets)
                }
            }
        }
    }
}

/** True while a white page shows over a tab - the dock draws its glass light then. */
object DockOverLightPage {
    var active by mutableStateOf(false)
}

/**
 * A QR page pushed onto a screen (Profile's two): it covers that screen's content in place and
 * slides in from the right, the way iOS pushes it - the dock stays where it is, as iOS's tab bar
 * does - and Back pops it.
 */
@Composable
fun PushedQrPage(visible: Boolean, white: Boolean, onBack: () -> Unit, content: @Composable () -> Unit) {
    // The dock's glass goes light over the white page, as iOS's tab bar does.
    if (visible && white) {
        DisposableEffect(Unit) {
            DockOverLightPage.active = true
            onDispose { DockOverLightPage.active = false }
        }
    }
    androidx.compose.animation.AnimatedVisibility(
        visible = visible,
        enter = androidx.compose.animation.slideInHorizontally { it },
        exit = androidx.compose.animation.slideOutHorizontally { it },
    ) {
        BackHandler(onBack = onBack)
        // The status bar keeps the theme's icons over the white page: iOS's status bar follows
        // the colour scheme (light in dark mode), not what is drawn under it.
        // The page's colour runs up under the status bar, as iOS's white page does - and down
        // under the dock: the tab's content stops above the dock, but iOS's page runs on behind
        // its tab bar, so paint past the bottom edge rather than leave a black band there.
        val pageColor = if (white) Color.White else LocalAppColors.current.background
        Box(
            Modifier.fillMaxSize().drawBehind {
                drawRect(pageColor, size = androidx.compose.ui.geometry.Size(size.width, size.height + 200.dp.toPx()))
            }
        ) {
            // Laid out above the dock, as iOS's page is inside its safe area; the colour runs on under it.
            Box(Modifier.fillMaxSize().statusBarsPadding().padding(bottom = LocalDockInset.current)) { content() }
        }
    }
}

/** Full-bleed QR overlay over the current screen's content area — matches iOS's push-navigated
 *  QR screens (full-screen white, dismissed via a back arrow/system back, never by a stray tap).
 *  Tapping anywhere on the overlay content copies the address — the whole screen is the copy
 *  target, so there's no dedicated copy button, just the quiet hint under the address. [message],
 *  if given, renders as a caption below the code (e.g. funding guidance). */
@Composable
fun QrCodeOverlay(
    value: String,
    onDismiss: () -> Unit,
    message: String? = null,
    borderColor: Color = KaspaTeal,
    borderWidth: Dp = 2.dp,
    /**
     * How many lines the value text may take. Two suits an address; a kpub is roughly twice as
     * long and would be silently truncated, which is worse than wrapping when someone is
     * comparing one by eye.
     */
    valueMaxLines: Int = 2,
    /** "Done" top right instead of "Close" top left - the Chats New sheet's QR pages, which iOS
     *  shows in a sheet of their own with a Done button (e6400d6). */
    dismissAsDone: Boolean = false,
    /** The toast a tap shows, when [value] isn't an address - "kpub copied to clipboard." for
     *  a kpub (iOS ColdStorageView's kpub QR). Null: the standard address toast. */
    copiedToast: String? = null,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val qrCopyHaptic = com.kachat.app.util.rememberHaptics()

    // Its own window, not a Box drawn into the current screen: as inline content, the app's
    // floating dock stayed on top of the code, which is both wrong to look at and something a
    // camera can read the edge of. A Dialog is above everything, and it takes system back.
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
    // Full-screen white, not the app's own themed background — a bright, high-contrast quiet
    // zone around the code is what actually gets a reliable scan on another device's camera,
    // regardless of whether KaChat itself is in light or dark mode.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            // The entire overlay is the copy target (no ripple — the whole screen flashing on
            // tap would look broken). The back arrow below still consumes its own taps, so
            // dismissal stays on the arrow / system back only.
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                clipboardManager.setText(AnnotatedString(value))
                // iOS: every one of these full-screen QR copies plays the success haptic.
                qrCopyHaptic(com.kachat.app.util.IosHaptic.SUCCESS)
                if (copiedToast != null) IosToasts.show(copiedToast)
                else com.kachat.app.util.showAddressCopiedToast(context, value, haptic = false)
            },
        contentAlignment = Alignment.Center
    ) {
        // "Close", not a back arrow: this is a modal you dismiss, not somewhere you navigated
        // to. Same word, same corner as iOS's toolbar button on every one of these.
        SheetBarButton(
            stringResource(if (dismissAsDone) R.string.done else R.string.close),
            onClick = onDismiss,
            modifier = Modifier
                .align(if (dismissAsDone) Alignment.TopEnd else Alignment.TopStart)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val qrPainter = rememberQrBitmapPainter(value)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(borderWidth, borderColor, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = qrPainter,
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = value,
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                maxLines = valueMaxLines,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.tap_anywhere_to_copy),
                color = Color(0xFF9A9AA0),
                style = MaterialTheme.typography.bodySmall
            )
            if (message != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = message,
                    color = Color(0xFF6B6B70),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
    }
}

/** One row in the group-member address list - supports both a raw Kaspa address and a KNS domain, resolved the same way the single-contact flow's address field does. */
/**
 * "Other domains": what the same typed name points to on the other name services, under the name
 * it resolved to, each one selectable (iOS a0dbc15, 6ac48a7 `OtherDomainsDropdown`). Every field
 * that takes an address shows it; the order is always .kachat first. Unregistered, unreachable or
 * not-yet-live ones are listed but not selectable. Opens by itself when nothing was picked but
 * another service has the name.
 */
@Composable
fun OtherDomainsDropdown(
    resolutions: List<com.kachat.app.services.NameResolution>,
    selected: com.kachat.app.services.NameServiceTLD?,
    onPick: (com.kachat.app.services.NameResolution) -> Unit,
) {
    val others = resolutions.filter { it.tld != selected }
    if (others.isEmpty()) return
    val openByItself = selected == null && resolutions.any { it.address != null }
    var expanded by remember(resolutions) { mutableStateOf(openByItself) }
    val colors = LocalAppColors.current
    Column(Modifier.fillMaxWidth().padding(top = 6.dp)) {
        Row(
            modifier = Modifier.clip(RoundedCornerShape(6.dp)).clickable { expanded = !expanded }.padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.other_domains), color = KaspaTeal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = KaspaTeal,
                modifier = Modifier.size(18.dp)
            )
        }
        if (expanded) {
            others.forEach { resolution ->
                val address = resolution.address
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = address != null) {
                            onPick(resolution)
                            expanded = false
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            resolution.display,
                            color = if (address == null) colors.textSecondary else colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        MiddleEllipsisText(
                            address ?: stringResource(
                                when {
                                    resolution.notLive -> R.string.coming_soon
                                    resolution.failed -> R.string.couldnt_check
                                    else -> R.string.not_registered
                                }
                            ),
                            color = colors.textSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                        )
                    }
                    if (address != null) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = KaspaTeal, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

/**
 * The half sheet behind "Address Actions" on Manage Addresses. Mirrors Cold Storage's
 * [ColdStorageAddressActionsSheet], including Address Visibility living here rather than as a
 * toolbar glyph.
 *
 * Discovery reports its position as it walks, and that lands HERE rather than dismissing: a scan
 * is one balance lookup per address until the gap limit is reached, so it runs for a while, and a
 * closed sheet with a toast at the end said nothing about whether anything was happening.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageAddressesActionsSheet(
    isDiscovering: Boolean,
    progress: com.kachat.app.services.SpendingAddressDiscovery.DiscoveryProgress?,
    summary: String?,
    onGenerate: () -> Unit,
    onDiscover: () -> Unit,
    onVisibility: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalAppColors.current
    com.kachat.app.ui.theme.IosSheetColors {
        val colors = LocalAppColors.current
        ModalBottomSheet(
            scrimColor = com.kachat.app.ui.theme.IosSheetScrim,
            shape = com.kachat.app.ui.theme.IosSheetShape,
            tonalElevation = com.kachat.app.ui.theme.IosSheetTonalElevation,
            windowInsets = androidx.compose.foundation.layout.WindowInsets.statusBars,
            // Freely dismissable mid-scan now, by the swipe as well as by the button above: the scan
            // belongs to the ViewModel rather than to the sheet, so closing it abandons nothing - the
            // Address Actions button keeps its spinner and the result still arrives. Holding the
            // sheet open was only ever protecting a progress readout.
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = colors.background,
        ) {
            // The sheet runs down behind the navigation bar, as iOS's does behind the home indicator;
            // its content stays above it.
            Column(Modifier.navigationBarsPadding()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(R.string.address_actions),
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )

                    if (isDiscovering) {
                        Spacer(Modifier.height(8.dp))
                        com.kachat.app.ui.theme.IosActivityIndicator(color = KaspaTeal, strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
                        Text(
                            "Checking address #${progress?.checkingIndex ?: 0}",
                            color = colors.textPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        )
                        Text(
                            if ((progress?.foundCount ?: 0) == 0) "No addresses with a balance or domain yet"
                            else "${progress?.foundCount} found so far",
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                        )
                        Text(
                            "Checks the first thousand addresses whatever the gaps, then keeps going while it keeps finding.",
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(12.dp))
                        // The scan runs in the ViewModel's own scope, not the sheet's, so closing the
                        // sheet does not stop it - it keeps running and reports what it found. Holding
                        // the sheet open for the length of a thousand-address sweep was the only reason
                        // to sit and watch it.
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(28.dp),
                        ) {
                            Text("Close and Keep Scanning", color = KaspaTeal, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(8.dp))
                    } else {
                        ActionSheetRow(
                            icon = Icons.Default.AddCircleOutline,
                            title = stringResource(R.string.generate_new_spending_address),
                            subtitle = "Reveals the next unused address in this wallet.",
                            onClick = onGenerate,
                        )
                        ActionSheetRow(
                            icon = Icons.Default.Search,
                            title = stringResource(R.string.discover_addresses),
                            subtitle = stringResource(R.string.finds_addresses_balance_or_domain),
                            onClick = onDiscover,
                        )
                        ActionSheetRow(
                            icon = Icons.Default.Checklist,
                            title = "Address Visibility",
                            subtitle = "Check off every address you want on the list, in one sitting.",
                            onClick = onVisibility,
                        )
                        if (summary != null) {
                            Text(summary, color = colors.textSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * What a domain transfer is doing right now.
 *
 * The steps come from [WalletViewModel.KnsInscribeUiStatus], which the transfer already reports;
 * this only gives them a place to be seen. The fractions are approximate on purpose: the waits
 * are network-dependent, and a bar that moves in known steps beats a spinner that says nothing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DomainTransferProgressSheet(
    domainName: String,
    status: WalletViewModel.KnsInscribeUiStatus,
) {
    val colors = LocalAppColors.current
    val (label, fraction) = when (status) {
        WalletViewModel.KnsInscribeUiStatus.CHECKING_AVAILABILITY -> "Preparing transfer" to 0.1f
        WalletViewModel.KnsInscribeUiStatus.FETCHING_FEE -> "Working out the fee" to 0.2f
        WalletViewModel.KnsInscribeUiStatus.SUBMITTING_COMMIT -> "Submitting commit transaction" to 0.4f
        WalletViewModel.KnsInscribeUiStatus.SUBMITTING_REVEAL -> "Submitting reveal transaction" to 0.75f
        WalletViewModel.KnsInscribeUiStatus.VERIFYING -> "Confirming the new owner" to 0.95f
        else -> "Working" to 0.1f
    }

    com.kachat.app.ui.theme.IosSheetColors {
        val colors = LocalAppColors.current
        ModalBottomSheet(
            scrimColor = com.kachat.app.ui.theme.IosSheetScrim,
            shape = com.kachat.app.ui.theme.IosSheetShape,
            tonalElevation = com.kachat.app.ui.theme.IosSheetTonalElevation,
            windowInsets = androidx.compose.foundation.layout.WindowInsets.statusBars,
            // Held open for the duration: the work keeps running either way, and a dismissed sheet
            // would leave an on-chain transfer with nothing reporting on it.
            onDismissRequest = {},
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = colors.background,
            dragHandle = null,
        ) {
            // The sheet runs down behind the navigation bar, as iOS's does behind the home indicator;
            // its content stays above it.
            Column(Modifier.navigationBarsPadding()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp, top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Text(
                        "Sending $domainName",
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                    )
                    LinearProgressIndicator(
                        progress = { fraction },
                        color = KaspaTeal,
                        trackColor = colors.surfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Text(label, color = colors.textPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        "A domain transfer is two transactions, so this takes a moment. Keep the app open until it finishes.",
                        color = colors.textSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
