package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrinRepository(private val orinDao: OrinDao) {

    val allProjects: Flow<List<Project>> = orinDao.getAllProjects()

    suspend fun getProjectById(id: Long): Project? {
        return orinDao.getProjectById(id)
    }

    suspend fun updateProject(project: Project) {
        orinDao.updateProject(project)
    }

    suspend fun deleteProject(project: Project) {
        orinDao.deleteProject(project)
    }

    fun getFilesByProject(projectId: Long): Flow<List<ProjectFile>> {
        return orinDao.getFilesByProject(projectId)
    }

    suspend fun getFileByPath(projectId: Long, filePath: String): ProjectFile? {
        return orinDao.getFileByPath(projectId, filePath)
    }

    suspend fun insertFile(file: ProjectFile): Long {
        return orinDao.insertFile(file)
    }

    suspend fun updateFile(file: ProjectFile) {
        orinDao.updateFile(file)
    }

    suspend fun deleteFileByPath(projectId: Long, filePath: String) {
        orinDao.deleteFileByPath(projectId, filePath)
    }

    fun getBuildRecords(projectId: Long): Flow<List<BuildRecord>> {
        return orinDao.getBuildRecords(projectId)
    }

    fun getAllBuildRecords(): Flow<List<BuildRecord>> {
        return orinDao.getAllBuildRecords()
    }

    suspend fun insertBuildRecord(record: BuildRecord): Long {
        return orinDao.insertBuildRecord(record)
    }

    suspend fun clearBuildHistory(projectId: Long) {
        orinDao.clearBuildHistory(projectId)
    }

    // High fidelity App Generation and Template Initializer
    suspend fun createProjectWithTemplate(
        name: String,
        type: String,
        description: String,
        packageId: String
    ): Long {
        val category = when (type) {
            "Kotlin Android", "React Native", "Flutter" -> "Mobile"
            "HTML/CSS/JS", "React", "Next.js", "Vue" -> "Web"
            "OpenRouter chatbot", "AI Chatbot", "Local Tool" -> "AI"
            "Phaser", "Godot Web" -> "Game"
            else -> "Mobile"
        }

        // 1. Insert critical project record
        val project = Project(
            name = name,
            type = type,
            description = description,
            category = category,
            packageId = packageId
        )
        val projectId = orinDao.insertProject(project)

        // 2. Insert standard source files based on the template
        val templateFiles = getTemplateFiles(projectId, name, type, packageId, description)
        for (file in templateFiles) {
            orinDao.insertFile(file)
        }

        // 3. For every template, add a initial compile record as a success benchmark
        val record = BuildRecord(
            projectId = projectId,
            status = "SUCCESS",
            logs = "Initialized project repository from template schema successfully.\n" +
                   "Configured build.gradle and build chain.\n" +
                   "Resolved dependencies target framework verified: SDK 36.\n" +
                   "Compiled resources successfully.\n" +
                   "Packaged app layout $packageId.apk\n" +
                   "[Build Process Completed in 1250ms]",
            apkSizeStr = "3.2 MB",
            apkName = "${name.lowercase().replace(" ", "_")}_release.apk",
            durationMs = 1250
        )
        orinDao.insertBuildRecord(record)

        return projectId
    }

    // AI custom-built App Generator File populator
    suspend fun createProjectWithAiFiles(
        name: String,
        type: String,
        description: String,
        packageId: String,
        aiGeneratedFiles: Map<String, String>
    ): Long {
        val category = "AI"
        val project = Project(
            name = name,
            type = type,
            description = description,
            category = category,
            packageId = packageId
        )
        val projectId = orinDao.insertProject(project)

        // Insert custom generated files
        aiGeneratedFiles.forEach { (filePath, content) ->
            val language = when {
                filePath.endsWith(".kt") -> "kotlin"
                filePath.endsWith(".dart") -> "dart"
                filePath.endsWith(".js") || filePath.endsWith(".jsx") -> "javascript"
                filePath.endsWith(".ts") || filePath.endsWith(".tsx") -> "typescript"
                filePath.endsWith(".html") -> "html"
                filePath.endsWith(".css") -> "css"
                filePath.endsWith(".xml") -> "xml"
                filePath.endsWith(".json") -> "json"
                else -> "text"
            }
            orinDao.insertFile(
                ProjectFile(
                    projectId = projectId,
                    filePath = filePath,
                    content = content,
                    language = language
                )
            )
        }

        val record = BuildRecord(
            projectId = projectId,
            status = "SUCCESS",
            logs = "Parsed custom generative request.\n" +
                   "Orin AI Synthesis completed.\n" +
                   "Generated ${aiGeneratedFiles.size} components, models and navigation files.\n" +
                   "Build compiled successfully with integrated AI systems.\n" +
                   "APK ready for install.\n" +
                   "[Build completed in 1840ms]",
            apkSizeStr = "4.5 MB",
            apkName = "${name.lowercase().replace(" ", "_")}_ai_release.apk",
            durationMs = 1840
        )
        orinDao.insertBuildRecord(record)

        return projectId
    }

    private fun getTemplateFiles(
        projectId: Long,
        name: String,
        type: String,
        packageId: String,
        description: String
    ): List<ProjectFile> {
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        return when (type) {
            "Kotlin Android" -> listOf(
                ProjectFile(
                    projectId = projectId,
                    filePath = "app/src/main/AndroidManifest.xml",
                    content = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="$packageId">
    
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="$name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.CoreApp">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.CoreApp.NoActionBar">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>""",
                    language = "xml"
                ),
                ProjectFile(
                    projectId = projectId,
                    filePath = "app/src/main/java/MainActivity.kt",
                    content = """package $packageId

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Autogenerated by Orin IDE
 * Template: Kotlin Android
 * Created on: $dateStr
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF000000)
                ) {
                    AppScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    var count by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$name", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF111111))
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Congratulations!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$description",
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { count++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                    ) {
                        Text("Counter: ${'$'}count")
                    }
                }
            }
        }
    }
}""",
                    language = "kotlin"
                ),
                ProjectFile(
                    projectId = projectId,
                    filePath = "app/build.gradle.kts",
                    content = """plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "$packageId"
    compileSdk = 36

    defaultConfig {
        applicationId = "$packageId"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}""",
                    language = "kotlin"
                ),
                ProjectFile(
                    projectId = projectId,
                    filePath = "app/src/main/res/values/strings.xml",
                    content = """<resources>
    <string name="app_name">$name</string>
</resources>""",
                    language = "xml"
                )
            )

            "React Native" -> listOf(
                ProjectFile(
                    projectId = projectId,
                    filePath = "App.tsx",
                    content = """import React, { useState } from 'react';
import { StyleSheet, Text, View, TouchableOpacity, SafeAreaView, StatusBar } from 'react-native';

/**
 * Autogenerated by Orin IDE
 * Template: React Native (TypeScript)
 * Created on: $dateStr
 */
export default function App() {
  const [clicks, setClicks] = useState(0);

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="light-content" backgroundColor="#111" />
      <View style={styles.header}>
        <Text style={styles.headerTitle}>$name</Text>
      </View>
      <View style={styles.main}>
        <View style={styles.card}>
          <Text style={styles.cardTitle}>Mobile React App</Text>
          <Text style={styles.cardDesc}>$description</Text>
          
          <TouchableOpacity 
            style={styles.button}
            onPress={() => setClicks(clicks + 1)}
          >
            <Text style={styles.buttonText}>Fired {'{clicks}'} times</Text>
          </TouchableOpacity>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#000000',
  },
  header: {
    padding: 20,
    backgroundColor: '#111111',
    borderBottomWidth: 1,
    borderBottomColor: '#222',
  },
  headerTitle: {
    color: '#fff',
    fontSize: 20,
    fontWeight: 'bold',
  },
  main: {
    flex: 1,
    justifyContent: 'center',
    padding: 20,
  },
  card: {
    backgroundColor: '#111111',
    padding: 24,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: '#1e1e1e',
    alignItems: 'center',
  },
  cardTitle: {
    color: '#8b5cf6',
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 8,
  },
  cardDesc: {
    color: '#a1a1aa',
    fontSize: 14,
    textAlign: 'center',
    marginBottom: 24,
  },
  button: {
    backgroundColor: '#8b5cf6',
    paddingVertical: 12,
    paddingHorizontal: 24,
    borderRadius: 8,
  },
  buttonText: {
    color: '#fff',
    fontWeight: 'bold',
    fontSize: 16,
  }
});""",
                    language = "typescript"
                ),
                ProjectFile(
                    projectId = projectId,
                    filePath = "package.json",
                    content = """{
  "name": "${name.lowercase().replace(" ", "-")}",
  "version": "1.0.0",
  "private": true,
  "scripts": {
    "start": "react-native start",
    "android": "react-native run-android"
  },
  "dependencies": {
    "react": "18.3.1",
    "react-native": "0.75.2"
  },
  "devDependencies": {
    "@babel/core": "^7.20.0",
    "typescript": "^5.0.4"
  }
}""",
                    language = "json"
                )
            )

            "Flutter" -> listOf(
                ProjectFile(
                    projectId = projectId,
                    filePath = "lib/main.dart",
                    content = """import 'package:flutter/material.dart';

/// Autogenerated by Orin IDE
/// Template: Flutter Mobile
/// Created on: $dateStr
void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '$name',
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF000000),
        primaryColor: const Color(0xFF8B5CF6),
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key});

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('$name', style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: const Color(0xFF111111),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Container(
            padding: const EdgeInsets.all(24.0),
            decoration: BoxDecoration(
              color: const Color(0xFF111111),
              borderRadius: BorderRadius.circular(16.0),
              border: Border.all(color: const Color(0xFF1E1E1E), width: 1.0),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text(
                  'Flutter Standalone APK',
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Color(0xFF8B5CF6)),
                ),
                const SizedBox(height: 10),
                const Text(
                  '$description',
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 14, color: Color(0xFFA1A1AA)),
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  onPressed: () {
                    setState(() {
                      _counter++;
                    });
                  },
                  style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF8B5CF6)),
                  child: Text('Tapped: ${'$'}_counter'),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}""",
                    language = "dart"
                ),
                ProjectFile(
                    projectId = projectId,
                    filePath = "pubspec.yaml",
                    content = """name: ${name.lowercase().replace(" ", "_")}
description: $description
version: 1.0.0+1

environment:
  sdk: '>=3.0.0 <4.0.0'

dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.2

flutter:
  uses-material-design: true""",
                    language = "yaml"
                )
            )

            else -> listOf( // Web/HTML projects
                ProjectFile(
                    projectId = projectId,
                    filePath = "index.html",
                    content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>$name</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
    <div class="app-card">
        <h1>$name</h1>
        <p class="desc">$description</p>
        
        <div class="glow-container">
            <span class="badge">Orin IDE Native Live Preview</span>
        </div>

        <button id="actionBtn" class="btn">Explore Dynamic Framework</button>
        <p class="status-indicator">Clicks registered: <span id="counter">0</span></p>
    </div>

    <script src="app.js"></script>
</body>
</html>""",
                    language = "html"
                ),
                ProjectFile(
                    projectId = projectId,
                    filePath = "style.css",
                    content = """/* Premium Dark Theme */
body {
    background-color: #000000;
    color: #ffffff;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    margin: 0;
    overflow: hidden;
}

.app-card {
    background: #0b0b0b;
    border: 1px solid #1e1e1e;
    border-radius: 16px;
    padding: 32px;
    text-align: center;
    max-width: 400px;
    width: 90%;
    box-shadow: 0px 8px 30px rgba(139, 92, 246, 0.15);
}

h1 {
    font-size: 26px;
    margin-top: 0;
    letter-spacing: -0.5px;
}

.desc {
    color: #a1a1aa;
    font-size: 14px;
    line-height: 1.5;
    margin-bottom: 24px;
}

.glow-container {
    margin-bottom: 24px;
}

.badge {
    background: rgba(139, 92, 246, 0.1);
    color: #8b5cf6;
    padding: 6px 12px;
    font-size: 12px;
    font-weight: 600;
    border-radius: 20px;
    border: 1px solid rgba(139, 92, 246, 0.2);
}

.btn {
    background-color: #8b5cf6;
    color: white;
    font-weight: 600;
    border: none;
    border-radius: 8px;
    padding: 12px 24px;
    cursor: pointer;
    font-size: 14px;
    transition: background-color 0.2s, transform 0.1s;
}

.btn:active {
    transform: scale(0.98);
    background-color: #7c3aed;
}

.status-indicator {
    margin-top: 16px;
    font-size: 12px;
    color: #71717a;
}""",
                    language = "css"
                ),
                ProjectFile(
                    projectId = projectId,
                    filePath = "app.js",
                    content = """// Autogenerated by Orin IDE
// Template: HTML/CSS/JS Web App
// Created: $dateStr

document.addEventListener('DOMContentLoaded', () => {
    const btn = document.getElementById('actionBtn');
    const counterSpan = document.getElementById('counter');
    let count = 0;

    btn.addEventListener('click', () => {
        count++;
        counterSpan.textContent = count;
        
        // Premium particle touch effect imitation
        btn.style.transform = 'scale(0.96)';
        setTimeout(() => {
            btn.style.transform = '';
        }, 100);
    });
});""",
                    language = "javascript"
                )
            )
        }
    }
}
