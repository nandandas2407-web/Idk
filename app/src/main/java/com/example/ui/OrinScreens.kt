package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.BuildRecord
import com.example.data.Project
import com.example.data.ProjectFile
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

// Screen Routes Navigation Names
object OrinRoutes {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val MAIN_IDE = "main_ide"
}

// -------------------------------------------------------------
// PERSISTENT CUSTOM APP ICONS (SVG Drawn inside Compose Canvas)
// -------------------------------------------------------------
@Composable
fun OrinLogoSvg(modifier: Modifier = Modifier, pulseScale: Float = 1f) {
    Canvas(modifier = modifier.size(100.dp)) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2, height / 2)

        // Subtle dynamic background purple glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AccentPurple.copy(alpha = 0.25f * pulseScale), Color.Transparent),
                center = center,
                radius = width * 0.75f
            ),
            radius = width * 0.75f
        )

        // Premium Outer hexagon framing
        val hexPath = Path().apply {
            moveTo(width * 0.5f, height * 0.1f)
            lineTo(width * 0.85f, height * 0.3f)
            lineTo(width * 0.85f, height * 0.7f)
            lineTo(width * 0.5f, height * 0.9f)
            lineTo(width * 0.15f, height * 0.7f)
            lineTo(width * 0.15f, height * 0.3f)
            close()
        }
        drawPath(
            path = hexPath,
            color = AccentPurple.copy(alpha = 0.8f),
            style = Stroke(width = 3.dp.toPx())
        )

        // Beautiful stylized inner compilation nodes "O" and "I" integration representation
        val orbitRadius = width * 0.26f
        drawCircle(
            color = Color.White,
            radius = orbitRadius,
            center = center,
            style = Stroke(width = 4.dp.toPx())
        )

        // Solid compilation core pulse
        drawCircle(
            color = AccentPurple,
            radius = 10.dp.toPx() * pulseScale,
            center = center
        )

        // Orbit compiler connection lines
        drawLine(
            color = Color.White.copy(alpha = 0.8f),
            start = Offset(center.x - orbitRadius, center.y),
            end = Offset(center.x + orbitRadius, center.y),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
fun CustomIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.size(20.dp)
    )
}

// -------------------------------------------------------------
// 1. SPLASH SCREEN COMPOSABLE
// -------------------------------------------------------------
@Composable
fun SplashScreen(onNavigateToWelcome: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "SplashLogoGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    var animatedTextVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animatedTextVisible = true
        delay(1800) // Beautiful cinematic entry pause
        onNavigateToWelcome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OrinLogoSvg(modifier = Modifier.size(120.dp), pulseScale = pulse)
            Spacer(modifier = Modifier.height(28.dp))

            AnimatedVisibility(
                visible = animatedTextVisible,
                enter = fadeIn(tween(800)) + expandVertically(tween(800)),
                exit = fadeOut(tween(400))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "O R I N   I D E",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "AI-POWERED MOBILE DEVELOPMENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        color = SecondaryText,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // Subtitle indicator on bottom safe bounds
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = AccentPurple,
                strokeWidth = 2.dp
            )
        }
    }
}

// -------------------------------------------------------------
// 2. WELCOME SCREEN COMPOSABLE
// -------------------------------------------------------------
@Composable
fun WelcomeScreen(
    viewModel: OrinViewModel,
    onNavigateToMainIde: () -> Unit
) {
    val projects by viewModel.projects.collectAsStateWithLifecycle()
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var showAiGenerateDialog by remember { mutableStateOf(false) }
    var showImportRepoDialog by remember { mutableStateOf(false) }

    // Dialog sheets variables
    var newProjectName by remember { mutableStateOf("") }
    var newProjectDesc by remember { mutableStateOf("") }
    var newProjectPkgID by remember { mutableStateOf("com.orin.myapp") }
    var newProjectType by remember { mutableStateOf("Kotlin Android") }

    var aiAppPrompt by remember { mutableStateOf("") }
    var aiAppName by remember { mutableStateOf("") }
    var aiAppType by remember { mutableStateOf("Kotlin Android") }

    var gitRepoUrl by remember { mutableStateOf("") }
    var isImportingGit by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Header Top Bar Hero area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OrinLogoSvg(modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Orin IDE",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                }
                Box(
                    modifier = Modifier
                        .background(AccentPurple.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(1.dp, AccentPurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "V1.0.4 - Sandbox",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Professional landing copy taglines
            Text(
                text = "Build real apps directly from your phone.",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Orin IDE transforms Android into a complete AI-powered development environment. Code. Build. Install standalone APKs.",
                fontSize = 13.sp,
                color = SecondaryText,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Master trigger cards layout (Split Grid style)
            Text(
                text = "NEW WORKSPACE",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = AccentPurple,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                WelcomeActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Create Project",
                    desc = "Initialize boilerplate project",
                    icon = Icons.Default.Add,
                    onClick = {
                        newProjectName = ""
                        newProjectDesc = ""
                        newProjectPkgID = "com.orin.myapp"
                        showCreateDialog = true
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                WelcomeActionCard(
                    modifier = Modifier.weight(1f),
                    title = "AI Generate App",
                    desc = "Code complete app from prompts",
                    icon = Icons.Default.AutoAwesome,
                    isSpecial = true,
                    onClick = {
                        aiAppName = ""
                        aiAppPrompt = ""
                        showAiGenerateDialog = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                WelcomeActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Import GitHub Repo",
                    desc = "Clone remote workspaces",
                    icon = Icons.Default.CloudDownload,
                    onClick = {
                        gitRepoUrl = ""
                        showImportRepoDialog = true
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                WelcomeActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Open Workspace",
                    desc = "Access existing sandboxes",
                    icon = Icons.Default.FolderOpen,
                    onClick = {
                        if (projects.isNotEmpty()) {
                            viewModel.selectProject(projects.first())
                            onNavigateToMainIde()
                        } else {
                            newProjectName = ""
                            newProjectDesc = ""
                            showCreateDialog = true
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Recent Projects block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT WORKSPACES",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = AccentPurple,
                    letterSpacing = 1.5.sp
                )
                if (projects.isNotEmpty()) {
                    Text(
                        text = "${projects.size} projects",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            if (projects.isEmpty()) {
                // Empty state card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, SubtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CustomIcon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "No Projects",
                            tint = SecondaryText,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No sandboxes created",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Create Project' or 'AI Generate App' to begin.",
                            color = SecondaryText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(projects) { project ->
                        ProjectItemRow(
                            project = project,
                            isActive = activeProject?.id == project.id,
                            onClick = {
                                viewModel.selectProject(project)
                                onNavigateToMainIde()
                            },
                            onDelete = {
                                viewModel.deleteProject(project)
                            }
                        )
                    }
                }
            }
        }

        // -----------------------------------------------------------------
        // MODALS (Create, AI Synthesize and GitHub cloning)
        // -----------------------------------------------------------------

        // 1. Create boilerplate Dialog
        if (showCreateDialog) {
            Dialog(onDismissRequest = { showCreateDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NearBlack),
                    border = BorderStroke(1.dp, SubtleBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CustomIcon(Icons.Default.AddBox, "Create Project", tint = AccentPurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Initialize Workspace", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Project Name", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicOutlinedTextField(
                            value = newProjectName,
                            onValueChange = { newProjectName = it },
                            placeholder = "My Awesome App"
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Description", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicOutlinedTextField(
                            value = newProjectDesc,
                            onValueChange = { newProjectDesc = it },
                            placeholder = "A beautiful music and synthesis generator sandbox"
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Package ID", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicOutlinedTextField(
                            value = newProjectPkgID,
                            onValueChange = { newProjectPkgID = it },
                            placeholder = "com.orin.myapp"
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Framework Template", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(6.dp))
                        val types = listOf("Kotlin Android", "React Native", "Flutter", "HTML/CSS/JS")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(types) { t ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (newProjectType == t) AccentPurple.copy(alpha = 0.2f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (newProjectType == t) AccentPurple else SubtleBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { newProjectType = t }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(t, color = if (newProjectType == t) Color.White else SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showCreateDialog = false }) {
                                Text("Cancel", color = SecondaryText)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                shape = RoundedCornerShape(8.dp),
                                enabled = newProjectName.isNotBlank(),
                                onClick = {
                                    viewModel.createProject(
                                        newProjectName,
                                        newProjectType,
                                        newProjectDesc,
                                        newProjectPkgID
                                    )
                                    showCreateDialog = false
                                    onNavigateToMainIde()
                                }
                            ) {
                                Text("Create Workspace", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. AI Generate project dialog
        if (showAiGenerateDialog) {
            Dialog(onDismissRequest = { showAiGenerateDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NearBlack),
                    border = BorderStroke(1.dp, SubtleBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CustomIcon(Icons.Default.AutoAwesome, "AI App Generation", tint = AccentPurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Orin AI Generator", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Application Name", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicOutlinedTextField(
                            value = aiAppName,
                            onValueChange = { aiAppName = it },
                            placeholder = "Music Stream"
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Describe what your app does (AI prompt)", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicOutlinedTextField(
                            value = aiAppPrompt,
                            onValueChange = { aiAppPrompt = it },
                            placeholder = "Create a modern audio streaming application with clean bottom controls and sample local loops"
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Framework Stack", fontSize = 11.sp, color = SecondaryText)
                        Spacer(modifier = Modifier.height(6.dp))
                        val types = listOf("Kotlin Android", "React Native", "Flutter", "HTML/CSS/JS")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(types) { t ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (aiAppType == t) AccentPurple.copy(alpha = 0.2f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (aiAppType == t) AccentPurple else SubtleBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { aiAppType = t }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(t, color = if (aiAppType == t) Color.White else SecondaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showAiGenerateDialog = false }) {
                                Text("Cancel", color = SecondaryText)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                shape = RoundedCornerShape(8.dp),
                                enabled = aiAppName.isNotBlank() && aiAppPrompt.isNotBlank(),
                                onClick = {
                                    viewModel.aiGenerateProject(
                                        name = aiAppName,
                                        type = aiAppType,
                                        prompt = aiAppPrompt,
                                        packageId = "com.orin.${aiAppName.lowercase().replace(" ", "")}"
                                    )
                                    showAiGenerateDialog = false
                                    onNavigateToMainIde()
                                }
                            ) {
                                Text("Execute Synthesis", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Import GitHub repository dialog
        if (showImportRepoDialog) {
            Dialog(onDismissRequest = { if (!isImportingGit) showImportRepoDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NearBlack),
                    border = BorderStroke(1.dp, SubtleBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CustomIcon(Icons.Default.CloudDownload, "GitHub Clone", tint = AccentPurple)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GitHub Workspace Cloner", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        if (!isImportingGit) {
                            Text("Repository URL", fontSize = 11.sp, color = SecondaryText)
                            Spacer(modifier = Modifier.height(4.dp))
                            BasicOutlinedTextField(
                                value = gitRepoUrl,
                                onValueChange = { gitRepoUrl = it },
                                placeholder = "https://github.com/username/project.git"
                            )

                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showImportRepoDialog = false }) {
                                    Text("Cancel", color = SecondaryText)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = gitRepoUrl.startsWith("http"),
                                    onClick = {
                                        coroutineScope.launch {
                                            isImportingGit = true
                                            delay(2000) // Simulated Git cloning progress logs
                                            val name = gitRepoUrl.substringAfterLast("/").substringBefore(".git")
                                            viewModel.createProject(
                                                name = name.ifBlank { "Git Clone app" },
                                                type = "Kotlin Android",
                                                description = "Cloned repo from $gitRepoUrl securely",
                                                packageId = "com.orin.${name.lowercase().replace("-", "")}"
                                            )
                                            isImportingGit = false
                                            showImportRepoDialog = false
                                            onNavigateToMainIde()
                                        }
                                    }
                                ) {
                                    Text("Start Cloning", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Activity log inside dialog loader
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = AccentPurple, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Establishing secure socket connection...", color = Color.White, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Git clone --depth 1 $gitRepoUrl", color = SecondaryText, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeActionCard(
    modifier: Modifier = Modifier,
    title: String,
    desc: String,
    icon: ImageVector,
    isSpecial: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSpecial) NearBlack else DarkCard
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSpecial) AccentPurple.copy(alpha = 0.4f) else SubtleBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(115.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (isSpecial) AccentPurple.copy(alpha = 0.15f) else Color(0xFF1E1E1E),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSpecial) AccentPurple else Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 10.sp,
                    color = SecondaryText,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ProjectItemRow(
    project: Project,
    isActive: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isActive) AccentPurple.copy(alpha = 0.08f) else DarkCard)
            .border(
                1.dp,
                if (isActive) AccentPurple.copy(alpha = 0.3f) else SubtleBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(Color(0xFF1A1A1A), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (project.type) {
                    "Kotlin Android" -> Icons.Default.PhoneAndroid
                    "React Native" -> Icons.Default.Code
                    "Flutter" -> Icons.Default.DeveloperMode
                    else -> Icons.Default.Web
                }
                Icon(icon, contentDescription = project.type, tint = AccentPurple, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = project.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = project.type,
                        fontSize = 10.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.size(3.dp).background(SecondaryText, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = project.category,
                        fontSize = 10.sp,
                        color = AccentPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        IconButton(onDelete, modifier = Modifier.size(30.dp)) {
            CustomIcon(Icons.Default.Delete, "Delete", tint = Color.Gray)
        }
    }
}

@Composable
fun BasicOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.DarkGray, fontSize = 13.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = AccentPurple,
            unfocusedBorderColor = SubtleBorder,
            focusedContainerColor = Color(0xFF030303),
            unfocusedContainerColor = Color(0xFF030303)
        ),
        textStyle = TextStyle(fontSize = 13.sp),
        maxLines = 2,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}

// -------------------------------------------------------------
// 3. MAIN IDE SCREEN PRESENTATION (Responsive split)
// -------------------------------------------------------------
@Composable
fun MainIdeScreen(
    viewModel: OrinViewModel,
    onNavigateBack: () -> Unit
) {
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
    val projectFiles by viewModel.projectFiles.collectAsStateWithLifecycle()
    val activeFile by viewModel.activeFile.collectAsStateWithLifecycle()
    val openTabs by viewModel.openTabs.collectAsStateWithLifecycle()
    val showWebPreview by viewModel.showWebPreview.collectAsStateWithLifecycle()

    // Interactive panels states
    var selectedSideNav by remember { mutableStateOf("explorer") } // explorer, git, compile, ai
    var showBottomTerm by remember { mutableStateOf(true) }
    var activeTermTab by remember { mutableStateOf("terminal") } // terminal, build, stats

    var isAddingFileMode by remember { mutableStateOf(false) }
    var newFileNameInput by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val coroutineScope = rememberCoroutineScope()

    if (activeProject == null) {
        // Fallback placeholder
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = onNavigateBack) { Text("Load Workspace") }
        }
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .statusBarsPadding()
            .navigationBarsPadding(),
        bottomBar = {
            // Main IDE active status info footer bar
            OrinFooterStatusBar(
                activeProject = activeProject,
                activeFile = activeFile,
                showTerm = showBottomTerm,
                toggleTerm = { showBottomTerm = !showBottomTerm }
            )
        }
    ) { innerPadding ->
        // Main split design. Under responsive parameters, we check if portrait mobile
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PureBlack)
        ) {
            val isTablet = maxWidth > 760.dp

            if (isTablet) {
                // WideScreen Canonical split layout (Side panel, editor, terminal, AI Assist side-by-side)
                Row(modifier = Modifier.fillMaxSize()) {
                    // Thin core icons drawer bar
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(50.dp)
                            .background(NearBlack)
                            .border(BorderStroke(1.dp, SubtleBorder))
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.Home, "Welcome Screen", tint = Color.Gray)
                        }
                        Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(SubtleBorder))

                        SideNavIcon(icon = Icons.Default.Folder, label = "explorer", isSelected = selectedSideNav == "explorer") { selectedSideNav = "explorer" }
                        SideNavIcon(icon = Icons.Default.ForkRight, label = "git", isSelected = selectedSideNav == "git") { selectedSideNav = "git" }
                        SideNavIcon(icon = Icons.Default.PlayCircle, label = "compile", isSelected = selectedSideNav == "compile") { selectedSideNav = "compile" }
                        SideNavIcon(icon = Icons.Default.AutoAwesome, label = "ai", isSelected = selectedSideNav == "ai") { selectedSideNav = "ai" }
                    }

                    // Collapsible Side controls panel
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(240.dp)
                            .background(NearBlack)
                            .border(BorderStroke(1.dp, SubtleBorder))
                    ) {
                        SidePanelContent(
                            panelType = selectedSideNav,
                            projectFiles = projectFiles,
                            activeFile = activeFile,
                            viewModel = viewModel,
                            onFileSelect = { viewModel.openFileInEditor(it) },
                            isAddingFile = isAddingFileMode,
                            addFileTrigger = { isAddingFileMode = !isAddingFileMode },
                            newFileName = newFileNameInput,
                            onNewFileNameChange = { newFileNameInput = it },
                            onAddFileFinalize = {
                                if (newFileNameInput.isNotBlank()) {
                                    viewModel.createNewFileInWorkspace(newFileNameInput)
                                    newFileNameInput = ""
                                    isAddingFileMode = false
                                }
                            }
                        )
                    }

                    // Centeral Editor + bottom Terminal stack
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        // Codes view area
                        Box(modifier = Modifier.weight(if (showBottomTerm) 1.5f else 3f)) {
                            CodeEditorWorkSurface(
                                activeFile = activeFile,
                                openTabs = openTabs,
                                editorContent = viewModel.editorContent.value,
                                onContentChange = { viewModel.editorContent.value = it },
                                viewModel = viewModel
                            )
                        }

                        // Terminal block
                        if (showBottomTerm) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .border(BorderStroke(1.dp, SubtleBorder))
                            ) {
                                BuiltInTerminalPanel(
                                    activeTab = activeTermTab,
                                    setTab = { activeTermTab = it },
                                    lines = viewModel.terminalLines.value,
                                    inputText = viewModel.terminalInput.value,
                                    onInputTextChange = { viewModel.terminalInput.value = it },
                                    onSubmitCommand = { viewModel.executeTerminalCommand(it) },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }

                    if (showWebPreview) {
                        WebPreviewPanel(
                            viewModel = viewModel,
                            modifier = Modifier
                                .weight(1.2f)
                                .fillMaxHeight(),
                            onClose = { viewModel.setWebPreviewVisible(false) }
                        )
                    }

                    // Collapsible Orin assistant panel on Right
                    CustomAiAssistantPanel(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(280.dp)
                            .border(BorderStroke(1.dp, SubtleBorder)),
                        history = viewModel.aiChatHistory.value,
                        inputText = viewModel.aiInput.value,
                        onInputTextChange = { viewModel.aiInput.value = it },
                        onSubmitQuery = { viewModel.sendAiChatQuery() },
                        isLoading = viewModel.isAiLoading.value
                    )
                }
            } else {
                // Portrait Touch Optimised layout (Dynamic Tabs navigation on top)
                // Slide/Tabs switcher to view File Explorer, Editor, Terminal or AI
                var mobileTabSelected by remember { mutableStateOf("editor") } // explorer, editor, preview, terminal, ai

                LaunchedEffect(showWebPreview) {
                    if (showWebPreview) {
                        mobileTabSelected = "preview"
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Mobile master navbar switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NearBlack)
                            .border(BorderStroke(1.dp, SubtleBorder))
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.Home, "Welcome", tint = Color.Gray)
                        }
                        MobileNavBarTab(title = "FILES", isSelected = mobileTabSelected == "explorer") { mobileTabSelected = "explorer" }
                        MobileNavBarTab(title = "CODE", isSelected = mobileTabSelected == "editor" || mobileTabSelected == "") { mobileTabSelected = "editor" }
                        MobileNavBarTab(title = "RUN", isSelected = mobileTabSelected == "preview") { mobileTabSelected = "preview" }
                        MobileNavBarTab(title = "SHELL", isSelected = mobileTabSelected == "terminal") { mobileTabSelected = "terminal" }
                        MobileNavBarTab(title = "COPILOT", isSelected = mobileTabSelected == "ai") { mobileTabSelected = "ai" }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (mobileTabSelected) {
                            "explorer" -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(NearBlack)
                                ) {
                                    SidePanelContent(
                                        panelType = "explorer",
                                        projectFiles = projectFiles,
                                        activeFile = activeFile,
                                        viewModel = viewModel,
                                        onFileSelect = {
                                            viewModel.openFileInEditor(it)
                                            mobileTabSelected = "editor"
                                        },
                                        isAddingFile = isAddingFileMode,
                                        addFileTrigger = { isAddingFileMode = !isAddingFileMode },
                                        newFileName = newFileNameInput,
                                        onNewFileNameChange = { newFileNameInput = it },
                                        onAddFileFinalize = {
                                            if (newFileNameInput.isNotBlank()) {
                                                viewModel.createNewFileInWorkspace(newFileNameInput)
                                                newFileNameInput = ""
                                                isAddingFileMode = false
                                                mobileTabSelected = "editor"
                                            }
                                        }
                                    )
                                }
                            }
                            "editor" -> {
                                CodeEditorWorkSurface(
                                    activeFile = activeFile,
                                    openTabs = openTabs,
                                    editorContent = viewModel.editorContent.value,
                                    onContentChange = { viewModel.editorContent.value = it },
                                    viewModel = viewModel
                                )
                            }
                            "preview" -> {
                                WebPreviewPanel(
                                    viewModel = viewModel,
                                    modifier = Modifier.fillMaxSize(),
                                    onClose = {
                                        viewModel.setWebPreviewVisible(false)
                                        mobileTabSelected = "editor"
                                    }
                                )
                            }
                            "terminal" -> {
                                BuiltInTerminalPanel(
                                    activeTab = activeTermTab,
                                    setTab = { activeTermTab = it },
                                    lines = viewModel.terminalLines.value,
                                    inputText = viewModel.terminalInput.value,
                                    onInputTextChange = { viewModel.terminalInput.value = it },
                                    onSubmitCommand = { viewModel.executeTerminalCommand(it) },
                                    viewModel = viewModel
                                )
                            }
                            "ai" -> {
                                CustomAiAssistantPanel(
                                    modifier = Modifier.fillMaxSize(),
                                    history = viewModel.aiChatHistory.value,
                                    inputText = viewModel.aiInput.value,
                                    onInputTextChange = { viewModel.aiInput.value = it },
                                    onSubmitQuery = { viewModel.sendAiChatQuery() },
                                    isLoading = viewModel.isAiLoading.value
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideNavIcon(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .background(
                if (isSelected) AccentPurple.copy(alpha = 0.15f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
    ) {
        Icon(icon, contentDescription = label, tint = if (isSelected) AccentPurple else Color.Gray)
    }
}

@Composable
fun MobileNavBarTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) AccentPurple else Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 2.dp)
                        .background(AccentPurple, CircleShape)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SUB SIDES CONTENT: FILES PATH EXPLORER Tree, Git Diff Checker, SDK Status
// -------------------------------------------------------------
@Composable
fun SidePanelContent(
    panelType: String,
    projectFiles: List<ProjectFile>,
    activeFile: ProjectFile?,
    viewModel: OrinViewModel,
    onFileSelect: (ProjectFile) -> Unit,
    isAddingFile: Boolean,
    addFileTrigger: () -> Unit,
    newFileName: String,
    onNewFileNameChange: (String) -> Unit,
    onAddFileFinalize: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        when (panelType) {
            "explorer" -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FILES SYSTEM",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = addFileTrigger, modifier = Modifier.size(24.dp)) {
                        Icon(
                            if (isAddingFile) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = "New File",
                            tint = AccentPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (isAddingFile) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black, RoundedCornerShape(6.dp))
                            .border(1.dp, SubtleBorder, RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        BasicTextField(
                            value = newFileName,
                            onValueChange = onNewFileNameChange,
                            textStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (newFileName.isEmpty()) {
                                    Text("src/App.kt", color = Color.DarkGray, fontSize = 12.sp)
                                }
                                innerTextField()
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onAddFileFinalize,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier
                                .align(Alignment.End)
                                .height(26.dp)
                        ) {
                            Text("Save", fontSize = 10.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(projectFiles) { file ->
                        val isSelected = activeFile?.filePath == file.filePath
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) AccentPurple.copy(alpha = 0.08f) else Color.Transparent)
                                .clickable { onFileSelect(file) }
                                .padding(horizontal = 8.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (file.filePath.endsWith(".kt")) Icons.Default.Code else Icons.Default.InsertDriveFile,
                                    contentDescription = file.filePath,
                                    tint = if (isSelected) AccentPurple else Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = file.filePath,
                                    color = if (isSelected) Color.White else SecondaryText,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(AccentPurple, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
            "git" -> {
                Text("GIT BRANCH SYNC", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, SubtleBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ForkRight, "branch", tint = AccentPurple, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("On branch: main", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("2 local uncommitted changes ready to cache. Run 'git status' or 'git commit' in compiler terminal.", color = SecondaryText, fontSize = 11.sp)
                    }
                }
            }
            "compile" -> {
                Text("COMPILATION ENGINE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                val isBuilding by viewModel.isBuilding.collectAsStateWithLifecycle()
                val progress by viewModel.buildProgress.collectAsStateWithLifecycle()
                val latestRecord by viewModel.latestRecord.collectAsStateWithLifecycle()

                Button(
                    onClick = { viewModel.triggerApkBuild() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BuildCircle, "build", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBuilding) "Compiling APK..." else "Trigger APK Build")
                    }
                }

                if (isBuilding) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Packaging files to APK...", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AccentPurple,
                        trackColor = SubtleBorder,
                    )
                } else {
                    val record = latestRecord
                    if (record != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ApkReadyCard(record = record)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// APk DEPLOY READY CARD (Installed directly on phone sandbox)
// -------------------------------------------------------------
@Composable
fun ApkReadyCard(record: BuildRecord) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        border = BorderStroke(1.dp, AccentPurple.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(12.dp), ambientColor = AccentPurple)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(AccentPurple.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, "success", tint = ConsoleText, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("APK Ready to Install", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Size: ${record.apkSizeStr}", color = SecondaryText, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = SubtleBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Filename: ${record.apkName}", color = SecondaryText, fontSize = 11.sp, fontFamily = FontFamily.Monospace)

            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { }, // Native installer trigger
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text("Install", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { }, // Share intent
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text("Share", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PREMIUM ENHANCED MONACO CODE EDITOR: Themes, Syntax Highlighting, Utility Keys
// -------------------------------------------------------------

enum class EditorTheme(
    val displayName: String,
    val background: Color,
    val lineNumbersBg: Color,
    val textDefault: Color,
    val border: Color,
    val currentLineBg: Color
) {
    COSMIC_DARK(
        displayName = "Cosmic Dark",
        background = Color(0xFF000000),
        lineNumbersBg = Color(0xFF080808),
        textDefault = Color(0xFFE4E4E7),
        border = Color(0xFF1E1E2F),
        currentLineBg = Color(0xFF13131F)
    ),
    DRACULA(
        displayName = "Dracula",
        background = Color(0xFF282A36),
        lineNumbersBg = Color(0xFF1E2029),
        textDefault = Color(0xFFF8F8F2),
        border = Color(0xFF44475A),
        currentLineBg = Color(0xFF343746)
    ),
    MONOKAI(
        displayName = "Monokai Pro",
        background = Color(0xFF272822),
        lineNumbersBg = Color(0xFF1E1F1C),
        textDefault = Color(0xFFF8F8F2),
        border = Color(0xFF3E3D32),
        currentLineBg = Color(0xFF3E3D32)
    ),
    CLASSIC_LIGHT(
        displayName = "Retro Light",
        background = Color(0xFFFFFFFF),
        lineNumbersBg = Color(0xFFF3F4F6),
        textDefault = Color(0xFF111827),
        border = Color(0xFFE5E7EB),
        currentLineBg = Color(0xFFF3F4F6)
    ),
    MATRIX_STREAM(
        displayName = "Matrix Stream",
        background = Color(0xFF000000),
        lineNumbersBg = Color(0xFF010601),
        textDefault = Color(0xFF39FF14),
        border = Color(0xFF0D320B),
        currentLineBg = Color(0xFF051804)
    )
}

class CodeHighlightTransformation(
    private val language: String,
    private val theme: EditorTheme
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildAnnotatedStringWithHighlighting(text.text, language, theme),
            OffsetMapping.Identity
        )
    }
}

@Composable
fun HelperKeysToolbar(
    onInsertText: (String) -> Unit,
    onIndent: () -> Unit,
    onOutdent: () -> Unit
) {
    val helperKeys = listOf(
        "TAB" to "    ",
        "{" to "{", "}" to "}",
        "(" to "(", ")" to ")",
        "[" to "[", "]" to "]",
        "\"" to "\"", "'" to "'",
        "<" to "<", ">" to ">",
        ":" to ":", ";" to ";",
        "=" to "=", "/" to "/",
        "." to ".", "_" to "_"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0B0B11))
            .border(BorderStroke(1.dp, Color(0xFF181824)))
            .padding(vertical = 4.dp, horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item {
            Button(
                onClick = onIndent,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1B2A)),
                contentPadding = PaddingValues(horizontal = 10.dp),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text("Tab Indent", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        item {
            Button(
                onClick = onOutdent,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1B2A)),
                contentPadding = PaddingValues(horizontal = 10.dp),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text("Outdent", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        items(helperKeys) { (label, value) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14131E)),
                border = BorderStroke(1.dp, Color(0xFF252438)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(28.dp)
                    .clickable { onInsertText(value) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Stateful text cursor insert and indentation helpers
fun insertTextAtCursor(current: TextFieldValue, textToInsert: String): TextFieldValue {
    val start = current.selection.start
    val end = current.selection.end
    val text = current.text
    val newText = text.substring(0, start) + textToInsert + text.substring(end)
    return TextFieldValue(
        text = newText,
        selection = TextRange(start + textToInsert.length)
    )
}

fun indentSelection(current: TextFieldValue): TextFieldValue {
    val text = current.text
    val selection = current.selection
    val start = selection.start
    val end = selection.end

    if (start == end) {
        return insertTextAtCursor(current, "    ")
    }

    val lines = text.lines()
    var charCount = 0
    val selectedIndices = mutableListOf<Int>()

    for (i in lines.indices) {
        val lineLen = lines[i].length + 1
        val lineStart = charCount
        val lineEnd = charCount + lines[i].length

        if ((lineStart <= start && start <= lineEnd) ||
            (lineStart <= end && end <= lineEnd) ||
            (start <= lineStart && lineEnd <= end)) {
            selectedIndices.add(i)
        }
        charCount += lineLen
    }

    val newLines = lines.toMutableList()
    var offsetDelta = 0
    for (idx in selectedIndices) {
        newLines[idx] = "    " + newLines[idx]
        offsetDelta += 4
    }

    val newText = newLines.joinToString("\n")
    return TextFieldValue(
        text = newText,
        selection = TextRange(start + 4, end + offsetDelta)
    )
}

fun outdentSelection(current: TextFieldValue): TextFieldValue {
    val text = current.text
    val selection = current.selection
    val start = selection.start
    val end = selection.end

    val lines = text.lines()
    var charCount = 0
    val selectedIndices = mutableListOf<Int>()

    for (i in lines.indices) {
        val lineLen = lines[i].length + 1
        val lineStart = charCount
        val lineEnd = charCount + lines[i].length

        if ((lineStart <= start && start <= lineEnd) ||
            (lineStart <= end && end <= lineEnd) ||
            (start <= lineStart && lineEnd <= end)) {
            selectedIndices.add(i)
        }
        charCount += lineLen
    }

    val newLines = lines.toMutableList()
    var offsetDelta = 0
    for (idx in selectedIndices) {
        val line = newLines[idx]
        val toRemove = if (line.startsWith("    ")) 4 else if (line.startsWith("\t")) 1 else {
            var count = 0
            while (count < 4 && count < line.length && line[count] == ' ') {
                count++
            }
            count
        }
        if (toRemove > 0) {
            newLines[idx] = line.substring(toRemove)
            offsetDelta += toRemove
        }
    }

    val newText = newLines.joinToString("\n")
    return TextFieldValue(
        text = newText,
        selection = TextRange(
            (start - 4).coerceAtLeast(0),
            (end - offsetDelta).coerceAtLeast(0)
        )
    )
}

@Composable
fun CodeEditorWorkSurface(
    activeFile: ProjectFile?,
    openTabs: List<ProjectFile>,
    editorContent: String,
    onContentChange: (String) -> Unit,
    viewModel: OrinViewModel
) {
    var showInlineAssistBar by remember { mutableStateOf(false) }
    var inlineAssistText by remember { mutableStateOf("") }
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    // Fully custom Code preferences
    var activeTheme by remember { mutableStateOf(EditorTheme.COSMIC_DARK) }
    var fontSizeSp by remember { mutableStateOf(13) }
    var isWordWrapEnabled by remember { mutableStateOf(false) }

    // Dropdown triggers
    var showThemeMenu by remember { mutableStateOf(false) }

    // Search and replace controller state
    var showSearchTray by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var replaceQuery by remember { mutableStateOf("") }
    val matchIndexes = remember { mutableStateListOf<Int>() }
    var currentMatchIndex by remember { mutableStateOf(-1) }

    // STATEFUL TEXT COMPILATION DRIVER (Fixes cursor jumps of BasicTextField)
    var textFieldValueState by remember(activeFile) {
        mutableStateOf(
            TextFieldValue(
                text = editorContent,
                selection = TextRange(0)
            )
        )
    }

    // Capture outside changes (e.g. AI refactors, active open table updates) safely
    LaunchedEffect(editorContent) {
        if (textFieldValueState.text != editorContent) {
            textFieldValueState = textFieldValueState.copy(
                text = editorContent,
                selection = TextRange(editorContent.length.coerceAtMost(textFieldValueState.selection.max))
            )
        }
    }

    // Keep match arrays synchronized
    LaunchedEffect(searchQuery, editorContent) {
        matchIndexes.clear()
        if (searchQuery.isNotEmpty()) {
            var idx = editorContent.indexOf(searchQuery)
            while (idx != -1) {
                matchIndexes.add(idx)
                idx = editorContent.indexOf(searchQuery, idx + 1)
            }
        }
        currentMatchIndex = if (matchIndexes.isNotEmpty()) 0 else -1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(activeTheme.background)
    ) {
        // Tab row showing currently open editors
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF030303))
                .border(BorderStroke(1.dp, SubtleBorder))
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(openTabs) { tabfile ->
                val isSelected = activeFile?.filePath == tabfile.filePath
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) Color(0xFF14141A) else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .border(1.dp, if (isSelected) SubtleBorder else Color.Transparent, RoundedCornerShape(4.dp))
                        .clickable { viewModel.openFileInEditor(tabfile) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = tabfile.filePath.substringAfterLast("/"),
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(
                            onClick = { viewModel.closeTab(tabfile) },
                            modifier = Modifier.size(12.dp)
                        ) {
                            Icon(Icons.Default.Close, "close", tint = Color.Gray, modifier = Modifier.size(10.dp))
                        }
                    }
                }
            }
        }

        if (activeFile == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Code, "Editor Empty", tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No file currently open", color = SecondaryText, fontSize = 13.sp)
                }
            }
            return
        }

        // Expanded Code Toolbar controls (Search, wrap, theme, font)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF07070F))
                .border(BorderStroke(1.dp, SubtleBorder))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.SettingsApplications, "File status", tint = AccentPurple, modifier = Modifier.size(14.dp))
                Text(
                    text = activeFile.filePath,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 140.dp)
                )

                // High contract word-wrapping visual indicator toggle
                IconButton(
                    onClick = { isWordWrapEnabled = !isWordWrapEnabled },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = if (isWordWrapEnabled) Icons.Default.WrapText else Icons.Default.KeyboardTab,
                        contentDescription = "Word wrap",
                        tint = if (isWordWrapEnabled) AccentPurple else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Search Tray expansion trigger toggles
                IconButton(
                    onClick = { showSearchTray = !showSearchTray },
                    modifier = Modifier.size(22.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search & Replace",
                        tint = if (showSearchTray) AccentPurple else Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Theme Selection Picker
                Box {
                    Button(
                        onClick = { showThemeMenu = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14131E)),
                        border = BorderStroke(1.dp, SubtleBorder),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(activeTheme.displayName, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    DropdownMenu(
                        expanded = showThemeMenu,
                        onDismissRequest = { showThemeMenu = false },
                        modifier = Modifier.background(Color(0xFF0F0F15))
                    ) {
                        EditorTheme.values().forEach { theme ->
                            DropdownMenuItem(
                                text = { Text(theme.displayName, color = Color.White, fontSize = 11.sp) },
                                onClick = {
                                    activeTheme = theme
                                    showThemeMenu = false
                                }
                            )
                        }
                    }
                }

                // Font adjustment controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFF14131E), RoundedCornerShape(4.dp))
                        .border(1.dp, SubtleBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 2.dp)
                ) {
                    IconButton(
                        onClick = { fontSizeSp = (fontSizeSp - 1).coerceAtLeast(8) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Text("-", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                    Text(
                        text = "${fontSizeSp}px",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    IconButton(
                        onClick = { fontSizeSp = (fontSizeSp + 1).coerceAtLeast(24) },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Text("+", color = Color.LightGray, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }

                Button(
                    onClick = { showInlineAssistBar = !showInlineAssistBar },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, "ai inline", tint = AccentPurple, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("AI", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = { 
                        viewModel.saveCurrentFile()
                        viewModel.setWebPreviewVisible(true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, "Run", tint = Color.White, modifier = Modifier.size(11.dp))
                        Text("Run", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Button(
                    onClick = { viewModel.saveCurrentFile() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text("Save", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active Search and replace dynamic UI Tray
        if (showSearchTray) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0B0A11))
                    .border(BorderStroke(1.dp, activeTheme.border))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Search term input
                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Black, RoundedCornerShape(4.dp))
                            .border(1.dp, SubtleBorder, RoundedCornerShape(4.dp))
                            .padding(6.dp),
                        decorationBox = { inner ->
                            if (searchQuery.isEmpty()) Text("Search query...", color = Color.Gray, fontSize = 11.sp)
                            inner()
                        }
                    )
                    
                    // Replace term input
                    BasicTextField(
                        value = replaceQuery,
                        onValueChange = { replaceQuery = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Black, RoundedCornerShape(4.dp))
                            .border(1.dp, SubtleBorder, RoundedCornerShape(4.dp))
                            .padding(6.dp),
                        decorationBox = { inner ->
                            if (replaceQuery.isEmpty()) Text("Replace with...", color = Color.Gray, fontSize = 11.sp)
                            inner()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Matches tally
                    val matchCountText = if (matchIndexes.isEmpty()) "No matches" else "${currentMatchIndex + 1}/${matchIndexes.size} matches"
                    Text(
                        text = matchCountText,
                        color = AccentPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = {
                                if (matchIndexes.isNotEmpty()) {
                                    currentMatchIndex = (currentMatchIndex + 1) % matchIndexes.size
                                    val targetIdx = matchIndexes[currentMatchIndex]
                                    textFieldValueState = textFieldValueState.copy(
                                        selection = TextRange(targetIdx, targetIdx + searchQuery.length)
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14131E)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier.height(24.dp),
                            enabled = matchIndexes.isNotEmpty()
                        ) {
                            Text("Find Next", color = Color.LightGray, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                if (currentMatchIndex != -1 && matchIndexes.isNotEmpty()) {
                                    val targetIdx = matchIndexes[currentMatchIndex]
                                    val text = textFieldValueState.text
                                    val newText = text.substring(0, targetIdx) + replaceQuery + text.substring(targetIdx + searchQuery.length)
                                    textFieldValueState = TextFieldValue(
                                        text = newText,
                                        selection = TextRange(targetIdx, targetIdx + replaceQuery.length)
                                    )
                                    onContentChange(newText)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14131E)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier.height(24.dp),
                            enabled = currentMatchIndex != -1
                        ) {
                            Text("Replace", color = Color.LightGray, fontSize = 10.sp)
                        }

                        Button(
                            onClick = {
                                if (searchQuery.isNotEmpty()) {
                                    val newText = textFieldValueState.text.replace(searchQuery, replaceQuery)
                                    textFieldValueState = TextFieldValue(
                                        text = newText,
                                        selection = TextRange(0)
                                    )
                                    onContentChange(newText)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14131E)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            modifier = Modifier.height(24.dp),
                            enabled = searchQuery.isNotEmpty()
                        ) {
                            Text("All", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Animated Expanding inline copilot helper bar
        if (showInlineAssistBar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D0D15))
                    .border(BorderStroke(1.dp, AccentPurple.copy(alpha = 0.3f)))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = inlineAssistText,
                    onValueChange = { inlineAssistText = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Black, RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    decorationBox = { innerTextField ->
                        if (inlineAssistText.isEmpty()) {
                            Text("Ask AI to inline refactor: e.g. 'Add modern glowing gradient card styling'", color = Color.DarkGray, fontSize = 11.sp)
                        }
                        innerTextField()
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inlineAssistText.isNotBlank()) {
                            viewModel.triggerInlineAiCodeOperation(inlineAssistText)
                            inlineAssistText = ""
                            showInlineAssistBar = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                    shape = RoundedCornerShape(4.dp),
                    enabled = !isAiLoading && inlineAssistText.isNotBlank()
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp))
                    } else {
                        Text("Apply", fontSize = 11.sp)
                    }
                }
            }
        }

        // Keyboard Utility accessories Toolbar
        HelperKeysToolbar(
            onInsertText = { charsToAppend ->
                textFieldValueState = insertTextAtCursor(textFieldValueState, charsToAppend)
                onContentChange(textFieldValueState.text)
            },
            onIndent = {
                textFieldValueState = indentSelection(textFieldValueState)
                onContentChange(textFieldValueState.text)
            },
            onOutdent = {
                textFieldValueState = outdentSelection(textFieldValueState)
                onContentChange(textFieldValueState.text)
            }
        )

        // Active highlighted code sheet with Line numbers
        val verticalScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(activeTheme.background)
                .verticalScroll(verticalScrollState)
        ) {
            // Line numbers panel on Left
            val numLines = textFieldValueState.text.lines().size.coerceAtLeast(1)
            Column(
                modifier = Modifier
                    .background(activeTheme.lineNumbersBg)
                    .width(42.dp)
                    .border(BorderStroke(1.dp, activeTheme.border))
                    .padding(vertical = 12.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.End
            ) {
                for (i in 1..numLines) {
                    Text(
                        text = i.toString(),
                        color = Color.DarkGray,
                        fontSize = fontSizeSp.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End
                    )
                }
            }

            // Real Time Regex Syntax Highlighter Editor Panel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(activeTheme.background)
                    .then(
                        if (isWordWrapEnabled) Modifier else Modifier.horizontalScroll(rememberScrollState())
                    )
                    .padding(12.dp)
            ) {
                BasicTextField(
                    value = textFieldValueState,
                    onValueChange = { newValue ->
                        textFieldValueState = newValue
                        onContentChange(newValue.text)
                    },
                    textStyle = TextStyle(
                        color = activeTheme.textDefault,
                        fontFamily = FontFamily.Monospace,
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp + 5).sp
                    ),
                    visualTransformation = CodeHighlightTransformation(activeFile.language, activeTheme),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// Highly stylized dynamic syntax regex highlighting engine supporting multiple editor color themes
fun buildAnnotatedStringWithHighlighting(
    text: String,
    language: String,
    theme: EditorTheme
): AnnotatedString {
    return buildAnnotatedString {
        // Base styling set by theme default
        withStyle(style = SpanStyle(color = theme.textDefault)) {
            append(text)
        }

        val lowerLang = language.lowercase()
        val isLight = theme == EditorTheme.CLASSIC_LIGHT

        // Choose custom color schemes tailored perfectly for light vs dark mode
        val keywordColor = if (isLight) Color(0xFF6D28D9) else if (theme == EditorTheme.DRACULA) Color(0xFFFF79C6) else if (theme == EditorTheme.MONOKAI) Color(0xFFF92672) else Color(0xFFC084FC)
        val stringColor = if (isLight) Color(0xFF047857) else if (theme == EditorTheme.DRACULA) Color(0xFFF1FA8C) else if (theme == EditorTheme.MONOKAI) Color(0xFFE6DB74) else Color(0xFF34D399)
        val commentColor = if (isLight) Color(0xFF6B7280) else if (theme == EditorTheme.DRACULA) Color(0xFF6272A4) else if (theme == EditorTheme.MONOKAI) Color(0xFF75715E) else Color(0xFF52525B)
        val numberColor = if (isLight) Color(0xFF1D4ED8) else if (theme == EditorTheme.DRACULA) Color(0xFFBD93F9) else if (theme == EditorTheme.MONOKAI) Color(0xFFAE81FF) else Color(0xFF38BDF8)
        val typeColor = if (isLight) Color(0xFFB45309) else if (theme == EditorTheme.DRACULA) Color(0xFF8BE9FD) else if (theme == EditorTheme.MONOKAI) Color(0xFF66D9EF) else Color(0xFFFBBF24)
        val selectorColor = if (isLight) Color(0xFFBE185D) else if (theme == EditorTheme.DRACULA) Color(0xFF50FA7B) else if (theme == EditorTheme.MONOKAI) Color(0xFFA6E22E) else Color(0xFFF472B6)

        when {
            lowerLang == "kotlin" || lowerLang == "java" || lowerLang == "dart" -> {
                val keywords = setOf(
                    "val", "var", "fun", "class", "interface", "object", "import", "package",
                    "const", "return", "if", "else", "for", "while", "do", "when", "try",
                    "catch", "finally", "throw", "null", "true", "false", "this", "super",
                    "override", "abstract", "open", "private", "protected", "public",
                    "internal", "lateinit", "init", "enum", "sealed", "data", "companion",
                    "void", "int", "double", "float", "boolean", "char", "long", "short", "byte",
                    "static", "final", "extends", "implements", "instanceof", "new", "switch", "case"
                )

                Regex("[a-zA-Z_][a-zA-Z0-9_]*").findAll(text).forEach { match ->
                    val word = match.value
                    if (keywords.contains(word)) {
                        addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
                    } else if (word.firstOrNull()?.isUpperCase() == true) {
                        addStyle(SpanStyle(color = typeColor), match.range.first, match.range.last + 1)
                    }
                }

                // Strings
                Regex("\".*?\"|'.*?'").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = stringColor), match.range.first, match.range.last + 1)
                }

                // Numbers
                Regex("\\b\\d+(\\.\\d+)?\\b").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = numberColor), match.range.first, match.range.last + 1)
                }

                // Annotations
                Regex("@[a-zA-Z0-9_]+").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = selectorColor), match.range.first, match.range.last + 1)
                }

                // Comments
                Regex("//.*|/\\*[\\s\\S]*?\\*/").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), match.range.first, match.range.last + 1)
                }
            }

            lowerLang == "javascript" || lowerLang == "typescript" -> {
                val keywords = setOf(
                    "const", "let", "var", "function", "class", "extends", "import", "export",
                    "from", "default", "return", "if", "else", "for", "while", "do", "switch",
                    "case", "break", "continue", "try", "catch", "finally", "throw", "null",
                    "undefined", "true", "false", "this", "new", "typeof", "instanceof", "await",
                    "async", "as", "interface", "type", "public", "private", "protected", "readonly",
                    "namespace", "any", "number", "string", "boolean", "unknown", "never"
                )

                Regex("[a-zA-Z_][a-zA-Z0-9_]*").findAll(text).forEach { match ->
                    val word = match.value
                    if (keywords.contains(word)) {
                        addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
                    } else if (word.firstOrNull()?.isUpperCase() == true) {
                        addStyle(SpanStyle(color = typeColor), match.range.first, match.range.last + 1)
                    }
                }

                // Strings
                Regex("\".*?\"|'.*?'|`[\\s\\S]*?`").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = stringColor), match.range.first, match.range.last + 1)
                }

                // Numbers
                Regex("\\b\\d+(\\.\\d+)?\\b").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = numberColor), match.range.first, match.range.last + 1)
                }

                // Comments
                Regex("//.*|/\\*[\\s\\S]*?\\*/").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), match.range.first, match.range.last + 1)
                }
            }

            lowerLang == "python" -> {
                val keywords = setOf(
                    "def", "class", "import", "from", "as", "if", "elif", "else", "for", "while",
                    "return", "try", "except", "finally", "raise", "assert", "with", "lambda",
                    "yield", "pass", "break", "continue", "and", "or", "not", "is", "in", "global",
                    "nonlocal", "del", "None", "True", "False"
                )

                Regex("[a-zA-Z_][a-zA-Z0-9_]*").findAll(text).forEach { match ->
                    val word = match.value
                    if (keywords.contains(word)) {
                        addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
                    } else if (word == "self" || word == "cls") {
                        addStyle(SpanStyle(color = selectorColor), match.range.first, match.range.last + 1)
                    }
                }

                // Strings
                Regex("\".*?\"|'.*?'|\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?'''").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = stringColor), match.range.first, match.range.last + 1)
                }

                // Numbers
                Regex("\\b\\d+(\\.\\d+)?\\b").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = numberColor), match.range.first, match.range.last + 1)
                }

                // Decorators
                Regex("@[a-zA-Z0-9_.]+").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = typeColor), match.range.first, match.range.last + 1)
                }

                // Comments
                Regex("#.*").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), match.range.first, match.range.last + 1)
                }
            }

            lowerLang == "html" || lowerLang == "xml" -> {
                Regex("<[^>]+>").findAll(text).forEach { match ->
                    val inner = match.value
                    val startIdx = match.range.first

                    addStyle(SpanStyle(color = keywordColor), startIdx, startIdx + 1)
                    addStyle(SpanStyle(color = keywordColor), match.range.last, match.range.last + 1)

                    val tagRegex = Regex("</?([a-zA-Z0-9:-]+)")
                    tagRegex.find(inner)?.let { tagMatch ->
                        val grp = tagMatch.groups[1]
                        if (grp != null) {
                            addStyle(
                                SpanStyle(color = selectorColor, fontWeight = FontWeight.SemiBold),
                                startIdx + grp.range.first,
                                startIdx + grp.range.last + 1
                            )
                        }
                    }

                    Regex("\\b([a-zA-Z0-9:-]+)\\s*=").findAll(inner).forEach { attrMatch ->
                        val key = attrMatch.groups[1]
                        if (key != null) {
                            addStyle(
                                SpanStyle(color = typeColor),
                                startIdx + key.range.first,
                                startIdx + key.range.last + 1
                            )
                        }
                    }

                    Regex("\".*?\"|'.*?'").findAll(inner).forEach { valMatch ->
                        addStyle(
                            SpanStyle(color = stringColor),
                            startIdx + valMatch.range.first,
                            startIdx + valMatch.range.last + 1
                        )
                    }
                }

                Regex("<!--[\\s\\S]*?-->").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), match.range.first, match.range.last + 1)
                }
            }

            lowerLang == "css" -> {
                // Properties
                Regex("\\b([a-zA-Z-]+)\\s*:").findAll(text).forEach { match ->
                    val prop = match.groups[1]
                    if (prop != null) {
                        addStyle(SpanStyle(color = typeColor), match.range.first, match.range.first + prop.value.length)
                    }
                }

                // Selectors
                Regex("([.#a-zA-Z0-9-:\\s,>+~*()]+?)\\s*\\{").findAll(text).forEach { match ->
                    val sel = match.groups[1]
                    if (sel != null) {
                        addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.SemiBold), match.range.first, match.range.first + sel.value.length)
                    }
                }

                // Values
                Regex(":\\s*([^;}]+)\\s*;").findAll(text).forEach { match ->
                    val vVal = match.groups[1]
                    if (vVal != null) {
                        addStyle(SpanStyle(color = stringColor), match.range.first + 1, match.range.first + 1 + vVal.value.length)
                    }
                }

                // Comments
                Regex("/\\*[\\s\\S]*?\\*/").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), match.range.first, match.range.last + 1)
                }
            }

            lowerLang == "json" -> {
                // String Keys
                Regex("\".*?\"\\s*:").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.SemiBold), match.range.first, match.range.last)
                }

                // String Values
                Regex(":\\s*\".*?\"").findAll(text).forEach { match ->
                    val relativeStart = match.value.indexOf('"')
                    addStyle(SpanStyle(color = stringColor), match.range.first + relativeStart, match.range.last + 1)
                }

                // Booleans or null
                Regex("\\b(true|false|null)\\b").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = selectorColor), match.range.first, match.range.last + 1)
                }

                // Numbers
                Regex("\\b\\d+(\\.\\d+)?\\b").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = numberColor), match.range.first, match.range.last + 1)
                }
            }

            lowerLang == "markdown" || lowerLang == "md" -> {
                // Headers
                Regex("^#{1,6}\\s+.*$", RegexOption.MULTILINE).findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
                }

                // Inline code
                Regex("`.*?`").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = typeColor, fontFamily = FontFamily.Monospace), match.range.first, match.range.last + 1)
                }

                // Code blocks
                Regex("```[\\s\\S]*?```").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = commentColor, fontFamily = FontFamily.Monospace), match.range.first, match.range.last + 1)
                }

                // Links
                Regex("\\[(.*?)\\]\\((.*?)\\)").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = stringColor, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline), match.range.first, match.range.last + 1)
                }
            }

            lowerLang == "cpp" || lowerLang == "c" -> {
                val keywords = setOf(
                    "auto", "break", "case", "char", "const", "continue", "default", "do",
                    "double", "else", "enum", "extern", "float", "for", "goto", "if", "int",
                    "long", "register", "return", "short", "signed", "sizeof", "static", "struct",
                    "switch", "typedef", "union", "unsigned", "void", "volatile", "while",
                    "class", "namespace", "using", "friend", "template", "typename", "public",
                    "private", "protected", "virtual", "override", "nullptr", "new", "delete",
                    "true", "false", "bool", "operator", "throw", "try", "catch"
                )

                Regex("[a-zA-Z_][a-zA-Z0-9_]*").findAll(text).forEach { match ->
                    val word = match.value
                    if (keywords.contains(word)) {
                        addStyle(SpanStyle(color = keywordColor, fontWeight = FontWeight.Bold), match.range.first, match.range.last + 1)
                    }
                }

                // Preprocessor Directives
                Regex("#\\s*(include|define|undef|ifdef|ifndef|if|else|elif|endif|pragma)\\b.*").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = typeColor), match.range.first, match.range.last + 1)
                }

                // Strings
                Regex("\".*?\"|'.*?'").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = stringColor), match.range.first, match.range.last + 1)
                }

                // Comments
                Regex("//.*|/\\*[\\s\\S]*?\\*/").findAll(text).forEach { match ->
                    addStyle(SpanStyle(color = commentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic), match.range.first, match.range.last + 1)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// BUILT IN NATIVE TERMINAL: Integrated CLI tab, Process performance
// -------------------------------------------------------------
@Composable
fun BuiltInTerminalPanel(
    activeTab: String,
    setTab: (String) -> Unit,
    lines: List<TerminalLine>,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSubmitCommand: (String) -> Unit,
    viewModel: OrinViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Key auto-scroller targeting latest CLI line triggers
    LaunchedEffect(lines.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBlack)
    ) {
        // Tab indicator layouts
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF050505))
                .border(BorderStroke(1.dp, SubtleBorder))
                .padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TerminalTabTitle(label = "Sandbox Terminal", isSelected = activeTab == "terminal") { setTab("terminal") }
                TerminalTabTitle(label = "Compile Output", isSelected = activeTab == "build") { setTab("build") }
                TerminalTabTitle(label = "Device Process", isSelected = activeTab == "stats") { setTab("stats") }
            }

            IconButton(onClick = { viewModel.executeTerminalCommand("clear") }, modifier = Modifier.size(24.dp)) {
                CustomIcon(Icons.Default.Delete, "clear", tint = Color.Gray)
            }
        }

        when (activeTab) {
            "terminal" -> {
                Column(modifier = Modifier.weight(1f)) {
                    // Script command auto suggestions helper row (insanely helpful for mobilers)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF030303))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val suggestions = listOf("ls", "git status", "git diff", "build", "process", "help")
                        items(suggestions) { action ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF161616), RoundedCornerShape(4.dp))
                                    .border(1.dp, SubtleBorder, RoundedCornerShape(4.dp))
                                    .clickable { onSubmitCommand(action) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(action, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = AccentPurple, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Shell log outputs stream
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .padding(12.dp)
                    ) {
                        lines.forEach { line ->
                            val textColor = when (line.type) {
                                "command" -> Color.White
                                "stderr" -> ErrorRed
                                "success" -> ConsoleText
                                "info" -> AccentPurple
                                else -> SecondaryText
                            }
                            Text(
                                text = line.text,
                                color = textColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    // Command Entry Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(BorderStroke(1.dp, SubtleBorder))
                            .background(Color.Black)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("orin@ide ~ $ ", color = AccentPurple, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        BasicTextField(
                            value = inputText,
                            onValueChange = onInputTextChange,
                            textStyle = TextStyle(color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSubmitCommand(inputText) }),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onSubmitCommand(inputText) }, modifier = Modifier.size(24.dp)) {
                            CustomIcon(Icons.Default.Send, "Execute", tint = AccentPurple)
                        }
                    }
                }
            }
            "build" -> {
                val buildLogs by viewModel.buildLogs.collectAsStateWithLifecycle()
                val isBuilding by viewModel.isBuilding.collectAsStateWithLifecycle()

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    if (buildLogs.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No active build running. Run 'build' or tap APK Compile.", color = Color.Gray, fontSize = 11.sp)
                        }
                    } else {
                        buildLogs.forEach { log ->
                            Text(log, color = if (log.contains("SUCCESS", ignoreCase = true)) ConsoleText else Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
            "stats" -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Orin Container Simulator Performance Monitor", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Divider(color = SubtleBorder)
                    Text("  API Framework: Kotlin Android targetSdk 36 COMPLIANT", color = SecondaryText, fontSize = 11.sp)
                    Text("  Native Terminal wrapper sync status: ONLINE", color = ConsoleText, fontSize = 11.sp)
                    Text("  SQLite Database link: ACTIVE Connection Pool", color = ConsoleText, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TerminalTabTitle(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (isSelected) {
                Box(modifier = Modifier.size(width = 12.dp, height = 2.dp).background(AccentPurple, CircleShape))
            }
        }
    }
}

// -------------------------------------------------------------
// RIGHT PANEL: CUSTOM AI CHAT assistant panel
// -------------------------------------------------------------
@Composable
fun CustomAiAssistantPanel(
    modifier: Modifier = Modifier,
    history: List<AiMessage>,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSubmitQuery: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = modifier
            .background(NearBlack)
            .statusBarsPadding()
    ) {
        // AI Title Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF050505))
                .border(BorderStroke(1.dp, SubtleBorder))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, "ai assistant", tint = AccentPurple, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Orin Copilot Assistant", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
        }

        // Chat flow area
        val listState = rememberScrollState()
        LaunchedEffect(history.size) {
            listState.animateScrollTo(listState.maxValue)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(listState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            history.forEach { msg ->
                val isAi = msg.sender == "assistant"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isAi) DarkCard else Color(0xFF1E1E2F)
                        ),
                        border = BorderStroke(1.dp, if (isAi) SubtleBorder else AccentPurple.copy(alpha = 0.2f)),
                        modifier = Modifier.widthIn(max = 240.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isAi) "Orin AI" else "Developer",
                                color = if (isAi) AccentPurple else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.content,
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = BorderStroke(1.dp, SubtleBorder),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = AccentPurple, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Orin AI reasoning...", color = SecondaryText, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Assistant query submit Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, SubtleBorder))
                .background(Color.Black)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = onInputTextChange,
                textStyle = TextStyle(color = Color.White, fontSize = 12.sp),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Black)
                    .padding(8.dp),
                decorationBox = { innerTextField ->
                    if (inputText.isEmpty()) {
                        Text("Ask Orin Copilot...", color = Color.DarkGray, fontSize = 12.sp)
                    }
                    innerTextField()
                }
            )
            IconButton(onClick = onSubmitQuery, enabled = !isLoading && inputText.isNotBlank(), modifier = Modifier.size(32.dp)) {
                CustomIcon(Icons.Default.Send, "send prompt", tint = AccentPurple)
            }
        }
    }
}

// -------------------------------------------------------------
// WEB PREVIEW PANEL (Simulated localhost:3000 Web server engine)
// -------------------------------------------------------------
@Composable
fun WebPreviewPanel(
    viewModel: OrinViewModel,
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val activeProject by viewModel.activeProject.collectAsStateWithLifecycle()
    val projectFiles by viewModel.projectFiles.collectAsStateWithLifecycle()
    val activeFile by viewModel.activeFile.collectAsStateWithLifecycle()
    val previewUrl by viewModel.webPreviewUrl.collectAsStateWithLifecycle()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(previewUrl) }
    var keyOverride by remember { mutableStateOf(0) } // For force-reloading
    var lastLoadedUrl by remember { mutableStateOf("") }

    // Controlled Url Loader to avert recursive recompose rendering loops
    LaunchedEffect(previewUrl, webViewInstance, keyOverride) {
        val webView = webViewInstance
        if (webView != null && previewUrl.isNotEmpty()) {
            webView.loadUrl(previewUrl)
            lastLoadedUrl = previewUrl
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NearBlack)
            .border(BorderStroke(1.dp, SubtleBorder))
    ) {
        // Browser Chrome/Omnibar Header Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF09090F))
                .border(BorderStroke(1.dp, SubtleBorder))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Browser Dot indicators (Safari style)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFEF4444), CircleShape))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFF59E0B), CircleShape))
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Navigation buttons
            IconButton(
                onClick = { webViewInstance?.goBack() },
                modifier = Modifier.size(26.dp)
            ) {
                Icon(Icons.Default.ArrowBack, "Back", tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }

            IconButton(
                onClick = { webViewInstance?.goForward() },
                modifier = Modifier.size(26.dp)
            ) {
                Icon(Icons.Default.ArrowForward, "Forward", tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }

            IconButton(
                onClick = { 
                    keyOverride++ 
                },
                modifier = Modifier.size(26.dp)
            ) {
                Icon(Icons.Default.Refresh, "Refresh", tint = Color.LightGray, modifier = Modifier.size(16.dp))
            }

            // Browser URL bar
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(28.dp)
                    .background(Color.Black, RoundedCornerShape(6.dp))
                    .border(BorderStroke(1.dp, Color(0xFF1F1F2E)), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Lock, "Secure Connection", tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                BasicTextField(
                    value = currentUrl,
                    onValueChange = { currentUrl = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onGo = {
                        viewModel.setWebPreviewUrl(currentUrl)
                        keyOverride++
                    })
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("localhost:3000", color = AccentPurple, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable {
                    currentUrl = "http://localhost:3000/"
                    viewModel.setWebPreviewUrl(currentUrl)
                    keyOverride++
                })
            }

            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(26.dp)
            ) {
                Icon(Icons.Default.Close, "Close Preview", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        // Web Client Rendering Box
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
        ) {
            key(keyOverride) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                            }
                            webViewClient = object : WebViewClient() {
                                override fun shouldInterceptRequest(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): WebResourceResponse? {
                                    val uri = request?.url ?: return null
                                    val host = uri.host
                                    val port = uri.port
                                    
                                    // Match localhost on 3000
                                    if (host == "localhost" && (port == 3000 || port == -1 || port == 80)) {
                                        val path = uri.path ?: "/"
                                        val targetPath = if (path == "/" || path.isBlank()) "index.html" else path.removePrefix("/")
                                        
                                        // Find file in workspace
                                        val matchingFile = projectFiles.find { 
                                            it.filePath.endsWith(targetPath) || 
                                            it.filePath == targetPath || 
                                            it.filePath.substringAfterLast("/") == targetPath 
                                        }
                                        
                                        if (matchingFile != null) {
                                            val mimeType = when {
                                                targetPath.endsWith(".html") -> "text/html"
                                                targetPath.endsWith(".css") -> "text/css"
                                                targetPath.endsWith(".js") -> "application/javascript"
                                                targetPath.endsWith(".json") -> "application/json"
                                                targetPath.endsWith(".png") -> "image/png"
                                                targetPath.endsWith(".jpg") || targetPath.endsWith(".jpeg") -> "image/jpeg"
                                                targetPath.endsWith(".svg") -> "image/svg+xml"
                                                else -> "text/plain"
                                            }
                                            
                                            // Handle live unsaved content for current edited file in memory
                                            val currentActiveFile = activeFile
                                            val fileContent = if (currentActiveFile != null && (currentActiveFile.filePath == matchingFile.filePath)) {
                                                viewModel.editorContent.value
                                            } else {
                                                matchingFile.content
                                            }
                                            
                                            val inputStream = ByteArrayInputStream(fileContent.toByteArray(StandardCharsets.UTF_8))
                                            return WebResourceResponse(mimeType, "UTF-8", inputStream)
                                        }
                                    }
                                    return super.shouldInterceptRequest(view, request)
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    if (url != null) {
                                        currentUrl = url
                                        lastLoadedUrl = url
                                    }
                                }
                            }
                            webViewInstance = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = {}
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STATUS BAR FOOTER COMPOSABLE
// -------------------------------------------------------------
@Composable
fun OrinFooterStatusBar(
    activeProject: Project?,
    activeFile: ProjectFile?,
    showTerm: Boolean,
    toggleTerm: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NearBlack)
            .border(BorderStroke(1.dp, SubtleBorder))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(ConsoleText, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("V-VM Environment Online", color = Color.Gray, fontSize = 10.sp)
            }

            if (activeProject != null) {
                Box(modifier = Modifier.size(width = 1.dp, height = 12.dp).background(SubtleBorder))
                Text("Workspace: ${activeProject.name}", color = SecondaryText, fontSize = 10.sp, maxLines = 1)
            }

            if (activeFile != null) {
                Box(modifier = Modifier.size(width = 1.dp, height = 12.dp).background(SubtleBorder))
                Text("File: ${activeFile.filePath.substringAfterLast("/")}", color = AccentPurple, fontSize = 10.sp, maxLines = 1)
            }
        }

        IconButton(onClick = toggleTerm, modifier = Modifier.size(22.dp)) {
            Icon(
                imageVector = if (showTerm) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = "Toggle sandbox terminal panel",
                tint = Color.Gray
            )
        }
    }
}
